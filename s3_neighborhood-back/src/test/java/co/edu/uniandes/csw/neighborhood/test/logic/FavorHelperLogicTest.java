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
public class FavorHelperLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private FavorHelperLogic favorResidentProfileLogic;

    @Inject
    private ResidentProfileLogic helperLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private FavorEntity favor = new FavorEntity();
    private List<ResidentProfileEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(FavorEntity.class.getPackage())
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(FavorHelperLogic.class.getPackage())
                .addPackage(FavorPersistence.class.getPackage())
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
        em.createQuery("delete from FavorEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        favor = factory.manufacturePojo(FavorEntity.class);
        favor.setId(1L);
        favor.setCandidates(new ArrayList<>());
        em.persist(favor);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            entity.setFavorsToHelp(new ArrayList<>());
            entity.getFavorsToHelp().add(favor);
            em.persist(entity);
            data.add(entity);
            favor.getCandidates().add(entity);
        }
    }

    /**
     * Test to associate a helper with an favor
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

        helperLogic.createResident(newResidentProfile);

        ResidentProfileEntity helperEntity = favorResidentProfileLogic.associateResidentProfileToFavor(favor.getId(), newResidentProfile.getId());
        Assert.assertNotNull(helperEntity);

        Assert.assertEquals(helperEntity.getId(), newResidentProfile.getId());
        Assert.assertEquals(helperEntity.getAddress(), newResidentProfile.getAddress());

        ResidentProfileEntity lastResident = favorResidentProfileLogic.getResidentProfile(favor.getId(), newResidentProfile.getId());

        Assert.assertEquals(lastResident.getId(), newResidentProfile.getId());

    }

    /**
     * Test for getting a collection of helper entities associated with an favor
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> helperEntities = favorResidentProfileLogic.getResidentProfiles(favor.getId());

        Assert.assertEquals(data.size(), helperEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(helperEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting a helper entity associated with an favor
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getResidentTest() throws BusinessLogicException {
        ResidentProfileEntity helperEntity = data.get(0);
        ResidentProfileEntity helper = favorResidentProfileLogic.getResidentProfile(favor.getId(), helperEntity.getId());
        Assert.assertNotNull(helper);

        Assert.assertEquals(helperEntity.getId(), helper.getId());
        Assert.assertEquals(helperEntity.getAddress(), helper.getAddress());

    }

    /**
     * Test for replacing helpers associated with an favor
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceResidentProfilesTest() throws BusinessLogicException {
        List<ResidentProfileEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setFavorsToHelp(new ArrayList<>());
            entity.getFavorsToHelp().add(favor);

            NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
            neighPersistence.create(neigh);

            entity.setNeighborhood(neigh);
            helperLogic.createResident(entity);

            newCollection.add(entity);
        }
        favorResidentProfileLogic.replaceResidentProfiles(favor.getId(), newCollection);
        List<ResidentProfileEntity> helperEntities = favorResidentProfileLogic.getResidentProfiles(favor.getId());
        for (ResidentProfileEntity aNuevaLista : newCollection) {
            Assert.assertTrue(helperEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing a helper from an favor
     *
     */
    @Test
    public void removeResidentTest() {
        for (ResidentProfileEntity helper : data) {
            favorResidentProfileLogic.removeResidentProfile(favor.getId(), helper.getId());
        }
        Assert.assertTrue(favorResidentProfileLogic.getResidentProfiles(favor.getId()).isEmpty());
    }

}
