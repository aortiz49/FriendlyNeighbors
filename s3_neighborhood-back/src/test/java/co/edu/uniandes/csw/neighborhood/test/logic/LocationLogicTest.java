/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class LocationLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private LocationLogic locationLogic;

    /**
     * The entity manager that will verify data directly with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    UserTransaction utx;

    /**
     * An array containing the set of data used for the tests.
     */
    private List<LocationEntity> data = new ArrayList<>();

    private NeighborhoodEntity neighborhood;

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(LocationEntity.class.getPackage())
                .addPackage(LocationLogic.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Initial test configuration that will run before each test.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            em.joinTransaction();

            // clears the data in the database directly using the EntityManager
            // and UserTransaction
            clearData();

            // creates the new data
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Clears tables involved in tests
     */
    private void clearData() {
        em.createQuery("delete from LocationEntity").executeUpdate();
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setName("West Park Village");
        em.persist(neighborhood);

        for (int i = 0; i < 3; i++) {

            LocationEntity entity
                    = factory.manufacturePojo(LocationEntity.class);
            entity.setNeighborhood(neighborhood);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);

            // add the locations to the neighborhood
            neighborhood.getPlaces().add(data.get(i));

        }
    }

    @Test
    public void createLocationTest() throws BusinessLogicException {

        // uses the factory to create a ranbdom NeighborhoodEntity object
        LocationEntity newLocation = factory.manufacturePojo(LocationEntity.class);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        newLocation.setNeighborhood(neighborhood);
        newLocation.setOpenTime("08:30 AM");
        newLocation.setCloseTime("08:30 PM");

        newLocation = locationLogic.createLocation(neighborhood.getId(), newLocation);

        // verify that the created object is not null
        Assert.assertNotNull(newLocation);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        LocationEntity entity
                = em.find(LocationEntity.class, newLocation.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newLocation.getAddress(), entity.getAddress());
        Assert.assertEquals(newLocation.getLatitude(), entity.getLatitude(), 0.0001);
        Assert.assertEquals(newLocation.getLongitude(), entity.getLongitude(), 0.0001);
        Assert.assertEquals(newLocation.getCloseTime(), entity.getCloseTime());
        Assert.assertEquals(newLocation.getOpenTime(), entity.getOpenTime());

    }

    @Test
    public void getLocationsTest() {
        
        List<LocationEntity> list = locationLogic.getLocations(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (LocationEntity entity : list) {
            boolean found = false;
            for (LocationEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    @Test
    public void getLocationTest() {
        LocationEntity entity = data.get(0);
        LocationEntity resultEntity = locationLogic.getLocation(neighborhood.getId(), entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getAddress(), resultEntity.getAddress());
    }

    @Test
    public void updateLocationTest() throws BusinessLogicException {

        LocationEntity entity = data.get(0);
        LocationEntity pojoEntity = factory.manufacturePojo(LocationEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setNeighborhood(neighborhood);
        pojoEntity.setOpenTime("10:34 AM");
        pojoEntity.setCloseTime("08:30 PM");
        
        locationLogic.updateLocation(neighborhood.getId(), pojoEntity);
        LocationEntity resp = em.find(LocationEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getAddress(), resp.getAddress());

    }

    @Test
    public void deleteLocationTest() throws BusinessLogicException {
        LocationEntity entity = data.get(1);
        locationLogic.deleteLocation(neighborhood.getId(), entity.getId());
        LocationEntity deleted = em.find(LocationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}
