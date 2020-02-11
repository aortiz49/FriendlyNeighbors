/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.friendlyNeighbors.test.persistence;

import co.edu.uniandes.csw.friendlyNeighbors.entities.LoginEntity;
import co.edu.uniandes.csw.friendlyNeighbors.persistence.LoginPersistence;
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
 * Persistence test for LoginPersistenceTest
 *
 * @author Carlos Figueredo
 */
@RunWith(Arquillian.class)
public class LoginPersistenceTest {
    @Inject
    private LoginPersistence loginPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<LoginEntity> data = new ArrayList<>();
    
    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(LoginEntity.class.getPackage())
                .addPackage(LoginPersistence.class.getPackage())
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
        em.createQuery("delete from LoginEntity").executeUpdate();
    }
    
    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            LoginEntity entity = factory.manufacturePojo(LoginEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }
    
    /**
     * Test for creating a Login.
     */
    @Test
    public void createLoginTest() {
        PodamFactory factory = new PodamFactoryImpl();
        LoginEntity newEntity = factory.manufacturePojo(LoginEntity.class);
        LoginEntity result = loginPersistence.create(newEntity);

        Assert.assertNotNull(result);

        LoginEntity entity = em.find(LoginEntity.class, result.getId());

        Assert.assertEquals(newEntity.getUserName(), entity.getUserName());
    }
    
    /**
     * Test for retrieving all logins from DB.
     */
        @Test
    public void findAllTest() {
        List<LoginEntity> list = loginPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (LoginEntity ent : list) {
            boolean found = false;
            for (LoginEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a login.
     */
    @Test
    public void getLoginTest() {
        LoginEntity entity = data.get(0);
        LoginEntity newEntity = loginPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getUserName(), newEntity.getUserName());
        Assert.assertEquals(entity.getPassword(), newEntity.getPassword());
        Assert.assertEquals(entity.getRol(), newEntity.getRol());

    }
    
    /**
     * Test for updating a login.
     */
    @Test
    public void updateResidentTest() {
        LoginEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        LoginEntity newEntity = factory.manufacturePojo(LoginEntity.class);

        newEntity.setId(entity.getId());

        loginPersistence.update(newEntity);

        LoginEntity resp = em.find(LoginEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getUserName(), resp.getUserName());
    }
    
    /**
     * Test for deleting a login.
     */
    @Test
    public void deleteAuthorTest() {
        LoginEntity entity = data.get(0);
        loginPersistence.delete(entity.getId());
        LoginEntity deleted = em.find(LoginEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
