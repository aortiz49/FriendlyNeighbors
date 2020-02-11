/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.friendlyNeighbors.test.persistence;

import co.edu.uniandes.csw.friendlyNeighbors.entities.OfferEntity;
import co.edu.uniandes.csw.friendlyNeighbors.persistence.OfferPersistence;
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
 * Persistence test for offerPersistenceTest
 *
 * @author Carlos Figueredo
 */
@RunWith(Arquillian.class)
public class OfferPersistenceTest {
    @Inject
    private OfferPersistence offerPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;
    
    private List<OfferEntity> data = new ArrayList<>();
    
    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(OfferEntity.class.getPackage())
                .addPackage(OfferPersistence.class.getPackage())
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
        em.createQuery("delete from OfferEntity").executeUpdate();
    }
    
    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            OfferEntity entity = factory.manufacturePojo(OfferEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }
    
    /**
     * Test for creating a offer.
     */
    @Test
    public void createLoginTest() {
        PodamFactory factory = new PodamFactoryImpl();
        OfferEntity newEntity = factory.manufacturePojo(OfferEntity.class);
        OfferEntity result = offerPersistence.create(newEntity);

        Assert.assertNotNull(result);

        OfferEntity entity = em.find(OfferEntity.class, result.getId());

        Assert.assertEquals(newEntity.getId(), entity.getId());
    }
    
    /**
     * Test for retrieving all offers from DB.
     */
        @Test
    public void findAllTest() {
        List<OfferEntity> list = offerPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (OfferEntity ent : list) {
            boolean found = false;
            for (OfferEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a offer.
     */
    @Test
    public void getLoginTest() {
        OfferEntity entity = data.get(0);
        OfferEntity newEntity = offerPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getId(), newEntity.getId());

    }
    
    /**
     * Test for updating a offer.
     */
    @Test
    public void updateResidentTest() {
        OfferEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        OfferEntity newEntity = factory.manufacturePojo(OfferEntity.class);

        newEntity.setId(entity.getId());

        offerPersistence.update(newEntity);

        OfferEntity resp = em.find(OfferEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getId(), resp.getId());
    }
    
    /**
     * Test for deleting a offer.
     */
    @Test
    public void deleteAuthorTest() {
        OfferEntity entity = data.get(0);
        offerPersistence.delete(entity.getId());
        OfferEntity deleted = em.find(OfferEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
}
