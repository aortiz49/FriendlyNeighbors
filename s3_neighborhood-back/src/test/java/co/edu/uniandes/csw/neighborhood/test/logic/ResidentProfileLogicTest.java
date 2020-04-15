/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
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
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class ResidentProfileLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ResidentProfileLogic residentLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

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
    private List<ResidentProfileEntity> data = new ArrayList<>();

    private ResidentLoginEntity login;

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(ResidentProfileLogic.class.getPackage())
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        login = factory.manufacturePojo(ResidentLoginEntity.class);
        login.setNeighborhood(neighborhood);
        em.persist(login);

        for (int i = 0; i < 3; i++) {

            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setLogin(login);
            entity.setNeighborhood(neighborhood);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a resident
     */
    @Test
    public void createResidentTest() {

        try {

            NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
            neighPersistence.create(neigh);

            // uses the factory to create a ranbdom NeighborhoodEntity object
            ResidentProfileEntity newResident = factory.manufacturePojo(ResidentProfileEntity.class);

            // invokes the method to be tested (create): it creates a table in the 
            // database. The parameter of this method is the newly created object from 
            // the podam factory which has an id associated to it. 
            ResidentProfileEntity result = residentLogic.createResident(newResident, neighborhood.getId(), login.getId());

            // verify that the created object is not null
            Assert.assertNotNull(result);

            // using the entity manager, it searches the database for the object 
            // matching the id of the newly created factory object
            ResidentProfileEntity entity
                    = em.find(ResidentProfileEntity.class, result.getId());

            // verifies that the object exists in the database
            Assert.assertNotNull(entity);

            // compares if the name of the new object generated by the factory matched
            // the name of the object in the database
            Assert.assertEquals(newResident.getName(), entity.getName());
        } catch (BusinessLogicException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test for creating a resident with existing email
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithSameEmail() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);

        newEntity.setEmail(data.get(0).getEmail());
        residentLogic.createResident(newEntity, neighborhood.getId(), login.getId());
    }

    /**
     * Test for getting ll residents
     */
    @Test
    public void getResidentsTest() {
        List<ResidentProfileEntity> list = residentLogic.getResidents(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (ResidentProfileEntity entity : list) {
            boolean found = false;
            for (ResidentProfileEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting resident
     */
    @Test
    public void getResidentTest() {
        ResidentProfileEntity entity = data.get(0);
        ResidentProfileEntity resultEntity = residentLogic.getResident(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Test for updating a resident
     */
    @Test
    public void updateResidentTest() {
        try {
            ResidentProfileEntity entity = data.get(0);
            ResidentProfileEntity pojoEntity = factory.manufacturePojo(ResidentProfileEntity.class);
            pojoEntity.setId(entity.getId());
            pojoEntity.setLogin(login);

            residentLogic.updateResident(pojoEntity, neighborhood.getId());
            ResidentProfileEntity resp = em.find(ResidentProfileEntity.class, entity.getId());
            Assert.assertEquals(pojoEntity.getId(), resp.getId());
            Assert.assertEquals(pojoEntity.getName(), resp.getName());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(ResidentProfileLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test for deleting a resident
     */
    @Test
    public void deleteResidentTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(1);
        residentLogic.deleteResident(entity.getId(), neighborhood.getId());
        ResidentProfileEntity deleted = em.find(ResidentProfileEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Test for creating a resident with no email
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoEmail() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);
        newEntity.setEmail(null);
        residentLogic.createResident(newEntity, neighborhood.getId(), login.getId());
    }

    /**
     * Test for creating a resident with duplicated email
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithDuplicatedEmail() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);

        ResidentProfileEntity newEntity2 = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity2.setLogin(login);
        newEntity.setEmail(newEntity2.getEmail());
        residentLogic.createResident(newEntity, neighborhood.getId(), login.getId());
        residentLogic.createResident(newEntity2, neighborhood.getId(), login.getId());
    }

    /**
     * Test for creating a resident with no phone
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoPhone() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);
        newEntity.setPhoneNumber(null);
        residentLogic.createResident(newEntity, neighborhood.getId(), login.getId());
    }

    /**
     * Test for creating a resident with no name
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoName() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);
        newEntity.setName(null);
        residentLogic.createResident(newEntity, neighborhood.getId(), login.getId());
    }

    /**
     * Test for creating a resident with no proof
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoProof() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);

        newEntity.setProofOfResidence(null);
        residentLogic.createResident(newEntity, neighborhood.getId(),login.getId());
    }

    /**
     * Test for creating a resident with no neighborhood
     */
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithNoNeighborhood() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setLogin(login);

        residentLogic.createResident(newEntity, new Long(-100),login.getId());
    }

    /**
     * Test for updating a resident with no Phone
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoPhone() throws BusinessLogicException {
        ResidentProfileEntity newEntity = data.get(0);
        newEntity.setPhoneNumber(null);
        residentLogic.updateResident(newEntity, neighborhood.getId());
    }

    /**
     * Test for updating a resident with no name
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoName() throws BusinessLogicException {
        ResidentProfileEntity newEntity = data.get(0);
        newEntity.setName(null);
        residentLogic.updateResident(newEntity, neighborhood.getId());
    }

    /**
     * Test for updating a resident with no proof
     */
    @Test(expected = BusinessLogicException.class)
    public void updateResidentsWithNoProof() throws BusinessLogicException {
        ResidentProfileEntity newEntity = data.get(0);
        newEntity.setProofOfResidence(null);
        residentLogic.updateResident(newEntity, neighborhood.getId());
    }

    /**
     * Test for finding a resident by email
     */
    @Test
    public void getResidentByEmailTest() {

        ResidentProfileEntity entity = data.get(0);
        ResidentProfileEntity newEntity = residentLogic.getResidentByEmail(entity.getEmail());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getNickname(), newEntity.getNickname());
        Assert.assertEquals(entity.getEmail(), newEntity.getEmail());
        Assert.assertEquals(entity.getPhoneNumber(), newEntity.getPhoneNumber());
        Assert.assertEquals(entity.getPreferences(), newEntity.getPreferences());
        Assert.assertEquals(entity.getProofOfResidence(), newEntity.getProofOfResidence());

    }
}
