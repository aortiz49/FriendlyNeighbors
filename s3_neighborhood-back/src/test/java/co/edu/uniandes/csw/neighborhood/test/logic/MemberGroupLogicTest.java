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
public class MemberGroupLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private MemberGroupLogic memberGroupLogic;

    @Inject
    private GroupLogic groupLogic;
    
    @Inject
    private NeighborhoodPersistence neighPersistence;
    
    private NeighborhoodEntity neighborhood;


    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private ResidentProfileEntity member = new ResidentProfileEntity();
    private List<GroupEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(MemberGroupLogic.class.getPackage())
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
        em.createQuery("delete from GroupEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        member = factory.manufacturePojo(ResidentProfileEntity.class);
        member.setId(1L);
        member.setGroups(new ArrayList<>());
        member.setNeighborhood(neighborhood);
        em.persist(member);

        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);
            entity.setNeighborhood(neighborhood);
            
            entity.setMembers(new ArrayList<>());
            entity.getMembers().add(member);
            em.persist(entity);
            data.add(entity);
            member.getGroups().add(entity);
        }
    }

    /**
     * Test to associate an group with a member
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addGroupTest() throws BusinessLogicException {
        GroupEntity newGroup = factory.manufacturePojo(GroupEntity.class);
        groupLogic.createGroup(newGroup);
        GroupEntity groupEntity = memberGroupLogic.associateGroupToMember(member.getId(), newGroup.getId());
        Assert.assertNotNull(groupEntity);

        Assert.assertEquals(groupEntity.getId(), newGroup.getId());
        Assert.assertEquals(groupEntity.getDescription(), newGroup.getDescription());

        GroupEntity lastGroup = memberGroupLogic.getGroup(member.getId(), newGroup.getId());

        Assert.assertEquals(lastGroup.getId(), newGroup.getId());

    }

    /**
     * Test for getting a collection of group entities associated with a member
     */
    @Test
    public void getGroupsTest() {
        List<GroupEntity> groupEntities = memberGroupLogic.getGroups(member.getId());

        Assert.assertEquals(data.size(), groupEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(groupEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting an group entity associated with a a member
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getGroupTest() throws BusinessLogicException {
        GroupEntity groupEntity = data.get(0);
        GroupEntity group = memberGroupLogic.getGroup(member.getId(), groupEntity.getId());
        Assert.assertNotNull(group);

        Assert.assertEquals(groupEntity.getId(), group.getId());
        Assert.assertEquals(groupEntity.getDescription(), group.getDescription());

    }

    /**
     * Test for replacing groups associated with a member
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceGroupsTest() throws BusinessLogicException {
        List<GroupEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);
            entity.setMembers(new ArrayList<>());
            entity.getMembers().add(member);
            groupLogic.createGroup(entity);
            newCollection.add(entity);
        }
        memberGroupLogic.replaceGroups(member.getId(), newCollection);
        List<GroupEntity> groupEntities = memberGroupLogic.getGroups(member.getId());
        for (GroupEntity aNuevaLista : newCollection) {
            Assert.assertTrue(groupEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing an group from member
     *
     */
    @Test
    public void removeGroupTest() {
        for (GroupEntity group : data) {
            memberGroupLogic.removeGroup(member.getId(), group.getId());
        }
        Assert.assertTrue(memberGroupLogic.getGroups(member.getId()).isEmpty());
    }

}
