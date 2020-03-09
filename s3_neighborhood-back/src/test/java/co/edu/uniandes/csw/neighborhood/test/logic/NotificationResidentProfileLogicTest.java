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

import co.edu.uniandes.csw.neighborhood.ejb.NotificationResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
 * Tests the NotificationResidentProfileLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class NotificationResidentProfileLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for notification/residentProfile logic.
     */
    @Inject
    private NotificationResidentProfileLogic notificationResidentProfileLogic;

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
     * List of residentProfiles to be used in the tests.
     */
    private List<ResidentProfileEntity> testPeeps = new ArrayList<>();

    /**
     * List of notifications to be used in the tests.
     */
    private List<NotificationEntity> testJoints = new ArrayList<>();
//===================================================
// Test Setup
//===================================================

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(ResidentProfileLogic.class.getPackage())
                .addPackage(ResidentProfilePersistence.class.getPackage())
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
        em.createQuery("delete from NotificationEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random residentProfiles
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity neigh = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(neigh);
            testPeeps.add(neigh);
        }

        // creates 3 random notifications
        for (int i = 0; i < 3; i++) {
            NotificationEntity buss = factory.manufacturePojo(NotificationEntity.class);
            em.persist(buss);
            testJoints.add(buss);
        }

        // associates notifications to a residentProfile
        testJoints.get(0).setAuthor(testPeeps.get(0));
        testJoints.get(2).setAuthor(testPeeps.get(0));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a notification with a residentProfile.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addNotificationToResidentProfileTest() throws BusinessLogicException {
        // gets the second random residentProfile from the list
        ResidentProfileEntity residentProfile = testPeeps.get(0);

        // gets the second random notification from the list, since the first has an associated 
        // notification already
        NotificationEntity notification = testJoints.get(1);

        // add the notification to the residentProfile
        NotificationEntity response = notificationResidentProfileLogic.addNotificationToResidentProfile(
                notification.getId(), residentProfile.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(notification.getId(), response.getId());
    }

    /**
     * Tests the consultation of all notification entities associated with a residentProfile.
     */
    @Test
    public void getNotificationsTest() {
        List<NotificationEntity> list = notificationResidentProfileLogic.getNotifications(testPeeps.get(0).getId());

        // checks that there are two notifications associated to the residentProfile
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated residentProfile matches
        Assert.assertEquals(list.get(0).getAuthor().getName(), testPeeps.get(0).getName());
    }

    /**
     * Tests the consultation of a notification entity associated with a residentProfile.
     *
     * @throws BusinessLogicException if the notification is not found
     */
    @Test
    public void getNotificationTest() throws BusinessLogicException {

        // gets the first notification from the list
        NotificationEntity notification = testJoints.get(0);

        // gets the first residentProfile from the list
        ResidentProfileEntity residentProfile = testPeeps.get(0);

        // get the notification from the residentProfile
        NotificationEntity response = notificationResidentProfileLogic.getNotification(residentProfile.getId(), notification.getId());

        Assert.assertEquals(notification.getId(), response.getId());

    }

    /**
     * Tests the removal of a notification from the residentProfile. 
     */
    @Test
    public void removeNotificationTest() {
        // gets the first residentProfile from the list. 
        // (Uses em.find because the persisted residentProfile contains the added notifications)
        ResidentProfileEntity residentProfile = em.find(ResidentProfileEntity.class,testPeeps.get(0).getId());
        
        // get the first associated notification
        NotificationEntity notification = testJoints.get(0);
        
        // gets the list of notifications in the residentProfile
        List<NotificationEntity> list = em.find(ResidentProfileEntity.class, residentProfile.getId()).getNotifications();
        
        notificationResidentProfileLogic.removeNotification(residentProfile.getId(), notification.getId());
        Assert.assertEquals(1, list.size());

    }

}
