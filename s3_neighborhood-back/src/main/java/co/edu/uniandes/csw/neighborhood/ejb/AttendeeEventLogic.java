
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
     * Associates event with ttendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee entity
     * @param eventId id from event
     * @return associated event
     */
    public EventEntity associateEventToAttendee(Long attendeeId, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between event with id {0} and  attendee with id {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId, neighId);
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);

        eventEntity.getAttendees().add(attendeeEntity);

        LOGGER.log(Level.INFO, "Association created between event with id {0} and  attendee with id  {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        return eventPersistence.find(eventId, neighId);
    }

    /**
     * Gets a collection of event entities associated with ttendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee entity
     * @return collection of event entities associated with ttendee
     */
    public List<EventEntity> getEvents(Long attendeeId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to attendee with id {0} from neighborhood {1}", new Object[]{attendeeId, neighId});
        return attendeePersistence.find(attendeeId, neighId).getEventsToAttend();
    }

    /**
     * Gets event associated with ttendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee
     * @param eventId id from associated entity
     * @return associated event
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long attendeeId, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about event with id {0} from attendee with {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        List<EventEntity> events = attendeePersistence.find(attendeeId, neighId).getEventsToAttend();
        EventEntity eventEvents = eventPersistence.find(eventId, neighId);
        int index = events.indexOf(eventEvents);
        LOGGER.log(Level.INFO, "Finish query about event with id {0} from attendee with {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        if (index >= 0) {
            return events.get(index);
        }
        throw new BusinessLogicException("There is no association between attendee and event");
    }

    /**
     * Replaces events associated with ttendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee
     * @param events collection of event to associate with ttendee
     * @return A new collection associated to attendee
     */
    public List<EventEntity> replaceEvents(Long attendeeId, List<EventEntity> events, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace events related to attendee with id {0} from neighborhood {1}", new Object[]{attendeeId, neighId});
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId, neighId);
        List<EventEntity> eventList = eventPersistence.findAll(neighId);
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
        LOGGER.log(Level.INFO, "Ended trying to replace events related to attendee with id {0} from neighborhood {1}", new Object[]{attendeeId, neighId});
        return attendeeEntity.getEventsToAttend();
    }

    /**
     * Unlinks event from attendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId Id from attendee
     * @param eventId Id from event
     */
    public void removeEvent(Long attendeeId, Long eventId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between event with id {0} and  attendee with id {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId, neighId);
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);
        eventEntity.getAttendees().remove(attendeeEntity);
        LOGGER.log(Level.INFO, "Association deleted between event with id {0} and  attendee with id {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
    }
}

  