/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import java.util.logging.Level;
import javax.inject.Inject;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class EventLocationLogic {
    
    private static final Logger LOGGER = Logger.getLogger(EventLocationLogic.class.getName());
    
    @Inject
    private EventPersistence eventPersistence;
    
    @Inject
    private LocationPersistence locationPersistence;
    
    /**
     * Associates an event with a location
     * 
     * @param eventId ID from the event entity
     * @param locationId ID prom location
     * @return associated event
     */
    public EventEntity associateEventToLocation(Long eventId, Long locationId){
        LOGGER.log(Level.INFO, "Trying to add event with id - {0}", eventId);
        LocationEntity locationEntity = locationPersistence.find(locationId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        eventEntity.setLocation(locationEntity);
        LOGGER.log(Level.INFO, "Event is associatedd with location with id = {0}", locationId);
        return eventPersistence.find(eventId);
    }
    
    /**
     * Gets a collection of events entities associated with a location
     * @param locationId ID from the location entity
     * @return collection of events associated with a location
     */
    public List<EventEntity> getEvents(Long locationId){
        LOGGER.log(Level.INFO, "Gets all events belonging to location with id = {0}", locationId);
        return locationPersistence.find(locationId).getEvents();
    }
    
    /**
     * Gets an event associated with a location
     * 
     * @param locationId ID from location
     * @param eventId ID from associated
     * @return associated event
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long locationId, Long eventId) throws BusinessLogicException{
        LOGGER.log(Level.INFO, "Finding event with id = {0} from location with id = " + locationId, eventId);
        List<EventEntity> events = locationPersistence.find(locationId).getEvents();
        EventEntity event = eventPersistence.find(eventId);
        int index = events.indexOf(event);
        LOGGER.log(Level.INFO, "Finish query about event id = {0} from location with id = "+ locationId, eventId);
        if(index >= 0){
            return events.get(index);
        }
        throw new BusinessLogicException("There is no association between event and location");
    }
    
    /**
     * Replaces events associated with a location
     * 
     * @param locationId ID from location
     * @param events Collections of events to asociate with location
     * @return A new collection associated to location
     */
    public List<EventEntity> replaceEvents(Long locationId, List<EventEntity> events){
        LOGGER.log(Level.INFO, "Trying to replace events related to location with id = {0}", locationId);
        LocationEntity locationEntity = locationPersistence.find(locationId);
        List<EventEntity> eventsList = eventPersistence.findAll();
        for(EventEntity event: eventsList){
            if(events.contains(event)){
                if(event.getLocation() != locationEntity){
                    event.setLocation(locationEntity);
                }
            } else{
                event.setLocation(null);
            }    
        }
        locationEntity.setEvents(events);
        LOGGER.log(Level.INFO, "Ended trying to replace events related to location with id = {0}", locationId);
        return locationEntity.getEvents();
    }
    
    /**
     * Unlinks an event from a location
     * 
     * @param locationId Id from location
     * @param eventId  Id from event
     */
    public void removeEvent(Long locationId, Long eventId){
        LOGGER.log(Level.INFO, "Trying to delete an event from location with id = {0}", locationId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        LocationEntity locationEntity = locationPersistence.find(locationId);
        locationEntity.getEvents().remove(eventEntity);
        LOGGER.log(Level.INFO, "Finished removing an event from location with id = {0}", locationId);
    }
}
