/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.ResidentLoginLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
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
public class ResidentLoginLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ResidentLoginLogic residentLoginLogic;

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
    private List<ResidentLoginEntity> data = new ArrayList<>();

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentLoginEntity.class.getPackage())
                .addPackage(ResidentLoginLogic.class.getPackage())
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
        em.createQuery("delete from ResidentLoginEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);
        em.persist(resident);

        for (int i = 0; i < 3; i++) {

            ResidentLoginEntity entity = factory.manufacturePojo(ResidentLoginEntity.class);
            entity.setNeighborhood(neighborhood);
            entity.setResident(resident);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    @Inject
    private ResidentProfileLogic residentLogic;

    @Test
    public void createResidentLoginTest() throws BusinessLogicException {

        // uses the factory to create a ranbdom NeighborhoodEntity object
        ResidentLoginEntity newResidentLogin = factory.manufacturePojo(ResidentLoginEntity.class);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        ResidentLoginEntity result = null;
        try {
            newResidentLogin.setPassword("Password4$");
            result = residentLoginLogic.createResidentLogin(neighborhood.getId(), newResidentLogin);
            ResidentProfileEntity profile = factory.manufacturePojo(ResidentProfileEntity.class);

            profile.setNeighborhood(neighborhood);
            profile.setLogin(result);

            ResidentProfileEntity createdResident = residentLogic.createResident(profile, neighborhood.getId());

    
            profile.setLogin(result);
            
            // verify that the created object is not null
            Assert.assertNotNull(result);
            result.setResident(profile);

            Assert.assertEquals(result.getResident().getName(), createdResident.getName());

        } catch (BusinessLogicException ex) {
            Logger.getLogger(ResidentLoginLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        ResidentLoginEntity entity
                = em.find(ResidentLoginEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

    }

    @Test
    public void getResidentLoginsTest() {
        List<ResidentLoginEntity> list = residentLoginLogic.getResidentLogins(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (ResidentLoginEntity entity : list) {
            boolean found = false;
            for (ResidentLoginEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    @Test
    public void getResidentLoginTest() {
        ResidentLoginEntity entity = data.get(0);
        ResidentLoginEntity resultEntity = residentLoginLogic.getResidentLogin(neighborhood.getId(), entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getUserName(), resultEntity.getUserName());
    }

    @Test
    public void updateResidentLoginTest() throws BusinessLogicException {

        ResidentLoginEntity entity = data.get(0);
        ResidentLoginEntity pojoEntity = factory.manufacturePojo(ResidentLoginEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setPassword("Password5$");

        residentLoginLogic.updateResidentLogin(neighborhood.getId(), pojoEntity);
        ResidentLoginEntity resp = em.find(ResidentLoginEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getUserName(), resp.getUserName());

    }

    @Test
    public void deleteResidentLoginTest() throws BusinessLogicException {
        ResidentLoginEntity entity = data.get(1);
        residentLoginLogic.deleteResidentLogin(neighborhood.getId(), entity.getId());
        ResidentLoginEntity deleted = em.find(ResidentLoginEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}
