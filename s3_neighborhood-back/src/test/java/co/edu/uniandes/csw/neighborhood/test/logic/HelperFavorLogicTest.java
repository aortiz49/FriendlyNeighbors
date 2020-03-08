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
public class HelperFavorLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private HelperFavorLogic helperFavorLogic;

    @Inject
    private FavorLogic favorLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private ResidentProfileEntity helper = new ResidentProfileEntity();
    private List<FavorEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(FavorEntity.class.getPackage())
                .addPackage(HelperFavorLogic.class.getPackage())
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
        em.createQuery("delete from FavorEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        helper = factory.manufacturePojo(ResidentProfileEntity.class);
        helper.setId(1L);
        helper.setFavorsToHelp(new ArrayList<>());
        em.persist(helper);

        for (int i = 0; i < 3; i++) {
            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);
            entity.setCandidates(new ArrayList<>());
            entity.getCandidates().add(helper);
            em.persist(entity);
            data.add(entity);
            helper.getFavorsToHelp().add(entity);
        }
    }

    /**
     * Test to associate an favor with a helper
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addFavorTest() throws BusinessLogicException {
        FavorEntity newFavor = factory.manufacturePojo(FavorEntity.class);
        favorLogic.createFavor(newFavor);
        FavorEntity favorEntity = helperFavorLogic.associateFavorToHelper(helper.getId(), newFavor.getId());
        Assert.assertNotNull(favorEntity);

        Assert.assertEquals(favorEntity.getId(), newFavor.getId());
        Assert.assertEquals(favorEntity.getDescription(), newFavor.getDescription());

        FavorEntity lastFavor = helperFavorLogic.getFavor(helper.getId(), newFavor.getId());

        Assert.assertEquals(lastFavor.getId(), newFavor.getId());

    }

    /**
     * Test for getting a collection of favor entities associated with a helper
     */
    @Test
    public void getFavorsTest() {
        List<FavorEntity> favorEntities = helperFavorLogic.getFavors(helper.getId());

        Assert.assertEquals(data.size(), favorEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(favorEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting an favor entity associated with a a helper
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getFavorTest() throws BusinessLogicException {
        FavorEntity favorEntity = data.get(0);
        FavorEntity favor = helperFavorLogic.getFavor(helper.getId(), favorEntity.getId());
        Assert.assertNotNull(favor);

        Assert.assertEquals(favorEntity.getId(), favor.getId());
        Assert.assertEquals(favorEntity.getDescription(), favor.getDescription());

    }

    /**
     * Test for replacing favors associated with a helper
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceFavorsTest() throws BusinessLogicException {
        List<FavorEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);
            entity.setCandidates(new ArrayList<>());
            entity.getCandidates().add(helper);
            favorLogic.createFavor(entity);
            newCollection.add(entity);
        }
        helperFavorLogic.replaceFavors(helper.getId(), newCollection);
        List<FavorEntity> favorEntities = helperFavorLogic.getFavors(helper.getId());
        for (FavorEntity aNuevaLista : newCollection) {
            Assert.assertTrue(favorEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing an favor from helper
     *
     */
    @Test
    public void removeFavorTest() {
        for (FavorEntity favor : data) {
            helperFavorLogic.removeFavor(helper.getId(), favor.getId());
        }
        Assert.assertTrue(helperFavorLogic.getFavors(helper.getId()).isEmpty());
    }

}
