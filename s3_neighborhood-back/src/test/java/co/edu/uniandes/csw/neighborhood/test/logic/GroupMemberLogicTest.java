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
public class GroupMemberLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private GroupMemberLogic groupResidentProfileLogic;

    @Inject
    private ResidentProfileLogic memberLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;
    
    private NeighborhoodEntity neighborhood;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private GroupEntity group = new GroupEntity();
    private List<ResidentProfileEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(GroupMemberLogic.class.getPackage())
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
        em.createQuery("delete from GroupEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);
        
        
        group = factory.manufacturePojo(GroupEntity.class);
        group.setId(1L);
        group.setMembers(new ArrayList<>());
        group.setNeighborhood(neighborhood);
        
        em.persist(group);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setNeighborhood(neighborhood);

            entity.setGroups(new ArrayList<>());
            entity.getGroups().add(group);
            em.persist(entity);
            data.add(entity);
            group.getMembers().add(entity);
        }
    }

    /**
     * Test to associate a member with an group
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addResidentTest() throws BusinessLogicException {
        ResidentProfileEntity newResidentProfile = factory.manufacturePojo(ResidentProfileEntity.class);



        newResidentProfile.setNeighborhood(neighborhood);

        memberLogic.createResident(newResidentProfile);

        ResidentProfileEntity memberEntity = groupResidentProfileLogic.associateMemberToGroup(group.getId(), newResidentProfile.getId());
        Assert.assertNotNull(memberEntity);

        Assert.assertEquals(memberEntity.getId(), newResidentProfile.getId());
        Assert.assertEquals(memberEntity.getAddress(), newResidentProfile.getAddress());

        ResidentProfileEntity lastResident = groupResidentProfileLogic.getResidentProfile(group.getId(), newResidentProfile.getId());

        Assert.assertEquals(lastResident.getId(), newResidentProfile.getId());

    }

    /**
     * Test for getting a collection of member entities associated with an group
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> memberEntities = groupResidentProfileLogic.getResidentProfiles(group.getId());

        Assert.assertEquals(data.size(), memberEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(memberEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting a member entity associated with an group
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getResidentTest() throws BusinessLogicException {
        ResidentProfileEntity memberEntity = data.get(0);
        ResidentProfileEntity member = groupResidentProfileLogic.getResidentProfile(group.getId(), memberEntity.getId());
        Assert.assertNotNull(member);

        Assert.assertEquals(memberEntity.getId(), member.getId());
        Assert.assertEquals(memberEntity.getAddress(), member.getAddress());

    }

    /**
     * Test for replacing members associated with an group
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceResidentProfilesTest() throws BusinessLogicException {
        List<ResidentProfileEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setGroups(new ArrayList<>());
            entity.getGroups().add(group);


            entity.setNeighborhood(neighborhood);
            memberLogic.createResident(entity);

            newCollection.add(entity);
        }
        groupResidentProfileLogic.replaceResidentProfiles(group.getId(), newCollection);
        List<ResidentProfileEntity> memberEntities = groupResidentProfileLogic.getResidentProfiles(group.getId());
        for (ResidentProfileEntity aNuevaLista : newCollection) {
            Assert.assertTrue(memberEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing a member from an group
     *
     */
    @Test
    public void removeResidentTest() {
        for (ResidentProfileEntity member : data) {
            groupResidentProfileLogic.removeResidentProfile(group.getId(), member.getId());
        }
        Assert.assertTrue(groupResidentProfileLogic.getResidentProfiles(group.getId()).isEmpty());
    }

}
