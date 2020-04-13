/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

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

import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
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

    private NeighborhoodEntity neighborhood;

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
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
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setName("West Park Village");
        em.persist(neighborhood);

        for (int i = 0; i < 3; i++) {
            LocationEntity entity = factory.manufacturePojo(LocationEntity.class);
            entity.setNeighborhood(neighborhood);

            em.persist(entity);
            data.add(entity);

            // add the locations to the neighborhood
            neighborhood.getPlaces().add(data.get(i));

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
        List<LocationEntity> list = locationPersistence.findAll(neighborhood.getId());
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
        LocationEntity newEntity = locationPersistence.find(neighborhood.getId(), entity.getId());
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

        locationPersistence.update(neighborhood.getId(), newEntity);

        LocationEntity resp = em.find(LocationEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }

    /**
     * Test for deleting a Location.
     */
    @Test
    public void deleteLocationTest() {
        LocationEntity entity = data.get(0);
        locationPersistence.delete(neighborhood.getId(), entity.getId());
        LocationEntity deleted = em.find(LocationEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

}
