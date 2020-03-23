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
    private ViewerPostLogic residentPostLogic;

    @Inject
    private PostLogic postLogic;


    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;
    

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private ResidentProfileEntity resident = new ResidentProfileEntity();
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
        em.createQuery("delete from PostEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setId(1L);
        resident.setPostsToView(new ArrayList<>());
        resident.setNeighborhood(neighborhood);


        em.persist(resident);

        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);
            entity.setAuthor(resident);

            entity.setViewers(new ArrayList<>());
            entity.getViewers().add(resident);

            em.persist(entity);
            data.add(entity);
            resident.getPostsToView().add(entity);
        }

    }

    /**
     * Test to associate post with  resident
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addPostTest() throws BusinessLogicException {
        PostEntity newPost = factory.manufacturePojo(PostEntity.class);
        
        postLogic.createPost(newPost, resident.getId(), neighborhood.getId());

        PostEntity postEntity = residentPostLogic.associatePostToViewer(resident.getId(), newPost.getId(), neighborhood.getId());
        Assert.assertNotNull(postEntity);

        Assert.assertEquals(postEntity.getId(), newPost.getId());
        Assert.assertEquals(postEntity.getDescription(), newPost.getDescription());

        PostEntity lastPost = residentPostLogic.getPost(resident.getId(), newPost.getId(), neighborhood.getId());

        Assert.assertEquals(lastPost.getId(), newPost.getId());

    }

    /**
     * Test for getting  collection of post entities associated with 
     * resident
     */
    @Test
    public void getPostsTest() {
        List<PostEntity> postEntities = residentPostLogic.getPosts(resident.getId(), neighborhood.getId());

        Assert.assertEquals(data.size(), postEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(postEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting  post entity associated with  resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getPostTest() throws BusinessLogicException {
        PostEntity postEntity = data.get(0);
        PostEntity post = residentPostLogic.getPost(resident.getId(), postEntity.getId(), neighborhood.getId());
        Assert.assertNotNull(post);

        Assert.assertEquals(postEntity.getId(), post.getId());
        Assert.assertEquals(postEntity.getDescription(), post.getDescription());

    }

    /**
     * Test for replacing posts associated with  resident
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replacePostsTest() throws BusinessLogicException {
        List<PostEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);
            entity.setViewers(new ArrayList<>());
            entity.getViewers().add(resident);
            postLogic.createPost(entity, resident.getId(), neighborhood.getId());
            newCollection.add(entity);
        }
        residentPostLogic.replacePosts(resident.getId(), newCollection, neighborhood.getId());
        List<PostEntity> postEntities = residentPostLogic.getPosts(resident.getId(), neighborhood.getId());
        for (PostEntity aNuevaLista : newCollection) {
            Assert.assertTrue(postEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing  post from resident
     *
     */
    @Test
    public void removePostTest() {
        for (PostEntity post : data) {
            residentPostLogic.removePost(resident.getId(), post.getId(), neighborhood.getId());
        }
        Assert.assertTrue(residentPostLogic.getPosts(resident.getId(), neighborhood.getId()).isEmpty());
    }

}
