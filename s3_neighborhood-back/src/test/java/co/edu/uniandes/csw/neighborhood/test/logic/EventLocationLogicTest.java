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
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.ejb.EventLocationLogic;
import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
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
 * Tests the EventLocationLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventLocationLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for event/location logic.
     */
    @Inject
    private EventLocationLogic eventLocationLogic;

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
     * List of locations to be used in the tests.
     */
    private List<LocationEntity> testLocations = new ArrayList<LocationEntity>();

    /**
     * List of events to be used in the tests.
     */
    private List<EventEntity> testEvents = new ArrayList<EventEntity>();
//===================================================
// Test Setup
//===================================================

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(LocationEntity.class.getPackage())
                .addPackage(LocationLogic.class.getPackage())
                .addPackage(LocationPersistence.class.getPackage())
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
        em.createQuery("delete from LocationEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random locations
        for (int i = 0; i < 3; i++) {
            LocationEntity neigh = factory.manufacturePojo(LocationEntity.class);
            em.persist(neigh);
            testLocations.add(neigh);
        }

        // creates 3 random eventes
        for (int i = 0; i < 3; i++) {
            EventEntity buss = factory.manufacturePojo(EventEntity.class);
            em.persist(buss);
            testEvents.add(buss);
        }

        // associates events to a location
        testEvents.get(0).setLocation(testLocations.get(0));
        testEvents.get(2).setLocation(testLocations.get(0));

        testLocations.get(0).getEvents().add(testEvents.get(0));
        testLocations.get(0).getEvents().add(testEvents.get(2));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a event with a location.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addEventToLocationTest() throws BusinessLogicException {
        // gets the second random location from the list
        LocationEntity location = testLocations.get(0);

        // gets the second random event from the list, since the first has an associated 
        // event already
        EventEntity event = testEvents.get(1);

        // add the event to the location
        EventEntity response = eventLocationLogic.addEventToLocation(event.getId(), location.getId());

        Assert.assertNotNull(response);

        LocationEntity found = em.find(LocationEntity.class, location.getId());
        Assert.assertEquals(3, found.getEvents().size());
        Assert.assertEquals(event.getId(), response.getId());
    }

    /**
     * Tests the consultation of all event entities associated with a location.
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> list = eventLocationLogic.getEvents(testLocations.get(0).getId());

        // checks that there are two events associated to the location
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated location matches
        Assert.assertEquals(list.get(0).getLocation().getName(), testLocations.get(0).getName());

        Assert.assertEquals(list.get(1).getLocation().getName(), testLocations.get(0).getName());

    }

    /**
     * Tests the consultation of a event entity associated with a location.
     *
     * @throws BusinessLogicException if the event is not found
     */
    @Test
    public void getEventTest() throws BusinessLogicException {

        // gets the first event from the list
        EventEntity event = testEvents.get(0);

        // gets the first location from the list
        LocationEntity location = testLocations.get(0);

        // get the event from the location
        EventEntity response = eventLocationLogic.getEvent(location.getId(), event.getId());

        Assert.assertEquals(event.getId(), response.getId());

    }

    /**
     * Tests the removal of a event from the location.
     */
    @Test
    public void removeEventTest() {
        // gets the first location from the list. 
        // (Uses em.find because the persisted location contains the added eventes)
        LocationEntity location = em.find(LocationEntity.class, testLocations.get(0).getId());

        // get the first associated event
        EventEntity event = testEvents.get(0);

        eventLocationLogic.removeEvent(location.getId(), event.getId());

        // gets the list of events in the location
        List<EventEntity> list = em.find(LocationEntity.class, location.getId()).getEvents();

        Assert.assertEquals(1, list.size());

    }

}
