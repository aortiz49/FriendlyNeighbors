/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.ResidentLoginLogic;
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
    @Inject
    private ResidentLoginLogic loginLogic;

    /**
     * Test for creating a resident
     */
    @Test
    public void createResidentTest() {

        try {

            ResidentProfileEntity newResidentProfile = factory.manufacturePojo(ResidentProfileEntity.class);
            ResidentLoginEntity theLogin = factory.manufacturePojo(ResidentLoginEntity.class);
            theLogin.setPassword("Gsnnah6!=");

            theLogin = loginLogic.createResidentLogin(neighborhood.getId(), theLogin);

            newResidentProfile.setLogin(theLogin);

            newResidentProfile.setNeighborhood(neighborhood);
            ResidentProfileEntity result = residentLogic.createResident(newResidentProfile, neighborhood.getId());

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
            Assert.assertEquals(newResidentProfile.getName(), entity.getName());
        } catch (BusinessLogicException ex) {
            fail(ex.getMessage());
        }
    }

   
}
