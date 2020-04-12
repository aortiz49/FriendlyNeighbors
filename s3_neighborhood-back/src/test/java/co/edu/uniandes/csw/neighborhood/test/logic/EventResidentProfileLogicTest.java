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
package co.edu.uniandes.csw.neighborhood.test.logic;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.ejb.EventResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
 * Tests the BusinessResidentProfileLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class EventResidentProfileLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for business/residentProfile logic.
     */
    @Inject
    private EventResidentProfileLogic eventResidentProfileLogic;

    /**
     * Entity manager to communicate with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    private UserTransaction utx;

    /**
     * ResidentProfile to be used in the tests.
     */
    private ResidentProfileEntity host;

    /**
     * Location to be used in the tests.
     */
    private LocationEntity location;

    /**
     * List of events to be used in the tests.
     */
    private List<EventEntity> events = new ArrayList<>();

    /**
     * Neighborhood to be used in the tests.
     */
    private NeighborhoodEntity neighborhood;

//===================================================
// Test Setup
//===================================================
    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(ResidentProfileLogic.class.getPackage())
                .addPackage(ResidentProfilePersistence.class.getPackage())
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates a neighborhood
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        // create a host
        host = factory.manufacturePojo(ResidentProfileEntity.class);
        host.setNeighborhood(neighborhood);
        host.setName("Danielle Diaz");
        em.persist(host);

        // creates a location
        location = factory.manufacturePojo(LocationEntity.class);
        em.persist(location);

        // creates 3 random businesses
        for (int i = 0; i < 3; i++) {
            EventEntity event = factory.manufacturePojo(EventEntity.class);

            event.setHost(host);
            event.setLocation(location);

            em.persist(event);
            events.add(event);
        }

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the consultation of all event entities hosted by a resident.
     */
    @Test
    public void getHostedEventsTest() {

        List<EventEntity> list = eventResidentProfileLogic.
                getHostedEvents(neighborhood.getId(), host.getId());

        // checks that there are three events hosted by the resident
        Assert.assertEquals(3, list.size());

        // checks that the name of the host matches
        Assert.assertEquals(list.get(0).getHost().getName(), host.getName());
    }

    /**
     * Tests the consultation of an event entity hosted by a resident.
     *
     * @throws BusinessLogicException if the event is not found
     */
    @Test
    public void getHostedEventTest() throws BusinessLogicException {

        // gets the first event from the list
        EventEntity event = events.get(0);

        // get the business from the residentProfile
        EventEntity response = eventResidentProfileLogic.getHostedEvent(
                neighborhood.getId(), host.getId(), event.getId());

        Assert.assertEquals(event.getId(), response.getId());

    }

    /**
     * Tests the removal of an event from the host.
     *
     * @throws BusinessLogicException if cannot delete the event
     */
    @Test
    public void removeHostedEventTest() throws BusinessLogicException {
        // gets the first residentProfile from the list. 
        // (Uses em.find because the persisted residentProfile contains the added businesses)
        ResidentProfileEntity residentProfile = em.find(ResidentProfileEntity.class,
                host.getId());

        // get the first hosted event
        EventEntity event = events.get(0);

        // gets the list of events in the residentProfile
        List<EventEntity> list = em.find(ResidentProfileEntity.class,
                host.getId()).getEvents();

        eventResidentProfileLogic.removeHostedEvent(neighborhood.getId(),
                residentProfile.getId(), event.getId());
        Assert.assertEquals(2, list.size());

    }
}
