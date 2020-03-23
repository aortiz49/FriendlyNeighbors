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
public class EventGroupLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private EventGroupLogic eventGroupLogic;

    @Inject
    private GroupLogic groupLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private EventEntity event = new EventEntity();
    private List<GroupEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(EventGroupLogic.class.getPackage())
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
        em.createQuery("delete from GroupEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        event = factory.manufacturePojo(EventEntity.class);
        event.setId(1L);
        event.setGroups(new ArrayList<>());

        LocationEntity location = factory.manufacturePojo(LocationEntity.class);
        em.persist(location);

        GroupEntity group = factory.manufacturePojo(GroupEntity.class);
        group.setNeighborhood(neighborhood);
        em.persist(group);

        event.setLocation(location);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);

        em.persist(resident);

        event.setHost(resident);

        em.persist(event);

        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);

            entity.setEvents(new ArrayList<>());
            entity.getEvents().add(event);
            entity.setNeighborhood(neighborhood);

            em.persist(entity);
            data.add(entity);
            event.getGroups().add(entity);
        }
    }

    /**
     * Test to associate group with event
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addGroupTest() throws BusinessLogicException {
        GroupEntity newGroup = factory.manufacturePojo(GroupEntity.class);

        newGroup.setNeighborhood(neighborhood);

        groupLogic.createGroup(newGroup, neighborhood.getId());

        GroupEntity groupEntity = eventGroupLogic.associateGroupToEvent(event.getId(), newGroup.getId(), neighborhood.getId());
        Assert.assertNotNull(groupEntity);

        Assert.assertEquals(groupEntity.getId(), newGroup.getId());
        Assert.assertEquals(groupEntity.getDescription(), newGroup.getDescription());

        GroupEntity lastGroup = eventGroupLogic.getGroup(event.getId(), newGroup.getId(), neighborhood.getId());

        Assert.assertEquals(lastGroup.getId(), newGroup.getId());

    }

    /**
     * Test for getting collection of group entities associated with event
     */
    @Test
    public void getGroupsTest() {
        List<GroupEntity> groupEntities = eventGroupLogic.getGroups(event.getId(), neighborhood.getId());

        Assert.assertEquals(data.size(), groupEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(groupEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting group entity associated with event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getGroupTest() throws BusinessLogicException {
        GroupEntity groupEntity = data.get(0);
        GroupEntity group = eventGroupLogic.getGroup(event.getId(), groupEntity.getId(), neighborhood.getId());
        Assert.assertNotNull(group);

        Assert.assertEquals(groupEntity.getId(), group.getId());
        Assert.assertEquals(groupEntity.getDescription(), group.getDescription());

    }

    /**
     * Test for replacing groups associated with event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void replaceGroupsTest() throws BusinessLogicException {
        List<GroupEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);
            entity.setEvents(new ArrayList<>());
            entity.getEvents().add(event);

            groupLogic.createGroup(entity, neighborhood.getId());

            newCollection.add(entity);
        }
        eventGroupLogic.replaceGroups(event.getId(), newCollection, neighborhood.getId());
        List<GroupEntity> groupEntities = eventGroupLogic.getGroups(event.getId(), neighborhood.getId());
        for (GroupEntity aNuevaLista : newCollection) {
            Assert.assertTrue(groupEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing group from event
     *
     */
    @Test
    public void removeGroupTest() {
        for (GroupEntity group : data) {
            eventGroupLogic.removeGroup(event.getId(), group.getId(), neighborhood.getId());
        }
        Assert.assertTrue(eventGroupLogic.getGroups(event.getId(), neighborhood.getId()).isEmpty());
    }

}
