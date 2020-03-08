package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.*;
import co.edu.uniandes.csw.neighborhood.entities.*;

import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.*;
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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class EventAttendeeLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private EventAttendeeLogic eventResidentProfileLogic;

    @Inject
    private ResidentProfileLogic residentLogic;

    @Inject
    private NeighborhoodPersistence neighPersistence;

    private NeighborhoodEntity neighborhood;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private EventEntity event = new EventEntity();
    private List<ResidentProfileEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(EventAttendeeLogic.class.getPackage())
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
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighPersistence.create(neighborhood);

        event = factory.manufacturePojo(EventEntity.class);
        event.setId(1L);
        event.setAttendees(new ArrayList<>());
        
        
         ResidentProfileEntity author = factory.manufacturePojo(ResidentProfileEntity.class);
         author.setNeighborhood(neighborhood);
         em.persist(author);

         
        event.setHost(author);

        em.persist(event);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            entity.setEventsToAttend(new ArrayList<>());
            entity.getEventsToAttend().add(event);
            entity.setNeighborhood(neighborhood);
            
            
            em.persist(entity);
            data.add(entity);
            event.getAttendees().add(entity);
        }
    }

    /**
     * Test to associate a resident with an event
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addResidentTest() throws BusinessLogicException {
        ResidentProfileEntity newResidentProfile = factory.manufacturePojo(ResidentProfileEntity.class);

        newResidentProfile.setNeighborhood(neighborhood);

        residentLogic.createResident(newResidentProfile);

        ResidentProfileEntity residentEntity = eventResidentProfileLogic.associateResidentProfileToEvent(event.getId(), newResidentProfile.getId());
        Assert.assertNotNull(residentEntity);

        Assert.assertEquals(residentEntity.getId(), newResidentProfile.getId());
        Assert.assertEquals(residentEntity.getAddress(), newResidentProfile.getAddress());

        ResidentProfileEntity lastResident = eventResidentProfileLogic.getResidentProfile(event.getId(), newResidentProfile.getId());

        Assert.assertEquals(lastResident.getId(), newResidentProfile.getId());

    }

    /**
     * Test for getting a collection of resident entities associated with an
     * event
     */
    @Test
    public void getResidentProfilesTest() {
        List<ResidentProfileEntity> residentEntities = eventResidentProfileLogic.getResidentProfiles(event.getId());

        Assert.assertEquals(data.size(), residentEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(residentEntities.contains(data.get(0)));
        }
    }

    /**
     * Test for getting a resident entity associated with an event
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getResidentTest() throws BusinessLogicException {
        ResidentProfileEntity residentEntity = data.get(0);
        ResidentProfileEntity resident = eventResidentProfileLogic.getResidentProfile(event.getId(), residentEntity.getId());
        Assert.assertNotNull(resident);

        Assert.assertEquals(residentEntity.getId(), resident.getId());
        Assert.assertEquals(residentEntity.getAddress(), resident.getAddress());

    }

    /**
     * Test for replacing residents associated with an event
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceResidentProfilesTest() throws BusinessLogicException {
        List<ResidentProfileEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            entity.setEventsToAttend(new ArrayList<>());
            entity.getEventsToAttend().add(event);

            entity.setNeighborhood(neighborhood);
            residentLogic.createResident(entity);

            newCollection.add(entity);
        }
        eventResidentProfileLogic.replaceResidentProfiles(event.getId(), newCollection);
        List<ResidentProfileEntity> residentEntities = eventResidentProfileLogic.getResidentProfiles(event.getId());
        for (ResidentProfileEntity aNuevaLista : newCollection) {
            Assert.assertTrue(residentEntities.contains(aNuevaLista));
        }
    }

    /**
     * Test for removing a resident from an event
     *
     */
    @Test
    public void removeResidentTest() {
        for (ResidentProfileEntity resident : data) {
            eventResidentProfileLogic.removeResidentProfile(event.getId(), resident.getId());
        }
        Assert.assertTrue(eventResidentProfileLogic.getResidentProfiles(event.getId()).isEmpty());
    }

}
