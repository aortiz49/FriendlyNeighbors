/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.persistence;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
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
 * Persistence test for Location
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class LocationPersistenceTest {

    @Inject
    private LocationPersistence locationPersistence;
    

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<LocationEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(LocationEntity.class.getPackage())
                .addPackage(LocationPersistence.class.getPackage())
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
        em.createQuery("delete from LocationEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            LocationEntity entity = factory.manufacturePojo(LocationEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Location.
     */
    @Test
    public void createLocationTest() {
        PodamFactory factory = new PodamFactoryImpl();
        LocationEntity newEntity = factory.manufacturePojo(LocationEntity.class);
        LocationEntity result = locationPersistence.create(newEntity);

        Assert.assertNotNull(result);

        LocationEntity entity = em.find(LocationEntity.class, result.getId());

        Assert.assertEquals(newEntity.getAddress(), entity.getAddress());
    }
    
    /**
     * Test for retrieving all Locations from DB.
     */
        @Test
    public void findAllTest() {
        List<LocationEntity> list = locationPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (LocationEntity ent : list) {
            boolean found = false;
            for (LocationEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Location.
     */
    @Test
    public void getLocationTest() {
        LocationEntity entity = data.get(0);
        LocationEntity newEntity = locationPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getAddress(), newEntity.getAddress());
    }

     /**
     * Test for updating a Location.
     */
    @Test
    public void updateLocationTest() {
        LocationEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        LocationEntity newEntity = factory.manufacturePojo(LocationEntity.class);

        newEntity.setId(entity.getId());

        locationPersistence.update(newEntity);

        LocationEntity resp = em.find(LocationEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }
    
     /**
     * Test for deleting a Location.
     */
    @Test
    public void deleteLocationTest() {
        LocationEntity entity = data.get(0);
        locationPersistence.delete(entity.getId());
        LocationEntity deleted = em.find(LocationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
     
    
}