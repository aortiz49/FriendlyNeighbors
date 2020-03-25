/*
MIT License

Copyright (c) 2019 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 * @author albayona
 */
@RunWith(Arquillian.class)
public class NotificationPersistenceTest {

    @Inject
    private NotificationPersistence notificationPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    NeighborhoodEntity neighborhood;

    ResidentProfileEntity resident;

    private List<NotificationEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
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

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        
        resident.setNeighborhood(neighborhood);
        em.persist(resident);
        

        for (int i = 0; i < 3; i++) {
            NotificationEntity entity = factory.manufacturePojo(NotificationEntity.class);
            entity.setAuthor(resident);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating a Notification.
     */
    @Test
    public void createNotificationTest() {
        PodamFactory factory = new PodamFactoryImpl();
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        NotificationEntity result = notificationPersistence.create(newEntity);

        Assert.assertNotNull(result);

        NotificationEntity entity = em.find(NotificationEntity.class, result.getId());

        Assert.assertNotNull(newEntity);
        Assert.assertEquals(newEntity.getHeader(), entity.getHeader());
        Assert.assertEquals(newEntity.getAuthor(), entity.getAuthor());
        Assert.assertEquals(newEntity.getBusiness(), entity.getBusiness());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());
        Assert.assertEquals(newEntity.getId(), entity.getId());

    }

    /**
     * Test for retrieving all residents from DB.
     */
    @Test
    public void findAllTest() {

        List<NotificationEntity> list = notificationPersistence.findAll(neighborhood.getId());
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
     * Test for a query about a notification.
     */
    @Test
    public void getResidentTest() {
        NotificationEntity entity = data.get(0);
        NotificationEntity newEntity = notificationPersistence.find(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(newEntity.getHeader(), entity.getHeader());
        Assert.assertEquals(newEntity.getAuthor(), entity.getAuthor());
        Assert.assertEquals(newEntity.getBusiness(), entity.getBusiness());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());
        Assert.assertEquals(newEntity.getId(), entity.getId());
    }

    /**
     * Test for a query about a notification not belonging to a neighborhood.
     */
    @Test(expected = RuntimeException.class)
    public void getResidentTestNotBelonging() {
        NotificationEntity entity = data.get(0);
        notificationPersistence.find(entity.getId(), new Long(10000));
    }

    /**
     * Test for updating a notification.
     */
    @Test
    public void updateResidentTest() {
        NotificationEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);

        newEntity.setId(entity.getId());
        newEntity.setAuthor(resident);

        notificationPersistence.update(newEntity, neighborhood.getId());

        NotificationEntity resp = notificationPersistence.find(entity.getId(), neighborhood.getId());

        Assert.assertEquals(newEntity.getHeader(), resp.getHeader());
    }

    /**
     * Test for deleting a notification.
     */
    @Test
    public void deleteResidentTest() {
        NotificationEntity entity = data.get(0);
        notificationPersistence.delete(entity.getId(), neighborhood.getId());
        NotificationEntity deleted = em.find(NotificationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }


}
