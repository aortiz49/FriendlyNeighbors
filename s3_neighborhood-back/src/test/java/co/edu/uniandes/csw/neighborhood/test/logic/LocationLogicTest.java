/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
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

        for (int i = 0; i < 3; i++) {
           
            LocationEntity entity
                    = factory.manufacturePojo(LocationEntity.class);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    @Test
    public void createLocationTest() {
        
         // uses the factory to create a ranbdom NeighborhoodEntity object
        LocationEntity newLocation = factory.manufacturePojo(LocationEntity.class);
                
              
        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        LocationEntity result = null;
            try {
                result = locationLogic.createLocation(newLocation);
            } catch (BusinessLogicException ex) {
                Logger.getLogger(LocationLogicTest.class.getName()).log(Level.SEVERE, null, ex);
            }

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        LocationEntity entity
                = em.find(LocationEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newLocation.getAddress(), entity.getAddress());
        
       
}
    
 

    @Test
    public void getLocationsTest() {
        List<LocationEntity> list = locationLogic.getLocations();
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
        LocationEntity resultEntity = locationLogic.getLocation(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getAddress(), resultEntity.getAddress());
    }


    @Test
    public void updateLocationTest() throws BusinessLogicException {
  
            LocationEntity entity = data.get(0);
            LocationEntity pojoEntity = factory.manufacturePojo(LocationEntity.class);
            pojoEntity.setId(entity.getId());
            locationLogic.updateLocation(pojoEntity);
            LocationEntity resp = em.find(LocationEntity.class, entity.getId());
            Assert.assertEquals(pojoEntity.getId(), resp.getId());
            Assert.assertEquals(pojoEntity.getAddress(), resp.getAddress());

    }


    @Test
    public void deleteLocationTest() throws BusinessLogicException {
        LocationEntity entity = data.get(1);
        locationLogic.deleteLocation(entity.getId());
        LocationEntity deleted = em.find(LocationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}