/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private EventLogic eventLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    @Inject
    private LocationPersistence locationPersistence;

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

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
    private List<EventEntity> data = new ArrayList<>();

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(EventLogic.class.getPackage())
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
        em.createQuery("delete from EventEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        LocationEntity location = factory.manufacturePojo(LocationEntity.class);
        em.persist(location);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);

        residentPersistence.create(resident);

        for (int i = 0; i < 3; i++) {

            EventEntity entity = factory.manufacturePojo(EventEntity.class);

            entity.setHost(resident);
            entity.setLocation(location);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a event
     */
    @Test
    public void createEventTest() {

        NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);

        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);

        LocationEntity location = factory.manufacturePojo(LocationEntity.class);

        locationPersistence.create(location);

        resident.setNeighborhood(neigh);

        residentPersistence.create(resident);

        // uses the factory to create a ranbdom NeighborhoodEntity object
        EventEntity newEvent = factory.manufacturePojo(EventEntity.class);
        newEvent.setHost(resident);
        newEvent.setLocation(location);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        EventEntity result = null;
        try {
            result = eventLogic.createEvent(newEvent,resident.getId(), neigh.getId());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(EventLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        EventEntity entity
                = em.find(EventEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newEvent.getDescription(), entity.getDescription());

    }

    /**
     * Test for getting ll events
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> list = eventLogic.getEvents(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (EventEntity entity : list) {
            boolean found = false;
            for (EventEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting  event
     */
    @Test
    public void getEventTest() {
        EventEntity entity = data.get(0);
        EventEntity resultEntity = eventLogic.getEvent(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
    }

    /**
     * Test for updating a event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void updateEventTest() throws BusinessLogicException {

        EventEntity entity = data.get(0);
        EventEntity pojoEntity = factory.manufacturePojo(EventEntity.class);
        pojoEntity.setId(entity.getId());
        eventLogic.updateEvent(pojoEntity, neighborhood.getId());
        EventEntity resp = em.find(EventEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getDescription(), resp.getDescription());

    }

    /**
     * Test for deleting a event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void deleteEventTest() throws BusinessLogicException {
        EventEntity entity = data.get(1);
        eventLogic.deleteEvent(entity.getId(), neighborhood.getId());
        EventEntity deleted = em.find(EventEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Finds an event by title.
     *
     * @return the found event, null if not found
     */
    @Test
    public void getEventByNameTest() {
        EventEntity entity = data.get(0);
        EventEntity resultEntity = eventLogic.getEventByTitle(entity.getTitle());

        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
        Assert.assertEquals(entity.getAvailability(), resultEntity.getAvailability());
        Assert.assertEquals(entity.getStartTime(), resultEntity.getStartTime());
        Assert.assertEquals(entity.getEndTime(), resultEntity.getEndTime());
    }

    /**
     * Tests the creation of an Event with no location.
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoLocationTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // persist the created event, should not be null
        EventEntity result = eventLogic.createEvent(newEntity, resident.getId(), neighborhood.getId());
    }

    /**
     * Tests the creation of an Event with non-existent location.
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNonExistentLocationTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // change location id to one that doesn't exist in the system
        LocationEntity loc = data.get(0).getLocation();
        loc.setId(11123L);
        newEntity.setLocation(loc);

        // persist the created business, should not finish
        EventEntity result = eventLogic.createEvent(newEntity, resident.getId(), neighborhood.getId());
    }
}
