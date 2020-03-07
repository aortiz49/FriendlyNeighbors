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
public class PostViewerLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private PostViewerLogic postResidentProfileLogic;

    @Inject
    private ResidentProfileLogic viewerLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private PostEntity post = new PostEntity();
    private List<ResidentProfileEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(PostEntity.class.getPackage())
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(PostViewerLogic.class.getPackage())
                .addPackage(PostPersistence.class.getPackage())
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
        post = factory.manufacturePojo(PostEntity.class);
        post.setId(1L);
        post.setViewers(new ArrayList<>());
        em.persist(post);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            entity.setPostsToView(new ArrayList<>());
            entity.getPostsToView().add(post);
            em.persist(entity);
            data.add(entity);
            post.getViewers().add(entity);
        }
    }

    /**
     * Test to associate a viewer with a post
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addResidentTest() throws BusinessLogicException {
        ResidentProfileEntity newResidentProfile = factory.manufacturePojo(ResidentProfileEntity.class);

        NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);

        newResidentProfile.setNeighborhood(neigh);

        viewerLogic.createResident(newResidentProfile);

        ResidentProfileEntity viewerEntity = postResidentProfileLogic.associateResidentProfileToPost(post.getId(), newResidentProfile.getId());
        Assert.assertNotNull(viewerEntity);

        Assert.assertEquals(viewerEntity.getId(), newResidentProfile.getId());
        Assert.assertEquals(viewerEntity.getAddress(), newResidentProfile.getAddress());

        ResidentProfileEntity lastResident = postResidentProfileLogic.getResidentProfile(post.getId(), newResidentProfile.getId());

        Assert.assertEquals(lastResident.getId(), newResidentProfile.getId());

    }

    /**
     * Test for getting a collection of viewer entities associated with a post
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> viewerEntities = postResidentProfileLogic.getResidentProfiles(post.getId());

        Assert.assertEquals(data.size(), viewerEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(viewerEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting a viewer entity associated with a post
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getResidentTest() throws BusinessLogicException {
        ResidentProfileEntity viewerEntity = data.get(0);
        ResidentProfileEntity viewer = postResidentProfileLogic.getResidentProfile(post.getId(), viewerEntity.getId());
        Assert.assertNotNull(viewer);

        Assert.assertEquals(viewerEntity.getId(), viewer.getId());
        Assert.assertEquals(viewerEntity.getAddress(), viewer.getAddress());

    }

    /**
     * Test for replacing viewers associated with a post
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceResidentProfilesTest() throws BusinessLogicException {
        List<ResidentProfileEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setPostsToView(new ArrayList<>());
            entity.getPostsToView().add(post);

            NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
            neighPersistence.create(neigh);

            entity.setNeighborhood(neigh);
            viewerLogic.createResident(entity);

            newCollection.add(entity);
        }
        postResidentProfileLogic.replaceResidentProfiles(post.getId(), newCollection);
        List<ResidentProfileEntity> viewerEntities = postResidentProfileLogic.getResidentProfiles(post.getId());
        for (ResidentProfileEntity aNuevaLista : newCollection) {
            Assert.assertTrue(viewerEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing a viewer from a post
     *
     */
    @Test
    public void removeResidentTest() {
        for (ResidentProfileEntity viewer : data) {
            postResidentProfileLogic.removeResidentProfile(post.getId(), viewer.getId());
        }
        Assert.assertTrue(postResidentProfileLogic.getResidentProfiles(post.getId()).isEmpty());
    }

}
