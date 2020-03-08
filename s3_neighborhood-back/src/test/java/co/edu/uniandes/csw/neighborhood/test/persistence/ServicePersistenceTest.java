package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
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
public class ServicePersistenceTest {

    @Inject
    private ServicePersistence servicePersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<ServiceEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ServiceEntity.class.getPackage())
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
            em.joinTransaction();
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
        em.createQuery("delete from ServiceEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            ServiceEntity entity = factory.manufacturePojo(ServiceEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Comment.
     */
    @Test
    public void createServiceTest() {
        PodamFactory factory = new PodamFactoryImpl();
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);
        ServiceEntity result = servicePersistence.create(newEntity);

        Assert.assertNotNull(result);

        ServiceEntity entity = em.find(ServiceEntity.class, result.getId());

        Assert.assertEquals(newEntity.getAuthor(), entity.getAuthor());
    }

    /**
     * Test for retrieving all neighborhoods from DB.
     */
    @Test
    public void findAllTest() {
        List<ServiceEntity> list = servicePersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (ServiceEntity ent : list) {
            boolean found = false;
            for (ServiceEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test to consult a Service by Title.
     */
    @Test
    public void findServiceByTitleTest() {
        ServiceEntity entity = data.get(0);
        ServiceEntity newEntity = servicePersistence.findByTitle(entity.getTitle());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

        newEntity = servicePersistence.findByTitle(null);
        Assert.assertNull(newEntity);
    }

    /**
     * Test for a query about a Service.
     */
    @Test
    public void getServiceTest() {
        // get the first bsuiness entity from the table 
        ServiceEntity entity = data.get(0);

        // using the find method from the Service persistence, returns the 
        // Service entity matching the id
        ServiceEntity newEntity = servicePersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getAvailability(), newEntity.getAvailability());
        Assert.assertEquals(entity.getDescription(), newEntity.getDescription());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

    }

    /**
     * Test for updating a Service.
     */
    @Test
    public void updateServiceTest() {
        ServiceEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);

        newEntity.setId(entity.getId());

        servicePersistence.update(newEntity);

        ServiceEntity resp = em.find(ServiceEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getAuthor(), resp.getAuthor());

    }

    /**
     * Test for deleting a Service.
     */
    @Test
    public void deleteServiceTest() {
        ServiceEntity entity = data.get(0);
        servicePersistence.delete(entity.getId());
        ServiceEntity deleted = em.find(ServiceEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
