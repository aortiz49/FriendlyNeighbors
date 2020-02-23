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
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        // creates a factory to generate objects with random data
        PodamFactory factory = new PodamFactoryImpl();

        for (int i = 0; i < 3; i++) {
           
            ResidentProfileEntity entity
                    = factory.manufacturePojo(ResidentProfileEntity.class);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    @Test
    public void createResidentTest() {

        
        try{
            
            
        NeighborhoodEntity neigh   = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);
                   
         // uses the factory to create a ranbdom NeighborhoodEntity object
        ResidentProfileEntity newResident = factory.manufacturePojo(ResidentProfileEntity.class);
        
        newResident.setNeighborhood(neigh);
        
              
        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        ResidentProfileEntity result = residentLogic.createResident(newResident);

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
        }
        
        catch(BusinessLogicException ex){
            fail(ex.getMessage());
    }
}
    
    
    
    @Test(expected = BusinessLogicException.class)
    public void createResidentsWithSameEmail() throws BusinessLogicException {
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        newEntity.setName(data.get(0).getName());
        residentLogic.createResident(newEntity);
    }


    @Test
    public void getResidentsTest() {
        List<ResidentProfileEntity> list = residentLogic.getResidents();
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


    @Test
    public void getResidentTest() {
        ResidentProfileEntity entity = data.get(0);
        ResidentProfileEntity resultEntity = residentLogic.getResident(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }


    @Test
    public void updateResidentTest() {
        try {
            ResidentProfileEntity entity = data.get(0);
            ResidentProfileEntity pojoEntity = factory.manufacturePojo(ResidentProfileEntity.class);
            pojoEntity.setId(entity.getId());
            residentLogic.updateResident(pojoEntity);
            ResidentProfileEntity resp = em.find(ResidentProfileEntity.class, entity.getId());
            Assert.assertEquals(pojoEntity.getId(), resp.getId());
            Assert.assertEquals(pojoEntity.getName(), resp.getName());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(ResidentProfileLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Test
    public void deleteResidentTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(1);
        residentLogic.deleteResident(entity.getId());
        ResidentProfileEntity deleted = em.find(ResidentProfileEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}