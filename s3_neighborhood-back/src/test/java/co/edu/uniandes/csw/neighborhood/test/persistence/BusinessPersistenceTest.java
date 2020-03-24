package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
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
 * Persistence test for Neighborhood
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class BusinessPersistenceTest {

    /**
     * Creates BusinessEntity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * The business persistence object to test. The container will inject an instance of this class.
     */
    @Inject
    private BusinessPersistence businessPersistence;

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
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(BusinessEntity.class.getPackage())
                .addPackage(BusinessPersistence.class.getPackage())
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

            // create sthe new data
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
        em.createQuery("delete from BusinessEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
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
     * Creating test for Comment.
     */
    @Test
    public void createBusinessTest() {

        // uses the factory to create a ranbdom BusinessEntity object
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        BusinessEntity result = businessPersistence.create(newEntity);

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        BusinessEntity entity = em.find(BusinessEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Test for retrieving all businesses from DB.
     */
    @Test
    public void findAllTest() {
        // testing the findAll method of the persistence class
        List<BusinessEntity> list = businessPersistence.findAll(neighborhood.getId());

        // verifies that the number of objects from findAll is the same as the
        // number of objects in the data list
        Assert.assertEquals(data.size(), list.size());

        // for every element in the list, compare its id with the corresponding
        // id from the data set
        for (BusinessEntity ent : list) {
            boolean found = false;
            for (BusinessEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test to consult a Business by name.
     */
    @Test
    public void findBusinessByNameTest() {
        BusinessEntity entity = data.get(0);
        BusinessEntity newEntity = businessPersistence.findByName(entity.getName());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());

        newEntity = businessPersistence.findByName(null);
        Assert.assertNull(newEntity);
    }

    /**
     * Test for a query about a Business.
     */
    @Test
    public void getBusinessTest() {
        // get the first bsuiness entity from the table 
        BusinessEntity entity = data.get(0);

        // using the find method from the business persistence, returns the 
        // business entity matching the id
        BusinessEntity newEntity = businessPersistence.find(entity.getId(),neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getAddress(), newEntity.getAddress());
        Assert.assertEquals(entity.getOpeningTime(), newEntity.getOpeningTime());
        Assert.assertEquals(entity.getRating(), newEntity.getRating(), .0001);
    }

    /**
     * Test for updating a Business.
     */
    @Test
    public void updateBusinessTest() {
        // gets the first business entity from the table
        BusinessEntity entity = data.get(0);

        // creates a factory to construct random objects
        PodamFactory factory = new PodamFactoryImpl();

        // uses the factory to create a random BusinessEntity object named 
        // newEntity
        BusinessEntity newEntity = factory.manufacturePojo(BusinessEntity.class);

        // sets the id of the newEntity object to the id of the first entity in
        // the database table
        newEntity.setId(entity.getId());

        // invokes the method being tested to see if the entity updated with 
        // the values from new entity
        businessPersistence.update(newEntity,neighborhood.getId());

        // resp is the updated entity from the table
        BusinessEntity resp = em.find(BusinessEntity.class, entity.getId());

        // verifies that the new entity matches the values from the entity 
        // in the table that was modified
        Assert.assertEquals(newEntity.getAddress(), resp.getAddress());
        Assert.assertEquals(newEntity.getOpeningTime(), resp.getOpeningTime());
        Assert.assertEquals(newEntity.getClosingTime(), resp.getClosingTime());
        Assert.assertEquals(newEntity.getRating(), resp.getRating(), .0001);
    }

    /**
     * Test for deleting a Business.
     */
    @Test
    public void deleteBusinessTest() {
        // gets the first business entity from the table
        BusinessEntity entity = data.get(0);

        // invokes the method to be tested from the persistence class
        businessPersistence.delete(entity.getId(),neighborhood.getId());

        // tries to obtain the deleted entry
        BusinessEntity deleted = em.find(BusinessEntity.class,
                entity.getId());

        // verifies that the result is null, since it should have been deleted
        Assert.assertNull(deleted);

    }
}
