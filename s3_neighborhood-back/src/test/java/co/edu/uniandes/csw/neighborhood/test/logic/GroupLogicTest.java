/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.GroupLogic;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class GroupLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private GroupLogic groupLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;

    /**
     * The entity manager that will verify data directly with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    UserTransaction utx;

    /**
     * An array containing the set of data used for the tests.
     */
    private List<GroupEntity> data = new ArrayList<>();

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(GroupLogic.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Initial test configuration that will run before each test.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            em.joinTransaction();

            // clears the data in the database directly using the EntityManager
            // and UserTransaction
            clearData();

            // creates the new data
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
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        for (int i = 0; i < 3; i++) {

            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);
            entity.setNeighborhood(neighborhood);
            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a group
     */
    @Test
    public void createGroupTest() {

        try {

            NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
            neighPersistence.create(neigh);

            // uses the factory to create a ranbdom NeighborhoodEntity object
            GroupEntity newGroup = factory.manufacturePojo(GroupEntity.class);

            newGroup.setNeighborhood(neigh);

            // invokes the method to be tested (create): it creates a table in the 
            // database. The parameter of this method is the newly created object from 
            // the podam factory which has an id associated to it. 
            GroupEntity result = groupLogic.createGroup(newGroup, neighborhood.getId());

            // verify that the created object is not null
            Assert.assertNotNull(result);

            // using the entity manager, it searches the database for the object 
            // matching the id of the newly created factory object
            GroupEntity entity
                    = em.find(GroupEntity.class, result.getId());

            // verifies that the object exists in the database
            Assert.assertNotNull(entity);

            // compares if the name of the new object generated by the factory matched
            // the name of the object in the database
            Assert.assertEquals(newGroup.getName(), entity.getName());
        } catch (BusinessLogicException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test for getting ll groups
     */
    @Test
    public void getGroupsTest() {
        List<GroupEntity> list = groupLogic.getGroups(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (GroupEntity entity : list) {
            boolean found = false;
            for (GroupEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting  group
     */
    @Test
    public void getGroupTest() {
        GroupEntity entity = data.get(0);
        GroupEntity resultEntity = groupLogic.getGroup(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Test for updating a group
     */
    @Test
    public void updateGroupTest() {
        try {
            GroupEntity entity = data.get(0);
            GroupEntity pojoEntity = factory.manufacturePojo(GroupEntity.class);
            pojoEntity.setId(entity.getId());
            groupLogic.updateGroup(pojoEntity, neighborhood.getId());
            GroupEntity resp = em.find(GroupEntity.class, entity.getId());
            Assert.assertEquals(pojoEntity.getId(), resp.getId());
            Assert.assertEquals(pojoEntity.getName(), resp.getName());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(GroupLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test for deleting a group
     */
    @Test
    public void deleteGroupTest() throws BusinessLogicException {
        GroupEntity entity = data.get(1);
        groupLogic.deleteGroup(entity.getId(), neighborhood.getId());
        GroupEntity deleted = em.find(GroupEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Test for creating a resident with no date
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoDate() throws BusinessLogicException {
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);
        newEntity.setDateCreated(null);
        groupLogic.createGroup(newEntity, neighborhood.getId());
    }

    /**
     * Test for creating a resident with no name
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoName() throws BusinessLogicException {
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);
        newEntity.setName(null);
        groupLogic.createGroup(newEntity, neighborhood.getId());
    }

    /**
     * Test for creating a resident with no description
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoDescription() throws BusinessLogicException {
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);
        newEntity.setDescription(null);
        groupLogic.createGroup(newEntity, neighborhood.getId());
    }


    /**
     * Test for updating a resident with no date
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoDate() throws BusinessLogicException {
        GroupEntity newEntity = data.get(0);
        newEntity.setDateCreated(null);
        groupLogic.updateGroup(newEntity, neighborhood.getId());
    }

    /**
     * Test for updating a resident with no name
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoName() throws BusinessLogicException {
        GroupEntity newEntity = data.get(0);
        newEntity.setName(null);
        groupLogic.updateGroup(newEntity, neighborhood.getId());
    }

    /**
     * Test for updating a resident with no description
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoDescription() throws BusinessLogicException {
        GroupEntity newEntity = data.get(0);
        newEntity.setDescription(null);
        groupLogic.updateGroup(newEntity, neighborhood.getId());
    }

}
