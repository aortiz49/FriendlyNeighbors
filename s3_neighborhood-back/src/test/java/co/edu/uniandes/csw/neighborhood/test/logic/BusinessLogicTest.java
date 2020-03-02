package co.edu.uniandes.csw.neighborhood.test.logic;


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
//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.ejb.BusinessLogic;
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.ArrayList;
import java.util.List;
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
 * Runs tests for NeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class BusinessLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Creates BusinessEntity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Injects BusinessLogic objects.
     */
    @Inject
    private BusinessLogic businessLogic;

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
    private List<BusinessEntity> data = new ArrayList<>();

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
                .addPackage(BusinessEntity.class.getPackage())
                .addPackage(BusinessLogic.class.getPackage())
                .addPackage(BusinessPersistence.class.getPackage())
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
        em.createQuery("delete from BusinessEntity").executeUpdate();
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {
        NeighborhoodEntity neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setName("Salitre");
        em.persist(neighborhood);

        // creates 3 businesses 
        for (int i = 0; i < 3; i++) {
            BusinessEntity entity = factory.manufacturePojo(BusinessEntity.class);
            em.persist(entity);
            entity.setNeighborhood(neighborhood);
            data.add(entity);
        }

        // select the first business
        BusinessEntity business0 = data.get(0);

        // set the name of the owner 
        ResidentProfileEntity owner = factory.manufacturePojo(ResidentProfileEntity.class);
        owner.setName("Andy");
        owner.setNeighborhood(neighborhood);
        em.persist(owner);

        business0.setOwner(owner);
        
        NeighborhoodEntity en = em.find(NeighborhoodEntity.class,business0.getNeighborhood().getId());
           
        System.out.println("co.edu.uniandes.csw.neighborhood.test.logic.BusinessLogicTest.insertData()");
    }

    /**
     * Tests the creation of a Business.
     */
    @Test
    public void createBusinessTest() throws BusinessLogicException {

        // creates a random business
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // sets the neighborhood
        newEntity.setNeighborhood(data.get(0).getNeighborhood());
        
        // sets the owner
        newEntity.setOwner(data.get(0).getOwner());

        // persist the created business, should not be null
        BusinessEntity result = businessLogic.createBusiness(newEntity);
        Assert.assertNotNull(result);

        // locate the persisted business
        BusinessEntity entity = em.find(BusinessEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

}
