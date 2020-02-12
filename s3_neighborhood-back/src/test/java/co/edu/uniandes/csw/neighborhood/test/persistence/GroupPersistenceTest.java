
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
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
 * Persistence test for Group
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class GroupPersistenceTest {

    @Inject
    private GroupPersistence groupPersistence;
    

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<GroupEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(GroupEntity.class.getPackage())
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
            em.joinTransaction();
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
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Group.
     */
    @Test
    public void createGroupTest() {
        PodamFactory factory = new PodamFactoryImpl();
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);
        GroupEntity result = groupPersistence.create(newEntity);

        Assert.assertNotNull(result);

        GroupEntity entity = em.find(GroupEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
    }
    
    /**
     * Test for retrieving all groups from DB.
     */
        @Test
    public void findAllTest() {
        List<GroupEntity> list = groupPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (GroupEntity ent : list) {
            boolean found = false;
            for (GroupEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Group.
     */
    @Test
    public void getGroupTest() {
        GroupEntity entity = data.get(0);
        GroupEntity newEntity = groupPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getDescription(), newEntity.getDescription());
    }

     /**
     * Test for updating a Group.
     */
    @Test
    public void updateGroupTest() {
        GroupEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);

        newEntity.setId(entity.getId());

        groupPersistence.update(newEntity);

        GroupEntity resp = em.find(GroupEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
        Assert.assertEquals(newEntity.getDescription(), resp.getDescription());
    }
    
     /**
     * Test for deleting a Group.
     */
    @Test
    public void deleteGroupTest() {
        GroupEntity entity = data.get(0);
        groupPersistence.delete(entity.getId());
        GroupEntity deleted = em.find(GroupEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
     
    
}