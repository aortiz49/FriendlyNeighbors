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
    }



}
