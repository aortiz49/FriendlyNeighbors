/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;


import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author albayona
 */
@Stateless
public class AttendeeEventLogic {

    private static final Logger LOGGER = Logger.getLogger(AttendeeEventLogic.class.getName());

    @Inject
    private EventPersistence eventPersistence;

    @Inject
    private ResidentProfilePersistence attendeePersistence;

    /**
     * Associates an event with a attendee
     *
     * @param attendeeId ID from attendee entity
     * @param eventId ID from event entity
     * @return associated event entity
     */
    public EventEntity associateEventToAttenddee(Long attendeeId, Long eventId) {
       LOGGER.log(Level.INFO, "Trying to associate event with attendee with id = {0}", attendeeId);
         ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        eventEntity.getAttendees().add(attendeeEntity);
        
        LOGGER.log(Level.INFO, "Event is associated with attendee with id = {0}", attendeeId);
       return eventPersistence.find(eventId);
    }

    /**
     * Gets a collection of event entities associated with a attendee 
     * @param attendeeId ID from attendee entity
     * @return collection of event entities associated with a attendee 
     */
    public List<EventEntity> getEvents(Long attendeeId) {
       LOGGER.log(Level.INFO, "Gets all events belonging to attendee with id = {0}", attendeeId);
            return attendeePersistence.find(attendeeId).getEventsToAttend();
    }

    /**
     * Gets an event entity associated with a a attendee
     *
     * @param attendeeId Id from attendee
     * @param eventId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated 
     */
    public EventEntity getEvent(Long attendeeId, Long eventId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding event with id = {0} from attendee with = " + attendeeId, eventId);
   List<EventEntity> events = attendeePersistence.find(attendeeId).getEventsToAttend();
        EventEntity eventEvents = eventPersistence.find(eventId);
        int index = events.indexOf(eventEvents);
       LOGGER.log(Level.INFO, "Finish query about event with id = {0} from attendee with = " + attendeeId, eventId);
        if (index >= 0) {
            return events.get(index);
        }
         throw new BusinessLogicException("There is no association between attendee and event");
    }

    /**
     * Replaces events associated with a attendee
     *
     * @param attendeeId Id from attendee
     * @param events Collection of event to associate with attendee
     * @return A new collection associated to attendee
     */
    public List<EventEntity> replaceEvents(Long attendeeId, List<EventEntity> events) {
        LOGGER.log(Level.INFO, "Trying to replace events related to attendee con id = {0}", attendeeId);
          ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId);
        List<EventEntity> eventList = eventPersistence.findAll();
        for (EventEntity event : eventList) {
            if (events.contains(event)) {
                if (!event.getAttendees().contains(attendeeEntity)) {
                    event.getAttendees().add(attendeeEntity);
                }
            } else {
                event.getAttendees().remove(attendeeEntity);
            }
        }
        attendeeEntity.setEventsToAttend(events);
       LOGGER.log(Level.INFO, "Ended trying to replace events related to attendee con id = {0}", attendeeId);
           return attendeeEntity.getEventsToAttend();
    }

    /**
     * Unlinks an event from a attendee
     *
     * @param attendeeId Id from attendee
     * @param eventId Id from event     
     */
    public void removeEvent(Long attendeeId, Long eventId) {
         LOGGER.log(Level.INFO, "Trying to delete an event from attendee con id = {0}", attendeeId);
       ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        eventEntity.getAttendees().remove(attendeeEntity);
       LOGGER.log(Level.INFO, "Finished removing an event from attendee con id = {0}", attendeeId);
        }
}
