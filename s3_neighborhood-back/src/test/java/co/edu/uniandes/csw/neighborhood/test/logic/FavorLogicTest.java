/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class FavorLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private FavorLogic favorLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

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
    private List<FavorEntity> data = new ArrayList<>();

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(FavorEntity.class.getPackage())
                .addPackage(FavorLogic.class.getPackage())
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
        em.createQuery("delete from FavorEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);
        

        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);

        residentPersistence.create(resident);


        for (int i = 0; i < 3; i++) {

            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);
            
            entity.setAuthor(resident);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a favor
     */
    @Test
    public void createFavorTest() {

        NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);

        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neigh);

        residentPersistence.create(resident);

        // uses the factory to create a ranbdom NeighborhoodEntity object
        FavorEntity newFavor = factory.manufacturePojo(FavorEntity.class);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        FavorEntity result = null;
        try {
            result = favorLogic.createFavor(newFavor, resident.getId(), neigh.getId());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(FavorLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        FavorEntity entity = em.find(FavorEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newFavor.getDescription(), entity.getDescription());

    }

    /**
     * Test for getting ll favors
     */
    @Test
    public void getFavorsTest() {
        List<FavorEntity> list = favorLogic.getFavors(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (FavorEntity entity : list) {
            boolean found = false;
            for (FavorEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting  favor
     */
    @Test
    public void getFavorTest() {
        FavorEntity entity = data.get(0);
        FavorEntity resultEntity = favorLogic.getFavor(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
    }

    /**
     * Test for updating a favor
     *
     * @throws BusinessLogicException
     */
    @Test
    public void updateFavorTest() throws BusinessLogicException {

        FavorEntity entity = data.get(0);
        FavorEntity pojoEntity = factory.manufacturePojo(FavorEntity.class);
        pojoEntity.setId(entity.getId());
        favorLogic.updateFavor(pojoEntity, neighborhood.getId());
        FavorEntity resp = em.find(FavorEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getDescription(), resp.getDescription());

    }

    /**
     * Test for deleting a favor
     *
     * @throws BusinessLogicException
     */
    @Test
    public void deleteFavorTest() throws BusinessLogicException {
        FavorEntity entity = data.get(1);
        favorLogic.deleteFavor(entity.getId(), neighborhood.getId());
        FavorEntity deleted = em.find(FavorEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}
