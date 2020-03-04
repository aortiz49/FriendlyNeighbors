package co.edu.uniandes.csw.neighborhood.test.logic;


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
//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.ArrayList;
import java.util.List;
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
 * Runs tests for NeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class NeighborhoodLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Injects NeighborhoodLogic objects.
     */
    @Inject
    private NeighborhoodLogic neighborhoodLogic;

    /**
     * Entity manager to communicate with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    private UserTransaction utx;

    /**
     * An array containing the set of data used for the tests.
     */
    private List<NeighborhoodEntity> data = new ArrayList<>();

    /**
     *
     * Configures the test.
     *
     * @return the jar that Arquillian will deploy on the embedded Payara server. It contains the
     * classes, the database descriptor, and the beans.xml to resolve injection dependencies.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NeighborhoodEntity.class.getPackage())
                .addPackage(NeighborhoodLogic.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }
//===================================================
// Test Setup
//===================================================
    /**
     * Initial test configuration.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
        em.createQuery("delete from BusinessEntity").executeUpdate();
        em.createQuery("delete from LocationEntity").executeUpdate();
        em.createQuery("delete from GroupEntity").executeUpdate();
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {

        // creates the five neighborhoods with empty residents, groups, and businesses, and locations 
        for (int i = 0; i < 5; i++) {
            NeighborhoodEntity entity = factory.manufacturePojo(NeighborhoodEntity.class);
            em.persist(entity);
            entity.setResidents(new ArrayList<>());
            entity.setBusinesses(new ArrayList<>());
            entity.setPlaces(new ArrayList<>());
            entity.setGroups(new ArrayList<>());

            data.add(entity);
        }

        // in the first neighborhood, create a resident and add it to the neighborhood
        NeighborhoodEntity neighborhood0 = data.get(0);
        ResidentProfileEntity withResident = factory.manufacturePojo(ResidentProfileEntity.class);
        withResident.setNeighborhood(neighborhood0);
        em.persist(withResident);
        neighborhood0.getResidents().add(withResident);

        // in the second neighborhood, create a business and add it to the neighborhood
        NeighborhoodEntity neighborhood1 = data.get(1);
        BusinessEntity withBusiness = factory.manufacturePojo(BusinessEntity.class);
        withBusiness.setNeighborhood(neighborhood1);
        em.persist(withBusiness);
        neighborhood1.getBusinesses().add(withBusiness);

        // in the third neighborhood, create a location and add it to the neighborhood
        NeighborhoodEntity neighborhood2 = data.get(2);
        LocationEntity withLocation = factory.manufacturePojo(LocationEntity.class);
        withLocation.setNeighborhood(neighborhood2);
        em.persist(withLocation);
        neighborhood2.getPlaces().add(withLocation);

        // in the fourth neighborhood, create a group and add it to the neighborhood
        NeighborhoodEntity neighborhood3 = data.get(3);
        GroupEntity withGroup = factory.manufacturePojo(GroupEntity.class);
        withGroup.setNeighborhood(neighborhood3);
        em.persist(withLocation);
        neighborhood3.getGroups().add(withGroup);

    }
//===================================================
// Tests
//===================================================
    /**
     * Tests the creation of a Neighborhood.
     */
    @Test
    public void createNeighborhoodTest() throws BusinessLogicException {

        // creates a random neighborhood
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        // persist the created neighborhood, should not be null
        NeighborhoodEntity result = neighborhoodLogic.createNeighborhood(newEntity);
        Assert.assertNotNull(result);

        // locate the persisted neighborhood
        NeighborhoodEntity entity = em.find(NeighborhoodEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Tests the creation of a Neighborhood with NULL name.
     */
    @Test(expected = BusinessLogicException.class)
    public void createNeighborhoodWithNullNameTest() throws BusinessLogicException {

        // creates a random neighborhood
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        // set the name of the entity to null
        newEntity.setName(null);

        // persist the created neighborhood, should not be null
        NeighborhoodEntity result = neighborhoodLogic.createNeighborhood(newEntity);

    }

    /**
     * Tests the creation of a Neighborhood with existing name.
     */
    @Test(expected = BusinessLogicException.class)
    public void createNeighborhoodWithExistingNameTest() throws BusinessLogicException {

        // creates a random neighborhood
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        // set the name of the entity to a name that exists already
        newEntity.setName(data.get(0).getName());

        // persist the created neighborhood, should not be null
        NeighborhoodEntity result = neighborhoodLogic.createNeighborhood(newEntity);

    }

    /**
     * Returns all the neighborhoods in the database.
     *
     * @return list of neighborhoods
     */
    @Test
    public void getNeighborhoodsTest() {
        List<NeighborhoodEntity> list = neighborhoodLogic.getNeighborhoods();
        Assert.assertEquals(data.size(), list.size());
        for (NeighborhoodEntity entity : list) {
            boolean found = false;
            for (NeighborhoodEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Finds a neighborhood by Id.
     *
     * @return the found neighborhood, null if not found
     */
    @Test
    public void getNeighborhoodTest() {
        NeighborhoodEntity entity = data.get(0);
        NeighborhoodEntity resultEntity = neighborhoodLogic.getNeighborhood(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Finds a neighborhood by name.
     *
     * @return the found neighborhood, null if not found
     */
    @Test
    public void getNeighborhoodByNameTest() {
        NeighborhoodEntity entity = data.get(0);
        NeighborhoodEntity resultEntity = neighborhoodLogic.getNeighborhoodByName(entity.getName());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Tests to update a neighborhood.
     */
    @Test
    public void updateNeighborhoodTest() throws BusinessLogicException {
        // get first neighborhood from generated data set
        NeighborhoodEntity entity = data.get(0);

        // generate a random neighborhood
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        // set the id of the random neighborhood to the id of the first one from the data set
        newEntity.setId(entity.getId());

        // update the neighborhood with the new information
        neighborhoodLogic.updateNeighborhood(entity.getId(), newEntity);

        // find the neighborhood in the database
        NeighborhoodEntity resp = em.find(NeighborhoodEntity.class, entity.getId());

        // make sure they are equal
        Assert.assertEquals(newEntity.getId(), resp.getId());
        Assert.assertEquals(newEntity.getName(), resp.getName());
    }

    /**
     * Tests the deletion of a neighborhood.
     *
     */
    @Test
    public void deleteNeighborhoodTest() throws BusinessLogicException {
        // get first neighborhood from generated data set
        NeighborhoodEntity neighborhoodEntity = data.get(4);

        // delete the neighborhood
        neighborhoodLogic.deleteNeighborhood(neighborhoodEntity.getId());

        // the deleted neighborhood will not be found in the database
        NeighborhoodEntity deleted = em.find(NeighborhoodEntity.class, neighborhoodEntity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * The deletion of a neighborhood with a residence.
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteNeighborhoodWithResidentTest() throws BusinessLogicException {
        neighborhoodLogic.deleteNeighborhood(data.get(0).getId());
    }

    /**
     * The deletion of a neighborhood with a business.
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteNeighborhoodWithBusinessTest() throws BusinessLogicException {
        neighborhoodLogic.deleteNeighborhood(data.get(1).getId());
    }

    /**
     * The deletion of a neighborhood with a location.
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteNeighborhoodWithLocationTest() throws BusinessLogicException {
        neighborhoodLogic.deleteNeighborhood(data.get(2).getId());
    }

    /**
     * The deletion of a neighborhood with a group.
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteNeighborhoodWithGroupTest() throws BusinessLogicException {
        neighborhoodLogic.deleteNeighborhood(data.get(3).getId());
    }

}
