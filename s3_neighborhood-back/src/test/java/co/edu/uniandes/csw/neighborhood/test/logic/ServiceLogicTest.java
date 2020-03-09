/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.ejb.BusinessLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ServiceLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
 * Runs tests for NeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class ServiceLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Creates BusinessEntity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Injects SServiceLogic objects.
     */
    @Inject
    private ServiceLogic serviceLogic;

    /**
     * Entity manager to communicate with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    private UserTransaction utx;

    /**
     * An array containing the set of data used for the tests.
     */
    private List<ServiceEntity> services = new ArrayList<>();

    /**
     *
     * Configures the test.
     *
     * @return the jar that Arquillian will deploy on the embedded Payara server. It contains the
     * classes, the database descriptor, and the beans.xml to resolve injection dependencies.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ServiceEntity.class.getPackage())
                .addPackage(ServiceLogic.class.getPackage())
                .addPackage(ServicePersistence.class.getPackage())
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
        } catch (IllegalStateException | SecurityException | HeuristicMixedException
                | HeuristicRollbackException | NotSupportedException | RollbackException
                | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException e1) {
            }
        }
    }

    /**
     * Clears tables involved in tests
     */
    private void clearData() {
        em.createQuery("delete from ServiceEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {

        // creates 3 services 
        for (int i = 0; i < 3; i++) {
            ServiceEntity entity = factory.manufacturePojo(ServiceEntity.class);
            em.persist(entity);
            services.add(entity);
        }

        // creates the resident where the business is located
        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setName("Josh Trager");
        em.persist(resident);

        // add the service to the resident
        resident.getServices().add(services.get(0));

        // add the author to the service
        services.get(0).setAuthor(resident);

    }

    /**
     * Tests the creation of a Service.
     */
    @Test
    public void createServiceTest() throws BusinessLogicException {

        // creates a random business
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);

        // sets the author of the service
        newEntity.setAuthor(services.get(0).getAuthor());

        // persist the created business, should not be null
        ServiceEntity result = serviceLogic.createService(newEntity);
        Assert.assertNotNull(result);

        // locate the persisted business
        ServiceEntity entity = em.find(ServiceEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getTitle(), entity.getTitle());
        Assert.assertEquals(newEntity.getAvailability(), entity.getAvailability());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());

    }

    /**
     * Tests the creation of a Service with no resident.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceNoResidentTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);

        // persist the created service, should not work
        serviceLogic.createService(newEntity);
    }

    /**
     * Tests the creation of a Service with a long description.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceLongDescriptionTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);
        newEntity.setDescription("0XzLvMu90HC5w5gtFYN3qnwyez8yN1NVCSYU4fdD00Abl"
                + "PZrkreAumJrnkVQtXHhGSWnk9BGv1nItmaRhTtRbTHBOKOQf4NvaQT8u5i6L"
                + "aax6lK94kEwEdiFoiVbV4MVoFrL0asdasdafafURiGjNZ97gOOlgIaaYZZC7"
                + "L4jVLOdakl"
                + "Agb2K3RRY0LQCG8DYmE1qyvBirDRcfnJePVHB7qWERBWJNbcGBdMdWyYlj2"
                + "Lq1cIrfuVGRYnlM8R4j0RuIA1KAwUfEcTx");

        // persist the created service, should not work
        serviceLogic.createService(newEntity);
    }

    /**
     * Tests the creation of a Service without a description.
     */
    @Test(expected = BusinessLogicException.class)
    public void createServiceNoDescriptionTest() throws BusinessLogicException {

        // creates a random service
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);
        newEntity.setDescription(null);

        // persist the created service, should not work
        serviceLogic.createService(newEntity);
    }

    /**
     * Returns all the services in the database.
     *
     */
    @Test
    public void getServicesTest() {
        List<ServiceEntity> list = serviceLogic.getServices();
        Assert.assertEquals(services.size(), list.size());

        for (ServiceEntity entity : list) {
            boolean found = false;
            for (ServiceEntity storedEntity : services) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Finds a service by Id.
     *
     */
    @Test
    public void getServiceTest() {
        ServiceEntity entity = services.get(0);
        ServiceEntity resultEntity = serviceLogic.getService(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }

    /**
     * Finds a service by title.
     *
     */
    @Test
    public void getServiceByTitle() {
        ServiceEntity entity = services.get(0);
        ServiceEntity resultEntity = serviceLogic.getServiceByName(entity.getTitle());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }

    /**
     * Tests to update a service.
     */
    @Test
    public void updateServiceTest() throws BusinessLogicException {
        // get first service from generated data set
        ServiceEntity entity = services.get(0);

        // generate a random business
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);

        // set the id of the random service to the id of the first one from the data set
        newEntity.setId(entity.getId());

        // update the service with the new information
        serviceLogic.updateService(entity.getId(), newEntity);

        // find the business in the database
        ServiceEntity resp = em.find(ServiceEntity.class, entity.getId());

        // make sure they are equal
        Assert.assertEquals(newEntity.getId(), resp.getId());
    }

    /**
     * Tests the deletion of a service.
     *
     */
    public void deleteServiceTest() throws BusinessLogicException {
        // get first service from generated data set
        ServiceEntity entity = services.get(0);

        // delete the service
        serviceLogic.deleteService(entity.getId());

    }
}
