
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
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
 * Persistence test for Event
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class EventPersistenceTest {

    @Inject
    private EventPersistence eventPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<EventEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(EventPersistence.class.getPackage())
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
        em.createQuery("delete from EventEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Event.
     */
    @Test
    public void createEventTest() {
        PodamFactory factory = new PodamFactoryImpl();
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);
        EventEntity result = eventPersistence.create(newEntity);

        Assert.assertNotNull(result);

        EventEntity entity = em.find(EventEntity.class, result.getId());

        Assert.assertEquals(newEntity.getId(), entity.getId());
    }
    
     /**
     * Test for retrieving all events from DB.
     */
        @Test
    public void findAllTest() {
        List<EventEntity> list = eventPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (EventEntity ent : list) {
            boolean found = false;
            for (EventEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Event.
     */
    @Test
    public void getEventTest() {
        EventEntity entity = data.get(0);
        EventEntity newEntity = eventPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getId(), newEntity.getId());
    }

     /**
     * Test for updating a Event.
     */
    @Test
    public void updateEventTest() {
        EventEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        newEntity.setId(entity.getId());

        eventPersistence.update(newEntity);

        EventEntity resp = em.find(EventEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getId(), resp.getId());
    }
    
     /**
     * Test for deleting a Event.
     */
    @Test
    public void deleteEventTest() {
        EventEntity entity = data.get(0);
        eventPersistence.delete(entity.getId());
        EventEntity deleted = em.find(EventEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
    
    
    
}