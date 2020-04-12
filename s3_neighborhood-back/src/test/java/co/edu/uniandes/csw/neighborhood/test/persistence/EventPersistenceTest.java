/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
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
 * Persistence test for Event
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventPersistenceTest {
//===================================================
// Attributes
//===================================================

    /**
     * Injected persistence dependence for the tests.
     */
    @Inject
    private EventPersistence eventPersistence;

    /**
     * Entity manager for the tests.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * Injected user transaction for the tests.
     */
    @Inject
    UserTransaction utx;

    /**
     * The neighborhood where the event takes place.
     */
    NeighborhoodEntity neighborhood;

    /**
     * The event host.
     */
    ResidentProfileEntity host;

    /**
     * The event location.
     */
    LocationEntity location;

    private List<EventEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(EventPersistence.class.getPackage())
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
        em.createQuery("delete from EventEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates a factory to generate java objects
        PodamFactory factory = new PodamFactoryImpl();

        // creates a random neighborhood
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);

        // persists the neighborhood
        em.persist(neighborhood);

        // creates a random host
        host = factory.manufacturePojo(ResidentProfileEntity.class);
        host.setNeighborhood(neighborhood);

        // perist the host
        em.persist(host);

        // creates a random location
        location = factory.manufacturePojo(LocationEntity.class);
        location.setNeighborhood(neighborhood);

        // persist the location
        em.persist(location);

        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            entity.setHost(host);
            entity.setLocation(location);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating event.
     */
    @Test
    public void createEventTest() {
        PodamFactory factory = new PodamFactoryImpl();

        // creates a random event
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        // persists the event
        EventEntity result = eventPersistence.create(newEntity);

        Assert.assertNotNull(result);

        EventEntity entity = em.find(EventEntity.class, result.getId());

        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

    }

    /**
     * Test for retrieving all residents from DB.
     */
    @Test
    public void findAllTest() {

        List<EventEntity> list = eventPersistence.findAll(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (EventEntity ent : list) {
            boolean found = false;
            for (EventEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for a query about a event.
     */
    @Test
    public void getResidentTest() {
        EventEntity entity = data.get(0);
        EventEntity newEntity = eventPersistence.find(neighborhood.getId(), entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getDateOfEvent(), newEntity.getDateOfEvent());
        Assert.assertEquals(entity.getStartTime(), newEntity.getStartTime());
        Assert.assertEquals(entity.getEndTime(), newEntity.getEndTime());
        Assert.assertEquals(entity.getDescription(), newEntity.getDescription());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());
    }

    /**
     * Test for a query about event not belonging to a neighborhood.
     */
    @Test(expected = RuntimeException.class)
    public void getResidentTestNotBelonging() {
        EventEntity entity = data.get(0);
        eventPersistence.find(new Long(10000), entity.getId());
    }

    /**
     * Test for updating event.
     */
    @Test
    public void updateResidentTest() {
        EventEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        EventEntity newEntity = factory.manufacturePojo(EventEntity.class);

        newEntity.setId(entity.getId());
        newEntity.setHost(host);

        eventPersistence.update(neighborhood.getId(), newEntity);

        EventEntity resp = eventPersistence.find(neighborhood.getId(), entity.getId());

        Assert.assertEquals(newEntity.getTitle(), resp.getTitle());
    }

    /**
     * Test for deleting event.
     */
    @Test
    public void deleteResidentTest() {
        EventEntity entity = data.get(0);
        eventPersistence.delete(neighborhood.getId(),entity.getId());
        EventEntity deleted = em.find(EventEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Test to consult event by name.
     */
    @Test
    public void findEventByTitleTest() {
        EventEntity entity = data.get(0);
        EventEntity newEntity = eventPersistence.findByTitle(entity.getTitle());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

        newEntity = eventPersistence.findByTitle(null);
        Assert.assertNull(newEntity);
    }

}
