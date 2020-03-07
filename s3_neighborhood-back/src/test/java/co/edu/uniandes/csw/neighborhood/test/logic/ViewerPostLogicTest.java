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
public class ViewerPostLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ViewerPostLogic viewerPostLogic;

    @Inject
    private PostLogic postLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private ResidentProfileEntity viewer = new ResidentProfileEntity();
    private List<PostEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(PostEntity.class.getPackage())
                .addPackage(ViewerPostLogic.class.getPackage())
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
        em.createQuery("delete from PostEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        viewer = factory.manufacturePojo(ResidentProfileEntity.class);
        viewer.setId(1L);
        viewer.setPostsToView(new ArrayList<>());
        em.persist(viewer);

        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);
            entity.setViewers(new ArrayList<>());
            entity.getViewers().add(viewer);
            em.persist(entity);
            data.add(entity);
            viewer.getPostsToView().add(entity);
        }
    }

    /**
     * Test to associate an post with a viewer
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addPostTest() throws BusinessLogicException {
        PostEntity newPost = factory.manufacturePojo(PostEntity.class);
        postLogic.createPost(newPost);
        PostEntity postEntity = viewerPostLogic.associatePostToAttenddee(viewer.getId(), newPost.getId());
        Assert.assertNotNull(postEntity);

        Assert.assertEquals(postEntity.getId(), newPost.getId());
        Assert.assertEquals(postEntity.getDescription(), newPost.getDescription());

        PostEntity lastPost = viewerPostLogic.getPost(viewer.getId(), newPost.getId());

        Assert.assertEquals(lastPost.getId(), newPost.getId());

    }

    /**
     * Test for getting a collection of post entities associated with a viewer
     */
    @Test
    public void getPostsTest() {
        List<PostEntity> postEntities = viewerPostLogic.getPosts(viewer.getId());

        Assert.assertEquals(data.size(), postEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(postEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting an post entity associated with a a viewer
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getPostTest() throws BusinessLogicException {
        PostEntity postEntity = data.get(0);
        PostEntity post = viewerPostLogic.getPost(viewer.getId(), postEntity.getId());
        Assert.assertNotNull(post);

        Assert.assertEquals(postEntity.getId(), post.getId());
        Assert.assertEquals(postEntity.getDescription(), post.getDescription());

    }

    /**
     * Test for replacing posts associated with a viewer
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replacePostsTest() throws BusinessLogicException {
        List<PostEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);
            entity.setViewers(new ArrayList<>());
            entity.getViewers().add(viewer);
            postLogic.createPost(entity);
            newCollection.add(entity);
        }
        viewerPostLogic.replacePosts(viewer.getId(), newCollection);
        List<PostEntity> postEntities = viewerPostLogic.getPosts(viewer.getId());
        for (PostEntity aNuevaLista : newCollection) {
            Assert.assertTrue(postEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing an post from viewer
     *
     */
    @Test
    public void removePostTest() {
        for (PostEntity post : data) {
            viewerPostLogic.removePost(viewer.getId(), post.getId());
        }
        Assert.assertTrue(viewerPostLogic.getPosts(viewer.getId()).isEmpty());
    }

}
