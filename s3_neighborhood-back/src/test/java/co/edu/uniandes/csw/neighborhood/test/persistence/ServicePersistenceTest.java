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
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
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
 * Persistence test for Service
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class ServicePersistenceTest {

    @Inject
    private ServicePersistence servicePersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    NeighborhoodEntity neighborhood;

    ResidentProfileEntity resident;

    private List<ServiceEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ServiceEntity.class.getPackage())
                .addPackage(ServicePersistence.class.getPackage())
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
        em.createQuery("delete from ServiceEntity").executeUpdate();
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
            ServiceEntity entity = factory.manufacturePojo(ServiceEntity.class);
            entity.setAuthor(resident);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating a Service.
     */
    @Test
    public void createServiceTest() {
        PodamFactory factory = new PodamFactoryImpl();
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);
        ServiceEntity result = servicePersistence.create(newEntity);

        Assert.assertNotNull(result);

        ServiceEntity entity = em.find(ServiceEntity.class, result.getId());

        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getAvailability(), newEntity.getAvailability());
        Assert.assertEquals(entity.getDescription(), newEntity.getDescription());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

    }

    /**
     * Test for retrieving all residents from DB.
     */
    @Test
    public void findAllTest() {

        List<ServiceEntity> list = servicePersistence.findAll(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (ServiceEntity ent : list) {
            boolean found = false;
            for (ServiceEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for a query about a service.
     */
    @Test
    public void getResidentTest() {
        ServiceEntity entity = data.get(0);
        ServiceEntity newEntity = servicePersistence.find(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getAvailability(), newEntity.getAvailability());
        Assert.assertEquals(entity.getDescription(), newEntity.getDescription());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());
    }

    /**
     * Test for a query about a service not belonging to a neighborhood.
     */
    @Test(expected = RuntimeException.class)
    public void getResidentTestNotBelonging() {
        ServiceEntity entity = data.get(0);
        servicePersistence.find(entity.getId(), new Long(10000));
    }

    /**
     * Test for updating a service.
     */
    @Test
    public void updateResidentTest() {
        ServiceEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        ServiceEntity newEntity = factory.manufacturePojo(ServiceEntity.class);

        newEntity.setId(entity.getId());
        newEntity.setAuthor(resident);

        servicePersistence.update(newEntity, neighborhood.getId());

        ServiceEntity resp = servicePersistence.find(entity.getId(), neighborhood.getId());

        Assert.assertEquals(newEntity.getTitle(), resp.getTitle());
    }

    /**
     * Test for deleting a service.
     */
    @Test
    public void deleteResidentTest() {
        ServiceEntity entity = data.get(0);
        servicePersistence.delete(entity.getId(), neighborhood.getId());
        ServiceEntity deleted = em.find(ServiceEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }


}
