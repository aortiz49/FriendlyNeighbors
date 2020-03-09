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

import co.edu.uniandes.csw.neighborhood.ejb.AttendeeEventLogic;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 * Tests the ResidentProfileEventLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class AttendeeEventLogicTest {
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
    private AttendeeEventLogic attendeeEventLogic;

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
    private List<EventEntity> testEvents = new ArrayList<>();

    /**
     * List of groups to be used in the tests.
     */
    private List<ResidentProfileEntity> testResidentProfiles = new ArrayList<>();
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
        em.createQuery("delete from EventEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();

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
            ResidentProfileEntity buss = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(buss);
            testResidentProfiles.add(buss);
        }

        // associates groups to an event
        testResidentProfiles.get(0).getEvents().add(testEvents.get(0));
        testResidentProfiles.get(2).getEvents().add(testEvents.get(0));

        testEvents.get(0).getAttendees().add(testResidentProfiles.get(0));
        testEvents.get(0).getAttendees().add(testResidentProfiles.get(2));

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
    public void addResidentProfileToEventTest() throws BusinessLogicException {
        // gets the second random event from the list
        EventEntity event = testEvents.get(0);

        // gets the second random group from the list, since the first has an associated 
        // group already
        ResidentProfileEntity group = testResidentProfiles.get(1);

        // add the group to the event
        ResidentProfileEntity response = attendeeEventLogic.addResidentProfileToEvent(group.getId(), event.getId());

        EventEntity found = em.find(EventEntity.class, event.getId());
        Assert.assertEquals(3, found.getAttendees().size());

        Assert.assertNotNull(response);
        Assert.assertEquals(group.getId(), response.getId());
    }

    /**
     * Tests the consultation of all group entities associated with a event.
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> list = attendeeEventLogic.getResidentProfiles(testEvents.get(0).getId());

        // checks that there are two groupes associated to the event
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated event matches
        Assert.assertEquals(list.get(0).getEventsToAttend().get(0).getTitle(), testEvents.get(0).getTitle());
        Assert.assertEquals(list.get(1).getEventsToAttend().get(0).getTitle(), testEvents.get(0).getTitle());

    }

    /**
     * Tests the consultation of a group entity associated with a event.
     *
     * @throws BusinessLogicException if the group is not found
     */
    @Test
    public void getResidentProfileTest() throws BusinessLogicException {

        // gets the first group from the list
        ResidentProfileEntity group = testResidentProfiles.get(0);

        // gets the first event from the list
        EventEntity event = testEvents.get(0);

        // get the group from the event
        ResidentProfileEntity response = attendeeEventLogic.getResidentProfile(event.getId(), group.getId());

        Assert.assertEquals(group.getId(), response.getId());

    }

    /**
     * Tests the removal of a group from the event.
     */
    @Test
    public void removeResidentProfileTest() {

        // gets the first group from the list. 
        // (Uses em.find because the persisted event contains the added groups)
        EventEntity event = em.find(EventEntity.class, testEvents.get(0).getId());

        // get the first associated group
        ResidentProfileEntity group = testResidentProfiles.get(0);

        attendeeEventLogic.removeResidentProfile(event.getId(), group.getId());

        // gets the list of events in the group
        List<ResidentProfileEntity> list = em.find(EventEntity.class, event.getId()).getAttendees();

        Assert.assertEquals(1, list.size());
    }

}
