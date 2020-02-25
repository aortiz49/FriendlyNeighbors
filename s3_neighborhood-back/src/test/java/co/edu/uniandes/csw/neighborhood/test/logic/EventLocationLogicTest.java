/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.EventLocationLogic;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
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
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class EventLocationLogicTest {
    
    private PodamFactory factory = new PodamFactoryImpl();
    
    @Inject
    private EventLocationLogic eventLocationLogic;
    
    @Inject
    private EventLogic eventLogic;
    
    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private LocationEntity location = new LocationEntity();
    private List<EventEntity> data = new ArrayList<>();
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EventEntity.class.getPackage())
                .addPackage(LocationEntity.class.getPackage())
                .addPackage(LocationLogic.class.getPackage())
                .addPackage(EventPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }
    
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
    
    private void clearData(){
        em.createQuery("delete from EventEntity").executeUpdate();
        em.createQuery("delete from LocationEntity").executeUpdate();
    }
    
    private void insertData(){
        location = factory.manufacturePojo(LocationEntity.class);
        location.setId(1L);
        location.setEvents(new ArrayList<>());
        em.persist(location);
        
        for (int i = 0; i < 3; i++) {
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
            
            entity.setLocation(location);
            em.persist(entity);
            data.add(entity);
            location.getEvents().add(entity);
        }
    }
    
    @Test
    public void addEventTest() throws BusinessLogicException {
        EventEntity newEvent = factory.manufacturePojo(EventEntity.class);
        eventLogic.createEvent(newEvent);
        
        EventEntity eventEntity = eventLocationLogic.associateEventToLocation(newEvent.getId(), location.getId());
        Assert.assertNotNull(eventEntity);
        
        Assert.assertEquals(eventEntity.getId(), newEvent.getId());
        Assert.assertEquals(eventEntity.getDescription(), newEvent.getDescription());
        
        EventEntity lastEvent = eventLocationLogic.getEvent(location.getId(), newEvent.getId());
        Assert.assertEquals(lastEvent.getId(), newEvent.getId());               
    }
    
    @Test
    public void getEventsTest(){
        List<EventEntity> eventEntities = eventLocationLogic.getEvents(location.getId());
        Assert.assertEquals(data.size(), eventEntities.size());
        
        for (int i = 0; i < data.size(); i++){
            Assert.assertTrue(eventEntities.contains(data.get(0)));
        }
    }
    
    @Test
    public void getEventTest() throws BusinessLogicException{
        EventEntity eventEntity = data.get(0);
        EventEntity event = eventLocationLogic.getEvent(location.getId(), eventEntity.getId());
        Assert.assertNotNull(event);
        
        Assert.assertEquals(eventEntity.getId(), event.getId());
        Assert.assertEquals(eventEntity.getDescription(), event.getDescription());
    }
    
    @Test
    public void replaceEventsTest() throws BusinessLogicException{
        List<EventEntity> newCollection = new ArrayList<>();
        for( int i = 0; i < 3; i++ ){
            EventEntity entity = factory.manufacturePojo(EventEntity.class);
           
            eventLogic.createEvent(entity);
            entity.setLocation(location);
            newCollection.add(entity);
        }
        eventLocationLogic.replaceEvents(location.getId(), newCollection);
        List<EventEntity> eventEntities = eventLocationLogic.getEvents(location.getId());
        for(EventEntity aNuevaLista: newCollection){
            Assert.assertTrue(eventEntities.contains(aNuevaLista));
        }
    }
    
    @Test
    public void removeEventTest(){
        for(EventEntity event : data){
            eventLocationLogic.removeEvent(location.getId(), event.getId());
        }
        Assert.assertTrue(eventLocationLogic.getEvents(location.getId()).isEmpty());
    }
}
