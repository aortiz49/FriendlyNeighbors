
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
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
 * Persistence test for Neighborhood
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class NeighborhoodPersistenceTest {

    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<NeighborhoodEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara.
     * jar contains classes, DB descriptor and
     * beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NeighborhoodEntity.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
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
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            NeighborhoodEntity entity = factory.manufacturePojo(NeighborhoodEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Creating test for Comment.
     */
    @Test
    public void createNeighborhoodTest() {
        PodamFactory factory = new PodamFactoryImpl();
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);
        NeighborhoodEntity result = neighborhoodPersistence.create(newEntity);

        Assert.assertNotNull(result);

        NeighborhoodEntity entity = em.find(NeighborhoodEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
    }
    
     /**
     * Test for retrieving all neighborhoods from DB.
     */
        @Test
    public void findAllTest() {
        List<NeighborhoodEntity> list = neighborhoodPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (NeighborhoodEntity ent : list) {
            boolean found = false;
            for (NeighborhoodEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Test for a query about a Comment.
     */
    @Test
    public void getNeighborhoodTest() {
        NeighborhoodEntity entity = data.get(0);
        NeighborhoodEntity newEntity = neighborhoodPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getNumberOfResidents(), newEntity.getNumberOfResidents());
        Assert.assertEquals(entity.getMunicipality(), newEntity.getMunicipality());

    }

     /**
     * Test for updating a Comment.
     */
    @Test
    public void updateNeighborhoodTest() {
        NeighborhoodEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        newEntity.setId(entity.getId());

        neighborhoodPersistence.update(newEntity);

        NeighborhoodEntity resp = em.find(NeighborhoodEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());

    }
    
     /**
     * Test for deleting a Neighborhood.
     */
    @Test
    public void deleteNeighborhoodTest() {
        NeighborhoodEntity entity = data.get(0);
        neighborhoodPersistence.delete(entity.getId());
        NeighborhoodEntity deleted = em.find(NeighborhoodEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
    
    
    
}