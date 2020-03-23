/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.ServiceLogic;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class ServiceLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ServiceLogic serviceLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    private NeighborhoodEntity neighborhood;
      
    private ResidentProfileEntity resident;

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
    private List<ServiceEntity> data = new ArrayList<>();

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ServiceEntity.class.getPackage())
                .addPackage(ServiceLogic.class.getPackage())
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
        em.createQuery("delete from ServiceEntity").executeUpdate();
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

        residentPersistence.create(resident);


        for (int i = 0; i < 3; i++) {

            ServiceEntity entity = factory.manufacturePojo(ServiceEntity.class);
            
            entity.setAuthor(resident);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a service
     */
    @Test
    public void createServiceTest() {

        NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);

        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neigh);

        residentPersistence.create(resident);

        // uses the factory to create a ranbdom NeighborhoodEntity object
        ServiceEntity newService = factory.manufacturePojo(ServiceEntity.class);
        newService.setAuthor(resident);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        ServiceEntity result = null;
        try {
            result = serviceLogic.createService(newService, resident.getId(), neigh.getId());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(ServiceLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        ServiceEntity entity = em.find(ServiceEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newService.getDescription(), entity.getDescription());

    }

    /**
     * Test for getting ll services
     */
    @Test
    public void getServicesTest() {
        List<ServiceEntity> list = serviceLogic.getServices(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (ServiceEntity entity : list) {
            boolean found = false;
            for (ServiceEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting  service
     */
    @Test
    public void getServiceTest() {
        ServiceEntity entity = data.get(0);
        ServiceEntity resultEntity = serviceLogic.getService(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
    }

    /**
     * Test for updating a service
     *
     * @throws BusinessLogicException
     */
    @Test
    public void updateServiceTest() throws BusinessLogicException {

        ServiceEntity entity = data.get(0);
        ServiceEntity pojoEntity = factory.manufacturePojo(ServiceEntity.class);
        pojoEntity.setId(entity.getId());
        serviceLogic.updateService(pojoEntity, neighborhood.getId());
        ServiceEntity resp = em.find(ServiceEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getDescription(), resp.getDescription());

    }

    /**
     * Test for deleting a service
     *
     * @throws BusinessLogicException
     */
    @Test
    public void deleteServiceTest() throws BusinessLogicException {
        ServiceEntity entity = data.get(1);
        serviceLogic.deleteService(entity.getId(), neighborhood.getId());
        ServiceEntity deleted = em.find(ServiceEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }


    /**
     * Tests the creation of a Service with no resident.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceNoResidentTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = data.get(0);
        // persist the created service, should not work
        serviceLogic.createService(newEntity, new Long(-100), new Long(-100));
    }

    /**
     * Tests the creation of a Service with  long description.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceLongDescriptionTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = data.get(0);
        newEntity.setDescription("0XzLvMu90HC5w5gtFYN3qnwyez8yN1NVCSYU4fdD00Abl"
                + "PZrkreAumJrnkVQtXHhGSWnk9BGv1nItmaRhTtRbTHBOKOQf4NvaQT8u5i6L"
                + "aax6lK94kEwEdiFoiVbV4MVoFrL0asdasdafafURiGjNZ97gOOlgIaaYZZC7"
                + "L4jVLOdakl"
                + "Agb2K3RRY0LQCG8DYmE1qyvBirDRcfnJePVHB7qWERBWJNbcGBdMdWyYlj2"
                + "Lq1cIrfuVGRYnlM8R4j0RuIA1KAwUfEcTx");

        // persist the created service, should not work
        serviceLogic.createService(newEntity, resident.getId(), neighborhood.getId());
    }

    /**
     * Tests the creation of a Service without a description.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceNoDescriptionTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = data.get(0);
        newEntity.setDescription(null);

        // persist the created service, should not work
        serviceLogic.createService(newEntity, resident.getId(), neighborhood.getId());
    }
}
