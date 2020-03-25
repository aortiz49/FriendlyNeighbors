/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.NotificationLogic;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;

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
 * @author albayona
 */
@RunWith(Arquillian.class)
public class NotificationLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private NotificationLogic notificationLogic;

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
    private List<NotificationEntity> data = new ArrayList<>();

    ///
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NotificationEntity.class.getPackage())
                .addPackage(NotificationLogic.class.getPackage())
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
        em.createQuery("delete from NotificationEntity").executeUpdate();
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

            NotificationEntity entity = factory.manufacturePojo(NotificationEntity.class);
            
            entity.setAuthor(resident);

            // add the data to the table
            em.persist(entity);

            // add the data to the list of test objects
            data.add(entity);
        }
    }

    /**
     * Test for creating a notification
     */
    @Test
    public void createNotificationTest() {

        NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neigh);

        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neigh);

        residentPersistence.create(resident);

        // uses the factory to create a ranbdom NeighborhoodEntity object
        NotificationEntity newNotification = factory.manufacturePojo(NotificationEntity.class);
        newNotification.setAuthor(resident);

        // invokes the method to be tested (create): it creates a table in the 
        // database. The parameter of this method is the newly created object from 
        // the podam factory which has an id associated to it. 
        NotificationEntity result = null;
        try {
            result = notificationLogic.createNotification(newNotification, resident.getId(), neigh.getId());
        } catch (BusinessLogicException ex) {
            Logger.getLogger(NotificationLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // verify that the created object is not null
        Assert.assertNotNull(result);

        // using the entity manager, it searches the database for the object 
        // matching the id of the newly created factory object
        NotificationEntity entity
                = em.find(NotificationEntity.class, result.getId());

        // verifies that the object exists in the database
        Assert.assertNotNull(entity);

        // compares if the name of the new object generated by the factory matched
        // the name of the object in the database
        Assert.assertEquals(newNotification.getDescription(), entity.getDescription());

    }

    /**
     * Test for getting ll notifications
     */
    @Test
    public void getNotificationsTest() {
        List<NotificationEntity> list = notificationLogic.getNotifications(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (NotificationEntity entity : list) {
            boolean found = false;
            for (NotificationEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for getting  notification
     */
    @Test
    public void getNotificationTest() {
        NotificationEntity entity = data.get(0);
        NotificationEntity resultEntity = notificationLogic.getNotification(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
    }

    /**
     * Test for updating a notification
     *
     * @throws BusinessLogicException
     */
    @Test
    public void updateNotificationTest() throws BusinessLogicException {

        NotificationEntity entity = data.get(0);
        NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
        pojoEntity.setId(entity.getId());
        notificationLogic.updateNotification(pojoEntity, neighborhood.getId());
        NotificationEntity resp = em.find(NotificationEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getDescription(), resp.getDescription());

    }

    /**
     * Test for deleting a notification
     *
     * @throws BusinessLogicException
     */
    @Test
    public void deleteNotificationTest() throws BusinessLogicException {
        NotificationEntity entity = data.get(1);
        notificationLogic.deleteNotification(entity.getId(), neighborhood.getId());
        NotificationEntity deleted = em.find(NotificationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Test for creating a notification with no title
     */
    @Test(expected = BusinessLogicException.class)
    public void createNotificationWithNoTitle() throws BusinessLogicException {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setHeader(null);
        notificationLogic.createNotification(newEntity, resident.getId(), neighborhood.getId());
    }

    /**
     * Test for creating a notification with no description
     */
    @Test(expected = BusinessLogicException.class)
    public void createNotificationWithNoDescription() throws BusinessLogicException {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setDescription(null);
        notificationLogic.createNotification(newEntity, resident.getId(), neighborhood.getId());
    }

    /**
     * Test for creating a notification with no date
     */
    @Test(expected = BusinessLogicException.class)
    public void createNotificationWithNoDate() throws BusinessLogicException {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setPublishDate(null);
        notificationLogic.createNotification(newEntity, resident.getId(), neighborhood.getId());
    }
}
