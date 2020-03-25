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
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
     * The neighborhood used for the tests.
     */
    private NeighborhoodEntity neighborhood;

    /**
     * The owner used for the tests.
     */
    private ResidentProfileEntity owner;

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
        em.createQuery("delete from BusinessEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {

        // creates the neighborhood where the business is located
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setName("Salitre");
        em.persist(neighborhood);

        // creates the owner of the businesses
        owner = factory.manufacturePojo(ResidentProfileEntity.class);
        owner.setNeighborhood(neighborhood);
        em.persist(owner);

        // creates 3 businesses 
        for (int i = 0; i < 3; i++) {
            BusinessEntity entity = factory.manufacturePojo(BusinessEntity.class);

            entity.setNeighborhood(neighborhood);
            entity.setOwner(owner);
            em.persist(entity);
            data.add(entity);

            // add the business to the neighborhood
            neighborhood.getBusinesses().add(data.get(i));

        }

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
        Assert.assertEquals(newEntity.getAddress(), entity.getAddress());
        Assert.assertEquals(newEntity.getLatitude(), entity.getLatitude(), 0.0001);
        Assert.assertEquals(newEntity.getLongitude(), entity.getLongitude(), 0.0001);
        Assert.assertEquals(newEntity.getRating(), entity.getRating(), .0001);

    }

    /**
     * Tests the creation of a Business with no neighborhood.
     */
    @Test(expected = BusinessLogicException.class)
    public void createBusinessNoNeighborhoodTest() throws BusinessLogicException {

        // creates a random business
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // persist the created business, should not be null
        BusinessEntity result = businessLogic.createBusiness(newEntity);
    }

    /**
     * Tests the creation of a Business with non-existent neighborhood.
     */
    @Test(expected = BusinessLogicException.class)
    public void createBusinessNonexistentNeighborhoodTest() throws BusinessLogicException {

        // creates a random business
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // change neighborhood id to one who doesn't exist in the system
        NeighborhoodEntity neigh = data.get(0).getNeighborhood();
        neigh.setId(11123L);
        newEntity.setNeighborhood(neigh);

        // persist the created business, should not finish
        BusinessEntity result = businessLogic.createBusiness(newEntity);
    }

    /**
     * Tests the creation of a Business with the same name.
     */
    @Test(expected = BusinessLogicException.class)
    public void createBusinessSameNameLogicException() throws BusinessLogicException {

        // creates a random business
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // change business name to existingbusiness
        newEntity.setName(data.get(0).getName());

        // persist the created business, should not finish
        BusinessEntity result = businessLogic.createBusiness(newEntity);
    }

    /**
     * Returns all the businesses in the database.
     *
     * @return list of businesses
     */
    @Test
    public void getBusinessesTest() {
        List<BusinessEntity> list = businessLogic.getBusinesses(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());

        for (BusinessEntity entity : list) {
            boolean found = false;
            for (BusinessEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Finds a business by Id.
     *
     * @return the found business, null if not found
     */
    @Test
    public void getBusinessTest() {
        BusinessEntity entity = data.get(0);
        BusinessEntity resultEntity = businessLogic.getBusiness(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Finds a business by name.
     *
     * @return the found business, null if not found
     */
    @Test
    public void getNeighborhoodByNameTest() {
        BusinessEntity entity = data.get(0);
        BusinessEntity resultEntity = businessLogic.getBusinessByName(entity.getName());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Tests to update a business.
     */
    @Test
    public void updateBusinessTest() throws BusinessLogicException {
        // get first business from generated data set
        BusinessEntity entity = data.get(0);

        // generate a random business
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // set the id of the random business to the id of the first one from the data set
        newEntity.setId(entity.getId());

        // set neighborhood
        newEntity.setNeighborhood(entity.getNeighborhood());

        // set owner
        newEntity.setOwner(entity.getOwner());

        // update the business with the new information
        businessLogic.updateBusiness(newEntity, neighborhood.getId());

        // find the business in the database
        BusinessEntity resp = em.find(BusinessEntity.class, entity.getId());

        // make sure they are equal
        Assert.assertEquals(newEntity.getId(), resp.getId());
        Assert.assertEquals(newEntity.getName(), resp.getName());
    }

    /**
     * Tests the deletion of a business.
     *
     */
    public void deleteBusinessTest() throws BusinessLogicException {
        // get first business from generated data set
        BusinessEntity entity = data.get(0);

        // delete the business
        businessLogic.deleteBusiness(entity.getId(), neighborhood.getId());

    }

}
