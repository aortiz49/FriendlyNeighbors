/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
 * Tests the event logic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory to create random java objects.
     */
    private PodamFactory factory;

    /**
     * Injected event logic dependencies for the tests.
     */
    @Inject
    private EventLogic eventLogic;

    /**
     * The host's neighborhood.
     */
    private NeighborhoodEntity neighborhood;

    /**
     * The event's host.
     */
    private ResidentProfileEntity host;

    /**
     * The event's location.
     */
    private LocationEntity location;

    /**
     * The entity manager that will verify data directly with the database.
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
    private final List<EventEntity> data = new ArrayList<>();

//===================================================
// SetUp
//===================================================
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
        } catch (IllegalStateException | SecurityException | HeuristicMixedException
                | HeuristicRollbackException | NotSupportedException | RollbackException
                | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException e1) {
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
        // creates a factory to generate java objects
        factory = new PodamFactoryImpl();

        // creates a random neighborhood
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setName("Inticaya");
        em.persist(neighborhood);

        // creates a random host
        host = factory.manufacturePojo(ResidentProfileEntity.class);
        host.setName("Andy Ortiz");
        host.setNeighborhood(neighborhood);
        em.persist(host);

        // creates a random location
        location = factory.manufacturePojo(LocationEntity.class);
        location.setName("Parque 93");
        location.setNeighborhood(neighborhood);
        em.persist(location);

        for (int i = 0; i < 3; i++) {

            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            entity.setHost(host);
            entity.setLocation(location);

            em.persist(entity);
            data.add(entity);
        }
    }

//===================================================
// Tests
//===================================================
    /**
     * Test for creating a event
     *
     * @throws BusinessLogicException is violates creation rules
     */
    @Test
    public void createEventTest() throws BusinessLogicException {

        // creates a random event
        EventEntity newEvent = factory.manufacturePojo(EventEntity.class);

        // sets the host of the event
        newEvent.setHost(host);

        // sets the location of the event
        newEvent.setLocation(location);

        // sets the title of the event
        newEvent.setTitle("Karen's birthday party!");

        // set event date
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 8, 10);
        Date eventDate = cal.getTime();

        // set post date
        cal.set(2020, 6, 18);
        Date postDate = cal.getTime();

        // set event/post dates
        newEvent.setDateOfEvent(eventDate);
        newEvent.setDatePosted(postDate);

        // set start/end times
        newEvent.setStartTime("08:33 AM");
        newEvent.setEndTime("08:33 PM");

        // create the event
        EventEntity created = eventLogic.createEvent(neighborhood.getId(), host.getId(),
                location.getId(), newEvent);

        Assert.assertEquals(newEvent.getId(), created.getId());
        Assert.assertEquals(newEvent.getTitle(), created.getTitle());
        Assert.assertEquals(newEvent.getDatePosted(), created.getDatePosted());
        Assert.assertEquals(newEvent.getDateOfEvent(), created.getDateOfEvent());
        Assert.assertEquals(newEvent.getStartTime(), created.getStartTime());
        Assert.assertEquals(newEvent.getEndTime(), created.getEndTime());
        Assert.assertEquals(newEvent.getDescription(), created.getDescription());

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
     * Test for getting event
     */
    @Test
    public void getEventTest() {
        EventEntity entity = data.get(0);
        EventEntity resultEntity = eventLogic.getEvent(neighborhood.getId(),entity.getId());
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
        pojoEntity.setTitle("New Title");
        pojoEntity.setStartTime("09:45 AM");
        pojoEntity.setEndTime("10:30 PM");

        eventLogic.updateEvent(neighborhood.getId(), pojoEntity);
        EventEntity resp = em.find(EventEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getTitle(), resp.getTitle());

    }

    /**
     * Test for deleting a event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void deleteEventTest() throws BusinessLogicException {
        EventEntity entity = data.get(1);
        eventLogic.deleteEvent(neighborhood.getId(), entity.getId());
        EventEntity deleted = em.find(EventEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Finds an event by title.
     *
     */
    @Test
    public void getEventByNameTest() {
        EventEntity entity = data.get(0);
        EventEntity resultEntity = eventLogic.getEventByTitle(entity.getTitle());

        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
        Assert.assertEquals(entity.getStartTime(), resultEntity.getStartTime());
        Assert.assertEquals(entity.getEndTime(), resultEntity.getEndTime());
    }

    /**
     * Tests the creation of an Event with no location.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoLocationTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setLocation(location);
        location.setId(111L);

        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), newEntity.getLocation().getId(), newEntity);
    }

    /**
     * Tests the creation of an Event with non-existent location.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
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
        eventLogic.createEvent(neighborhood.getId(), host.getId(), loc.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no post date.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoPostDateTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setDatePosted(null);

        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no event date.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoEventDateTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setDateOfEvent(null);

        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no start time.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoStartTimeTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setStartTime(null);
        
        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no end time.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoEndTimeTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setEndTime(null);
        
        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no title.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoTitleTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setTitle(null);

        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
    
    
    /**
     * Tests the creation of an Event with no description.
     *
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoDescriptionTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        newEntity.setDescription(null);

        // persist the created event, should not be null
        eventLogic.createEvent(neighborhood.getId(), host.getId(), location.getId(), newEntity);
    }
}
