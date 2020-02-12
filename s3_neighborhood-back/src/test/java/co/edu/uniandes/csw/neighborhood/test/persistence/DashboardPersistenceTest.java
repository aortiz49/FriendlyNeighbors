/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.vecindario.test.persistence;

import co.edu.uniandes.csw.vecindario.entities.DashboardEntity;
import co.edu.uniandes.csw.vecindario.persistence.DashboardPersistence;
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
 * Persistence test for DashboardPersistenceTest
 *
 * @author Carlos Figueredo
 */
@RunWith(Arquillian.class)
public class DashboardPersistenceTest {
    
    @Inject
    private DashboardPersistence dashboardPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;
    
    private List<DashboardEntity> data = new ArrayList<>();
    
    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(DashboardEntity.class.getPackage())
                .addPackage(DashboardPersistence.class.getPackage())
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
        em.createQuery("delete from DashboardEntity").executeUpdate();
    }
    
    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            DashboardEntity entity = factory.manufacturePojo(DashboardEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }
    
    /**
     * Test for creating a Dashboard.
     */
    @Test
    public void createLoginTest() {
        PodamFactory factory = new PodamFactoryImpl();
        DashboardEntity newEntity = factory.manufacturePojo(DashboardEntity.class);
        DashboardEntity result = dashboardPersistence.create(newEntity);

        Assert.assertNotNull(result);

        DashboardEntity entity = em.find(DashboardEntity.class, result.getId());

        Assert.assertEquals(newEntity.getId(), entity.getId());
    }
    
    /**
     * Test for retrieving all dashboards from DB.
     */
        @Test
    public void findAllTest() {
        List<DashboardEntity> list = dashboardPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (DashboardEntity ent : list) {
            boolean found = false;
            for (DashboardEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a dashboard.
     */
    @Test
    public void getLoginTest() {
        DashboardEntity entity = data.get(0);
        DashboardEntity newEntity = dashboardPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getId(), newEntity.getId());

    }
    
    /**
     * Test for updating a dashboard.
     */
    @Test
    public void updateResidentTest() {
        DashboardEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        DashboardEntity newEntity = factory.manufacturePojo(DashboardEntity.class);

        newEntity.setId(entity.getId());

        dashboardPersistence.update(newEntity);

        DashboardEntity resp = em.find(DashboardEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getId(), resp.getId());
    }
    
    /**
     * Test for deleting a offer.
     */
    @Test
    public void deleteAuthorTest() {
        DashboardEntity entity = data.get(0);
        dashboardPersistence.delete(entity.getId());
        DashboardEntity deleted = em.find(DashboardEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
