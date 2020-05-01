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
    private ResidentProfileLogic residentLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private PostEntity post = new PostEntity();
    private List<ResidentProfileEntity> data = new ArrayList<>();

    private ResidentLoginEntity login;
        @Inject
    private ResidentLoginLogic loginLogic;

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
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
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        post = factory.manufacturePojo(PostEntity.class);
        post.setId(1L);
        post.setViewers(new ArrayList<>());

        login = factory.manufacturePojo(ResidentLoginEntity.class);
        login.setNeighborhood(neighborhood);
        em.persist(login);

        ResidentProfileEntity author = factory.manufacturePojo(ResidentProfileEntity.class);
        author.setNeighborhood(neighborhood);
        author.setLogin(login);

        em.persist(author);

        post.setAuthor(author);

        em.persist(post);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            entity.setPostsToView(new ArrayList<>());
            entity.getPostsToView().add(post);
            entity.setLogin(login);
            entity.setNeighborhood(neighborhood);

            em.persist(entity);
            data.add(entity);
            post.getViewers().add(entity);
        }
    }

    /**
     * Test to associate resident with post
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addResidentTest() throws BusinessLogicException {
        ResidentProfileEntity newResidentProfile = factory.manufacturePojo(ResidentProfileEntity.class);
        ResidentLoginEntity theLogin = factory.manufacturePojo(ResidentLoginEntity.class);
        theLogin.setPassword("Gsnnah6!=");

        theLogin = loginLogic.createResidentLogin(neighborhood.getId(), theLogin);
        
        newResidentProfile.setLogin(theLogin);
        
        newResidentProfile.setNeighborhood(neighborhood);

        residentLogic.createResident(newResidentProfile, neighborhood.getId());

        ResidentProfileEntity residentEntity = postResidentProfileLogic.associateViewerToPost(post.getId(), newResidentProfile.getId(), neighborhood.getId());
        Assert.assertNotNull(residentEntity);

        Assert.assertEquals(residentEntity.getId(), newResidentProfile.getId());
        Assert.assertEquals(residentEntity.getAddress(), newResidentProfile.getAddress());

        ResidentProfileEntity lastResident = postResidentProfileLogic.getViewer(post.getId(), newResidentProfile.getId(), neighborhood.getId());

        Assert.assertEquals(lastResident.getId(), newResidentProfile.getId());

    }

    /**
     * Test for getting collection of resident entities associated with post
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> residentEntities = postResidentProfileLogic.getViewers(post.getId(), neighborhood.getId());

        Assert.assertEquals(data.size(), residentEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(residentEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting resident entity associated with post
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getResidentTest() throws BusinessLogicException {
        ResidentProfileEntity residentEntity = data.get(0);
        ResidentProfileEntity resident = postResidentProfileLogic.getViewer(post.getId(), residentEntity.getId(), neighborhood.getId());
        Assert.assertNotNull(resident);

        Assert.assertEquals(residentEntity.getId(), resident.getId());
        Assert.assertEquals(residentEntity.getAddress(), resident.getAddress());

    }



    /**
     * Test for removing resident from post
     *
     */
    @Test
    public void removeResidentTest() {
        for (ResidentProfileEntity resident : data) {
            postResidentProfileLogic.removeViewer(post.getId(), resident.getId(), neighborhood.getId());
        }
        Assert.assertTrue(postResidentProfileLogic.getViewers(post.getId(), neighborhood.getId()).isEmpty());
    }

}
