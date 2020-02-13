/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.persistence;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence;
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
 * Persistence test for Favor
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class FavorPersistenceTest {

    @Inject
    private FavorPersistence favorPersistence;
    

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<FavorEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(FavorEntity.class.getPackage())
                .addPackage(FavorPersistence.class.getPackage())
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
        em.createQuery("delete from FavorEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Favor.
     */
    @Test
    public void createFavorTest() {
        PodamFactory factory = new PodamFactoryImpl();
        FavorEntity newEntity = factory.manufacturePojo(FavorEntity.class);
        FavorEntity result = favorPersistence.create(newEntity);

        Assert.assertNotNull(result);

        FavorEntity entity = em.find(FavorEntity.class, result.getId());

        Assert.assertEquals(newEntity.getTitle(), entity.getTitle());
    }
    
    /**
     * Test for retrieving all Favors from DB.
     */
        @Test
    public void findAllTest() {
        List<FavorEntity> list = favorPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (FavorEntity ent : list) {
            boolean found = false;
            for (FavorEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Favor.
     */
    @Test
    public void getFavorTest() {
        FavorEntity entity = data.get(0);
        FavorEntity newEntity = favorPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());
    }

     /**
     * Test for updating a Favor.
     */
    @Test
    public void updateFavorTest() {
        FavorEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        FavorEntity newEntity = factory.manufacturePojo(FavorEntity.class);

        newEntity.setId(entity.getId());

        favorPersistence.update(newEntity);

        FavorEntity resp = em.find(FavorEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getDatePosted(), resp.getDatePosted());
    }
    
     /**
     * Test for deleting a Favor.
     */
    @Test
    public void deleteFavorTest() {
        FavorEntity entity = data.get(0);
        favorPersistence.delete(entity.getId());
        FavorEntity deleted = em.find(FavorEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
     
    
}