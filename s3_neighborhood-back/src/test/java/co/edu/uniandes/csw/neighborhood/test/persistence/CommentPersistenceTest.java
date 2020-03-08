
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.persistence.CommentPersistence;
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
 * Persistence test for Comment
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class CommentPersistenceTest {

    @Inject
    private CommentPersistence commentPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<CommentEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(CommentEntity.class.getPackage())
                .addPackage(CommentPersistence.class.getPackage())
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
        em.createQuery("delete from CommentEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            CommentEntity entity = factory.manufacturePojo(CommentEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Comment.
     */
    @Test
    public void createCommentTest() {
        PodamFactory factory = new PodamFactoryImpl();
        CommentEntity newEntity = factory.manufacturePojo(CommentEntity.class);
        CommentEntity result = commentPersistence.create(newEntity);

        Assert.assertNotNull(result);

        CommentEntity entity = em.find(CommentEntity.class, result.getId());

        Assert.assertEquals(newEntity.getText(), entity.getText());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getPost(), entity.getPost());
        Assert.assertEquals(newEntity.getDate(), entity.getDate());

    }
    
     /**
     * Test for retrieving all comments from DB.
     */
        @Test
    public void findAllTest() {
        List<CommentEntity> list = commentPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (CommentEntity ent : list) {
            boolean found = false;
            for (CommentEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Comment.
     */
    @Test
    public void getCommentTest() {
        CommentEntity entity = data.get(0);
        CommentEntity newEntity = commentPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDate(), newEntity.getDate());
        Assert.assertEquals(entity.getText(), newEntity.getText());
    }

     /**
     * Test for updating a Comment.
     */
    @Test
    public void updateCommentTest() {
        CommentEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        CommentEntity newEntity = factory.manufacturePojo(CommentEntity.class);

        newEntity.setId(entity.getId());

        commentPersistence.update(newEntity);

        CommentEntity resp = em.find(CommentEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getDate(), resp.getDate());
    }
    
     /**
     * Test for deleting a Comment.
     */
    @Test
    public void deleteCommentTest() {
        CommentEntity entity = data.get(0);
        commentPersistence.delete(entity.getId());
        CommentEntity deleted = em.find(CommentEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
    
    
    
}