/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.persistence;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.persistence.NotificationPersistence;
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
 * Persistence test for Notification
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class NotificationPersistenceTest {

    @Inject
    private NotificationPersistence notificationPersistence;
    

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<NotificationEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NotificationEntity.class.getPackage())
                .addPackage(NotificationPersistence.class.getPackage())
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
        em.createQuery("delete from NotificationEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            NotificationEntity entity = factory.manufacturePojo(NotificationEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Notification.
     */
    @Test
    public void createNotificationTest() {
        PodamFactory factory = new PodamFactoryImpl();
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        NotificationEntity result = notificationPersistence.create(newEntity);

        Assert.assertNotNull(result);

        NotificationEntity entity = em.find(NotificationEntity.class, result.getId());

        Assert.assertEquals(newEntity.getTitle(), entity.getTitle());
    }
    
    /**
     * Test for retrieving all Notifications from DB.
     */
        @Test
    public void findAllTest() {
        List<NotificationEntity> list = notificationPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (NotificationEntity ent : list) {
            boolean found = false;
            for (NotificationEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Notification.
     */
    @Test
    public void getNotificationTest() {
        NotificationEntity entity = data.get(0);
        NotificationEntity newEntity = notificationPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());
    }

     /**
     * Test for updating a Notification.
     */
    @Test
    public void updateNotificationTest() {
        NotificationEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);

        newEntity.setId(entity.getId());

        notificationPersistence.update(newEntity);

        NotificationEntity resp = em.find(NotificationEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getDatePosted(), resp.getDatePosted());
    }
    
     /**
     * Test for deleting a Notification.
     */
    @Test
    public void deleteNotificationTest() {
        NotificationEntity entity = data.get(0);
        notificationPersistence.delete(entity.getId());
        NotificationEntity deleted = em.find(NotificationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
     
    
}