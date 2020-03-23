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
public class GroupEventLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private GroupEventLogic groupEventLogic;

    @Inject
    private EventLogic eventLogic;

    @Inject
    private LocationPersistence locationPersistence;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private GroupEntity group = new GroupEntity();
    private List<EventEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(EventEntity.class.getPackage())
                .addPackage(GroupEventLogic.class.getPackage())
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
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        group = factory.manufacturePojo(GroupEntity.class);
        group.setId(1L);
        group.setEvents(new ArrayList<>());
        group.setNeighborhood(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);

        em.persist(resident);

        em.persist(group);

        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);

            entity.setGroups(new ArrayList<>());
            entity.getGroups().add(group);
            entity.setHost(resident);

            em.persist(entity);
            data.add(entity);
            group.getEvents().add(entity);
        }

    }

    /**
     * Test to associate event with group
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addEventTest() throws BusinessLogicException {
        EventEntity newEvent = factory.manufacturePojo(EventEntity.class);

        LocationEntity location = factory.manufacturePojo(LocationEntity.class);

        locationPersistence.create(location);

        newEvent.setLocation(location);

        eventLogic.createEvent(newEvent, resident.getId(), neighborhood.getId());

        EventEntity eventEntity = groupEventLogic.associateEventToGroup(group.getId(), newEvent.getId(), neighborhood.getId());
        Assert.assertNotNull(eventEntity);

        Assert.assertEquals(eventEntity.getId(), newEvent.getId());
        Assert.assertEquals(eventEntity.getDescription(), newEvent.getDescription());

        EventEntity lastEvent = groupEventLogic.getEvent(group.getId(), newEvent.getId(), neighborhood.getId());

        Assert.assertEquals(lastEvent.getId(), newEvent.getId());

    }

    /**
     * Test for getting collection of event entities associated with group
     */
    @Test
    public void getEventsTest() {
        List<EventEntity> eventEntities = groupEventLogic.getEvents(group.getId(), neighborhood.getId());

        Assert.assertEquals(data.size(), eventEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(eventEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting event entity associated with group
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getEventTest() throws BusinessLogicException {
        EventEntity eventEntity = data.get(0);
        EventEntity event = groupEventLogic.getEvent(group.getId(), eventEntity.getId(), neighborhood.getId());
        Assert.assertNotNull(event);

        Assert.assertEquals(eventEntity.getId(), event.getId());
        Assert.assertEquals(eventEntity.getDescription(), event.getDescription());

    }

    /**
     * Test for replacing events associated with group
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceEventsTest() throws BusinessLogicException {
        List<EventEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            entity.setGroups(new ArrayList<>());
            entity.getGroups().add(group);

            LocationEntity location = factory.manufacturePojo(LocationEntity.class);

            locationPersistence.create(location);

            entity.setLocation(location);

            eventLogic.createEvent(entity, resident.getId(), neighborhood.getId());
            newCollection.add(entity);
        }
        groupEventLogic.replaceEvents(group.getId(), newCollection, neighborhood.getId());
        List<EventEntity> eventEntities = groupEventLogic.getEvents(group.getId(), neighborhood.getId());
        for (EventEntity aNuevaLista : newCollection) {
            Assert.assertTrue(eventEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing event from group
     *
     */
    @Test
    public void removeEventTest() {
        for (EventEntity event : data) {
            groupEventLogic.removeEvent(group.getId(), event.getId(), neighborhood.getId());
        }
        Assert.assertTrue(groupEventLogic.getEvents(group.getId(), neighborhood.getId()).isEmpty());
    }

}
