package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.*;
import co.edu.uniandes.csw.neighborhood.entities.*;

import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.*;
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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class AttendeeEventLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private AttendeeEventLogic residentEventLogic;

    @Inject
    private EventLogic eventLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private ResidentProfileEntity resident = new ResidentProfileEntity();
    private List<EventEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(EventEntity.class.getPackage())
                .addPackage(AttendeeEventLogic.class.getPackage())
                .addPackage(ResidentProfilePersistence.class.getPackage())
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
        em.createQuery("delete from EventEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setId(1L);
        resident.setEventsToAttend(new ArrayList<>());
        em.persist(resident);

        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            entity.setAttendees(new ArrayList<>());
            entity.getAttendees().add(resident);
            em.persist(entity);
            data.add(entity);
            resident.getEventsToAttend().add(entity);
        }

        EventEntity entity = factory.manufacturePojo(EventEntity.class);
        entity.setAttendees(new ArrayList<>());
        entity.getAttendees().add(resident);
        em.persist(entity);
        data.add(entity);
    }

    /**
     * Test to associate an event with a resident
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addEventTest() throws BusinessLogicException {
        // gets the resident
        ResidentProfileEntity residentEntity = resident;

        // gets the third random event from the list
        EventEntity event = data.get(3);

        // add the event to the resident
        residentEventLogic.associateEventToAttenddee(residentEntity.getId(), event.getId());

        ResidentProfileEntity found = em.find(ResidentProfileEntity.class, residentEntity.getId());
        Assert.assertEquals(4, found.getEvents().size());
        Assert.assertEquals(event.getId(), response.getId());

    }

    /**
     * Test for getting a collection of event entities associated with a resident
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> eventEntities = residentEventLogic.getEvents(resident.getId());

        Assert.assertEquals(data.size(), eventEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(eventEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting an event entity associated with a a resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getEventTest() throws BusinessLogicException {
        EventEntity eventEntity = data.get(0);
        EventEntity event = residentEventLogic.getEvent(resident.getId(), eventEntity.getId());
        Assert.assertNotNull(event);

        Assert.assertEquals(eventEntity.getId(), event.getId());
        Assert.assertEquals(eventEntity.getDescription(), event.getDescription());

    }

    /**
     * Test for replacing events associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceEventsTest() throws BusinessLogicException {
        List<EventEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            entity.setAttendees(new ArrayList<>());
            entity.getAttendees().add(resident);
            eventLogic.createEvent(entity);
            newCollection.add(entity);
        }
        residentEventLogic.replaceEvents(resident.getId(), newCollection);
        List<EventEntity> eventEntities = residentEventLogic.getEvents(resident.getId());
        for (EventEntity aNuevaLista : newCollection) {
            Assert.assertTrue(eventEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing an event from resident
     *
     */
    @Test
    public void removeEventTest() {
        for (EventEntity event : data) {
            residentEventLogic.removeEvent(resident.getId(), event.getId());
        }
        Assert.assertTrue(residentEventLogic.getEvents(resident.getId()).isEmpty());
    }

}
