/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentLoginPersistence;
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
 * Persistence test for ResidentLoginPersistenceTest
 *
 * @author Carlos Figueredo
 */
@RunWith(Arquillian.class)
public class ResidentLoginPersistenceTest {

    @Inject
    private ResidentLoginPersistence loginPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<ResidentLoginEntity> data = new ArrayList<>();

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentLoginEntity.class.getPackage())
                .addPackage(ResidentLoginPersistence.class.getPackage())
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
        em.createQuery("delete from ResidentLoginEntity").executeUpdate();
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
            ResidentLoginEntity entity = factory.manufacturePojo(ResidentLoginEntity.class);
            entity.setResident(resident);
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
        ResidentLoginEntity newEntity = factory.manufacturePojo(ResidentLoginEntity.class);
        ResidentLoginEntity result = loginPersistence.create(newEntity);

        Assert.assertNotNull(result);

        ResidentLoginEntity entity = em.find(ResidentLoginEntity.class, result.getId());

        Assert.assertEquals(newEntity.getUserName(), entity.getUserName());
    }

    /**
     * Test for retrieving all logins from DB.
     */
    @Test
    public void findAllTest() {
        List<ResidentLoginEntity> list = loginPersistence.findAll(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (ResidentLoginEntity ent : list) {
            boolean found = false;
            for (ResidentLoginEntity entity : data) {
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
        ResidentLoginEntity entity = data.get(0);
        ResidentLoginEntity newEntity = loginPersistence.find(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getUserName(), newEntity.getUserName());
        Assert.assertEquals(entity.getPassword(), newEntity.getPassword());

    }

    /**
     * Test for updating a login.
     */
    @Test
    public void updateResidentTest() {
        ResidentLoginEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        ResidentLoginEntity newEntity = factory.manufacturePojo(ResidentLoginEntity.class);

        newEntity.setId(entity.getId());

        loginPersistence.update(newEntity,neighborhood.getId());

        ResidentLoginEntity resp = em.find(ResidentLoginEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getUserName(), resp.getUserName());
    }

    /**
     * Test for deleting a login.
     */
    @Test
    public void deleteAuthorTest() {
        ResidentLoginEntity entity = data.get(0);
        loginPersistence.delete(entity.getId(),neighborhood.getId());
        ResidentLoginEntity deleted = em.find(ResidentLoginEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
