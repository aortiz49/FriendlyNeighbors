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
package co.edu.uniandes.csw.neighborhood.ejb;
//===================================================
// Imports
//===================================================

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
 * Logic that handles events attended by a resident.
 *
 * @author albayona
 */
@Stateless
public class AttendeeEventsLogic {

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(AttendeeEventsLogic.class.getName());

    /**
     * Dependency injection for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

    /**
     * Dependency injection for resident profile persistence.
     */
    @Inject
    private ResidentProfilePersistence attendeePersistence;

//===================================================
// Methods
//===================================================    
    /**
     * Adds an attended event to a resident.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pAttendeeId the attendee's id
     * @param pEventId the event's id
     *
     * @return the added event
     */
    public EventEntity associateEventToAttendee(Long pNeighborhoodId, Long pAttendeeId, Long pEventId) {

        LOGGER.log(Level.INFO, "Begin adding event with id {0} to attendee with id {1}, "
                + "from neighborhood {2}", new Object[]{pEventId, pAttendeeId, pNeighborhoodId});

        // finds the attendee to add the event to
        ResidentProfileEntity attendeeEntity = attendeePersistence.
                find(pNeighborhoodId, pAttendeeId);

        // finds the event to add to the attendee
        EventEntity eventEntity = eventPersistence.find(pNeighborhoodId, pEventId);

        eventEntity.getAttendees().add(attendeeEntity);

        LOGGER.log(Level.INFO, "End adding event with id {0} to attendee with id {1}, "
                + "from neighborhood {2}", new Object[]{pEventId, pAttendeeId, pNeighborhoodId});
        return eventEntity;
    }

    /**
     * Gets a collection of event entities associated with attendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee entity
     * @return collection of event entities associated with attendee
     */
    public List<EventEntity> getAttendedEvents(Long neighId, Long attendeeId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to attendee with id {0} from neighborhood {1}", new Object[]{attendeeId, neighId});
        return attendeePersistence.find(attendeeId, neighId).getAttendedEvents();
    }

    /**
     * Gets event associated with attendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee
     * @param eventId id from associated entity
     * @return associated event
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getAttendedEvent(Long neighId, Long attendeeId, Long eventId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about event with id {0} from attendee with {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        List<EventEntity> events = attendeePersistence.find(attendeeId, neighId).getAttendedEvents();
        EventEntity eventEvents = eventPersistence.find(neighId, eventId);
        int index = events.indexOf(eventEvents);
        LOGGER.log(Level.INFO, "Finish query about event with id {0} from attendee with {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        if (index >= 0) {
            return events.get(index);
        }
        throw new BusinessLogicException("There is no association between attendee and event");
    }

    /**
     * Replaces events associated with attendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId id from attendee
     * @param events collection of event to associate with attendee
     * @return A new collection associated to attendee
     */
    public List<EventEntity> replaceAttendedEvents(Long neighId, Long attendeeId, List<EventEntity> events) {
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
        return attendeeEntity.getAttendedEvents();
    }

    /**
     * Unlinks event from attendee
     *
     * @param neighId parent neighborhood
     * @param attendeeId Id from attendee
     * @param eventId Id from event
     */
    public void removeAttendedEvent(Long neighId, Long attendeeId, Long eventId) {
        LOGGER.log(Level.INFO, "Deleting association between event with id {0} and  attendee with id {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId, neighId);
        EventEntity eventEntity = eventPersistence.find(neighId, eventId);
        eventEntity.getAttendees().remove(attendeeEntity);
        LOGGER.log(Level.INFO, "Association deleted between event with id {0} and  attendee with id {1}, from neighbothood {2}", new Object[]{eventId, attendeeId, neighId});
    }
}
