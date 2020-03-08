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

import co.edu.uniandes.csw.neighborhood.ejb.GroupEventLogic;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
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

/**
 * Tests the GroupEventLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class GroupEventLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for group/event logic.
     */
    @Inject
    private GroupEventLogic groupEventLogic;

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
     * List of events to be used in the tests.
     */
    private List<EventEntity> testEvents = new ArrayList<EventEntity>();

    /**
     * List of groups to be used in the tests.
     */
    private List<GroupEntity> testGroups = new ArrayList<GroupEntity>();
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
        em.createQuery("delete from GroupEntity").executeUpdate();
        em.createQuery("delete from EventEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random events
        for (int i = 0; i < 3; i++) {
            EventEntity neigh = factory.manufacturePojo(EventEntity.class);
            em.persist(neigh);
            testEvents.add(neigh);
        }

        // creates 3 random groupes
        for (int i = 0; i < 3; i++) {
            GroupEntity buss = factory.manufacturePojo(GroupEntity.class);
            em.persist(buss);
            testGroups.add(buss);
        }

        // associates groups to an event
        testGroups.get(0).getEvents().add(testEvents.get(0));
        testGroups.get(2).getEvents().add(testEvents.get(0));

        testEvents.get(0).getGroups().add(testGroups.get(0));
        testEvents.get(0).getGroups().add(testGroups.get(2));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a group with a event.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addGroupToEventTest() throws BusinessLogicException {
        // gets the second random event from the list
        EventEntity event = testEvents.get(0);

        // gets the second random group from the list, since the first has an associated 
        // group already
        GroupEntity group = testGroups.get(1);

        // add the group to the event
        GroupEntity response = groupEventLogic.addGroupToEvent(group.getId(), event.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(group.getId(), response.getId());
    }

    /**
     * Tests the consultation of all group entities associated with a event.
     */
    @Test
    public void getGroupesTest() {
        List<GroupEntity> list = groupEventLogic.getGroups(testEvents.get(0).getId());

        // checks that there are two groupes associated to the event
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated event matches
        Assert.assertEquals(list.get(0).getEvents().get(0).getTitle(), testEvents.get(0).getTitle());

        Assert.assertEquals(list.get(1).getEvents().get(0).getTitle(), testEvents.get(0).getTitle());

    }

    /**
     * Tests the consultation of a group entity associated with a event.
     *
     * @throws BusinessLogicException if the group is not found
     */
    @Test
    public void getGroupTest() throws BusinessLogicException {

        // gets the first group from the list
        GroupEntity group = testGroups.get(0);

        // gets the first event from the list
        EventEntity event = testEvents.get(0);

        // get the group from the event
        GroupEntity response = groupEventLogic.getGroup(event.getId(), group.getId());

        Assert.assertEquals(group.getId(), response.getId());

    }

    /**
     * Tests the removal of a group from the event.
     */
    @Test
    public void removeGroupTest() {
        // gets the first event from the list. 
        // (Uses em.find because the persisted event contains the added groupes)
        EventEntity event = em.find(EventEntity.class, testEvents.get(0).getId());

        // get the first associated group
        GroupEntity group = testGroups.get(0);

        // gets the list of groups in the event
        List<GroupEntity> list = event.getGroups();

        groupEventLogic.removeGroup(event.getId(), group.getId());
        
        Assert.assertEquals(1, list.size());

    }

}
