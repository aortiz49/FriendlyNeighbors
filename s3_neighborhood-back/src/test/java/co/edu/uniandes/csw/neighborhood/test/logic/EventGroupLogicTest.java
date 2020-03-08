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

import co.edu.uniandes.csw.neighborhood.ejb.EventGroupLogic;
import co.edu.uniandes.csw.neighborhood.ejb.GroupLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
 * Tests the EventGroupLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventGroupLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for event/group logic.
     */
    @Inject
    private EventGroupLogic eventGroupLogic;

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
     * List of groups to be used in the tests.
     */
    private List<GroupEntity> testGroups = new ArrayList<GroupEntity>();

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
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(GroupLogic.class.getPackage())
                .addPackage(GroupPersistence.class.getPackage())
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
        em.createQuery("delete from GroupEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random groups
        for (int i = 0; i < 3; i++) {
            GroupEntity neigh = factory.manufacturePojo(GroupEntity.class);
            em.persist(neigh);
            testGroups.add(neigh);
        }

        // creates 3 random eventes
        for (int i = 0; i < 3; i++) {
            EventEntity buss = factory.manufacturePojo(EventEntity.class);
            em.persist(buss);
            testEvents.add(buss);
        }

        // associates events to a group
        testEvents.get(0).getGroups().add(testGroups.get(0));
        testEvents.get(2).getGroups().add(testGroups.get(0));

        testGroups.get(0).getEvents().add(testEvents.get(0));
        testGroups.get(0).getEvents().add(testEvents.get(2));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a event with a group.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addEventToGroupTest() throws BusinessLogicException {
        // gets the second random group from the list
        GroupEntity group = testGroups.get(0);

        // gets the second random event from the list, since the first has an associated 
        // event already
        EventEntity event = testEvents.get(1);

        // add the event to the group
        EventEntity response = eventGroupLogic.addEventToGroup(event.getId(), group.getId());

        Assert.assertNotNull(response);

        GroupEntity found = em.find(GroupEntity.class, group.getId());
        Assert.assertEquals(3, found.getEvents().size());
        Assert.assertEquals(event.getId(), response.getId());
    }

    /**
     * Tests the consultation of all event entities associated with a group.
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> list = eventGroupLogic.getEvents(testGroups.get(0).getId());

        // checks that there are two events associated to the group
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated group matches
        Assert.assertEquals(list.get(0).getGroups().get(0).getName(), testGroups.get(0).getName());

        Assert.assertEquals(list.get(1).getGroups().get(0).getName(), testGroups.get(0).getName());

    }

    /**
     * Tests the consultation of a event entity associated with a group.
     *
     * @throws BusinessLogicException if the event is not found
     */
    @Test
    public void getEventTest() throws BusinessLogicException {

        // gets the first event from the list
        EventEntity event = testEvents.get(0);

        // gets the first group from the list
        GroupEntity group = testGroups.get(0);

        // get the event from the group
        EventEntity response = eventGroupLogic.getEvent(group.getId(), event.getId());

        Assert.assertEquals(event.getId(), response.getId());

    }

    /**
     * Tests the removal of a event from the group.
     */
    @Test
    public void removeEventTest() {
        // gets the first group from the list. 
        // (Uses em.find because the persisted group contains the added eventes)
        GroupEntity group = em.find(GroupEntity.class, testGroups.get(0).getId());

        // get the first associated event
        EventEntity event = testEvents.get(0);

        eventGroupLogic.removeEvent(group.getId(), event.getId());

        // gets the list of events in the group
        List<EventEntity> list = em.find(GroupEntity.class, group.getId()).getEvents();

        Assert.assertEquals(1, list.size());

    }

}
