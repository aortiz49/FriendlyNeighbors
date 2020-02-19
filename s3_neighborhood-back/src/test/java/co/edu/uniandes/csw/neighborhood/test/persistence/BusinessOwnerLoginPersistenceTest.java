/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.BusinessOwnerLoginEntity;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessOwnerLoginPersistence;
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
 * Persistence test for BusinessOwnerLoginPersistenceTest
 *
 * @author Carlos Figueredo
 */
@RunWith(Arquillian.class)
public class BusinessOwnerLoginPersistenceTest {

    @Inject
    private BusinessOwnerLoginPersistence loginPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<BusinessOwnerLoginEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(BusinessOwnerLoginEntity.class.getPackage())
                .addPackage(BusinessOwnerLoginPersistence.class.getPackage())
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
        em.createQuery("delete from BusinessOwnerLoginEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            BusinessOwnerLoginEntity entity = factory.manufacturePojo(BusinessOwnerLoginEntity.class);

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
        BusinessOwnerLoginEntity newEntity = factory.manufacturePojo(BusinessOwnerLoginEntity.class);
        BusinessOwnerLoginEntity result = loginPersistence.create(newEntity);

        Assert.assertNotNull(result);

        BusinessOwnerLoginEntity entity = em.find(BusinessOwnerLoginEntity.class, result.getId());

        Assert.assertEquals(newEntity.getUserName(), entity.getUserName());
        Assert.assertEquals(newEntity.getGovernmentId(), entity.getGovernmentId());
        Assert.assertEquals(newEntity.getIsActive(), entity.getIsActive());
        Assert.assertEquals(newEntity.getPassword(), entity.getPassword());

    }

    /**
     * Test for retrieving all logins from DB.
     */
    @Test
    public void findAllTest() {
        List<BusinessOwnerLoginEntity> list = loginPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (BusinessOwnerLoginEntity ent : list) {
            boolean found = false;
            for (BusinessOwnerLoginEntity entity : data) {
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
        BusinessOwnerLoginEntity entity = data.get(0);
        BusinessOwnerLoginEntity newEntity = loginPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getUserName(), newEntity.getUserName());
        Assert.assertEquals(entity.getPassword(), newEntity.getPassword());


    }

    /**
     * Test for updating a login.
     */
    @Test
    public void updateBusinessOwnerTest() {
        BusinessOwnerLoginEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        BusinessOwnerLoginEntity newEntity = factory.manufacturePojo(BusinessOwnerLoginEntity.class);

        newEntity.setId(entity.getId());

        loginPersistence.update(newEntity);

        BusinessOwnerLoginEntity resp = em.find(BusinessOwnerLoginEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getUserName(), resp.getUserName());
    }

    /**
     * Test for deleting a login.
     */
    @Test
    public void deleteAuthorTest() {
        BusinessOwnerLoginEntity entity = data.get(0);
        loginPersistence.delete(entity.getId());
        BusinessOwnerLoginEntity deleted = em.find(BusinessOwnerLoginEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
