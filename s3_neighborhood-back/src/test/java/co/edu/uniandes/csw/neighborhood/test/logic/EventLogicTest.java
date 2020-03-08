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
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
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
//===================================================
// Imports
//===================================================

/**
 * Runs tests for NeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Injects BusinessLogic objects.
     */
    @Inject
    private EventLogic eventLogic;

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
    private List<EventEntity> events = new ArrayList<>();

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
                .addPackage(EventEntity.class.getPackage())
                .addPackage(EventLogic.class.getPackage())
                .addPackage(EventPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

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
        em.createQuery("delete from EventEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {

        // creates 3 events 
        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            em.persist(entity);
            events.add(entity);
        }

        // creates the location where the event will be hosted
        LocationEntity location = factory.manufacturePojo(LocationEntity.class);
        location.setName("Parque de la 93");
        em.persist(location);

        // creates the host who will take care of the event
        ResidentProfileEntity host = factory.manufacturePojo(ResidentProfileEntity.class);
        host.setName("Paulina Acosta");
        em.persist(host);

        // creates the resident where the event is located
        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        host.setName("Andy Ortiz");
        em.persist(resident);

        // add the event to the host
        host.getEvents().add(events.get(0));

        // add the event to the resident
        resident.getEvents().add(events.get(0));

        // add the host and resident to the event
        events.get(0).setHost(host);
        events.get(0).getAttendees().add(resident);

        // add the location to the event
        events.get(0).setLocation(location);

    }

    /**
     * Tests the creation of an Event.
     */
    @Test
    public void createEventTest() throws BusinessLogicException {

        // creates a random event
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // sets the location
        newEntity.setLocation(events.get(0).getLocation());

        // sets the attendee
        newEntity.getAttendees().add(events.get(0).getAttendees().get(0));

        // sets the host
        newEntity.setHost(events.get(0).getHost());

        // persist the created event, should not be null
        EventEntity result = eventLogic.createEvent(newEntity);
        Assert.assertNotNull(result);

        // locate the persisted business
        EventEntity entity = em.find(EventEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getTitle(), entity.getTitle());
    }

    /**
     * Tests the creation of an Event with no location.
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNoLocationTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // persist the created event, should not be null
        EventEntity result = eventLogic.createEvent(newEntity);
    }

    /**
     * Tests the creation of an Event with non-existent location.
     */
    @Test(expected = BusinessLogicException.class)
    public void createEventNonExistentLocationTest() throws BusinessLogicException {

        // creates a random entity
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // change location id to one that doesn't exist in the system
        LocationEntity loc = events.get(0).getLocation();
        loc.setId(11123L);
        newEntity.setLocation(loc);

        // persist the created business, should not finish
        EventEntity result = eventLogic.createEvent(newEntity);
    }

    /**
     * Returns all the events in the database.
     *
     * @return list of events
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> list = eventLogic.getAllEvents();
        Assert.assertEquals(events.size(), list.size());

        for (EventEntity entity : list) {
            boolean found = false;
            for (EventEntity storedEntity : events) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Finds an event by Id.
     *
     * @return the found event, null if not found
     */
    @Test
    public void getEventTest() {
        EventEntity entity = events.get(0);
        EventEntity resultEntity = eventLogic.getEvent(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
        Assert.assertEquals(entity.getAvailability(), resultEntity.getAvailability());
        Assert.assertEquals(entity.getStartTime(), resultEntity.getStartTime());
        Assert.assertEquals(entity.getEndTime(), resultEntity.getEndTime());
    }

    /**
     * Finds an event by title.
     *
     * @return the found event, null if not found
     */
    @Test
    public void getEventByNameTest() {
        EventEntity entity = events.get(0);
        EventEntity resultEntity = eventLogic.getEventByTitle(entity.getTitle());

        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
        Assert.assertEquals(entity.getAvailability(), resultEntity.getAvailability());
        Assert.assertEquals(entity.getStartTime(), resultEntity.getStartTime());
        Assert.assertEquals(entity.getEndTime(), resultEntity.getEndTime());
    }

    /**
     * Tests to update an event.
     */
    @Test
    public void updateEventTest() throws BusinessLogicException {
        // get first event from generated data set
        EventEntity entity = events.get(0);

        // generate a random event
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // set the id of the random event to the id of the first one from the data set
        newEntity.setId(entity.getId());

        // update the event with the new information
        eventLogic.updateEvent(entity.getId(), newEntity);

        // find the event in the database
        EventEntity resp = em.find(EventEntity.class, entity.getId());

        // make sure they are equal
        Assert.assertEquals(newEntity.getId(), resp.getId());
        Assert.assertEquals(newEntity.getDescription(), resp.getDescription());
        Assert.assertEquals(newEntity.getAvailability(), resp.getAvailability());
        Assert.assertEquals(newEntity.getStartTime(), resp.getStartTime());
        Assert.assertEquals(newEntity.getEndTime(), resp.getEndTime());
    }

    /**
     * Tests the deletion of an event.
     *
     */
    public void deleteEventTest() throws BusinessLogicException {
        // get first event from generated data set
        EventEntity entity = events.get(0);

        // delete the event
        eventLogic.deleteEvent(entity.getId());

    }

}
