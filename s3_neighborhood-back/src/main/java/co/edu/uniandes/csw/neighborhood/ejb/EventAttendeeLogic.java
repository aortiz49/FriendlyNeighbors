/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
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
public class EventAttendeeLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfileLogic.class.getName());

    @Inject
    private ResidentProfilePersistence attendeePersistence;

    @Inject
    private EventPersistence eventPersistence;

    /**
     * Associates a attendee with an event
     *
     * @param eventId ID from event entity
     * @param attendeeId ID from attendee
     * @return associated attendee
     */
    public ResidentProfileEntity associateResidentProfileToEvent(Long eventId, Long attendeeId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to add attendee to event with id = {0}", eventId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId);

        if (attendeeEntity.getNeighborhood().getId() != eventEntity.getHost().getNeighborhood().getId()) {
            throw new BusinessLogicException("Event and attendee must belong to the same neighborhood");
        }

        eventEntity.getAttendees().add(attendeeEntity);

        LOGGER.log(Level.INFO, "Resident is associated with event with id = {0}", eventId);
        return attendeePersistence.find(attendeeId);
    }

    /**
     * Gets a collection of attendee entities associated with an event
     *
     * @param eventId ID from event entity
     * @return collection of attendee entities associated with an event
     */
    public List<ResidentProfileEntity> getResidentProfiles(Long eventId) {
        LOGGER.log(Level.INFO, "Gets all attendees belonging to event with id = {0}", eventId);
        return eventPersistence.find(eventId).getAttendees();
    }

    /**
     * Gets an attendee associated with an event
     *
     * @param eventId Id from event
     * @param attendeeId Id from associated entity
     * @return associated attendee
     * @throws BusinessLogicException If attendee is not associated
     */
    public ResidentProfileEntity getResidentProfile(Long eventId, Long attendeeId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding attendee with id = {0} from event with = " + eventId, attendeeId);
        List<ResidentProfileEntity> attendees = eventPersistence.find(eventId).getAttendees();
        ResidentProfileEntity attendeeResidentProfiles = attendeePersistence.find(attendeeId);
        int index = attendees.indexOf(attendeeResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about attendee with id = {0} from event with = " + eventId, attendeeId);
        if (index >= 0) {
            return attendees.get(index);
        }
        throw new BusinessLogicException("There is no association between attendee and event");
    }

    /**
     * Replaces attendees associated with a event
     *
     * @param eventId Id from event
     * @param attendees Collection of attendee to associate with event
     * @return A new collection associated to event
     */
    public List<ResidentProfileEntity> replaceResidentProfiles(Long eventId, List<ResidentProfileEntity> attendees) {
        LOGGER.log(Level.INFO, "Trying to replace attendees related to event with id = {0}", eventId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        List<ResidentProfileEntity> attendeeList = attendeePersistence.findAll();
        for (ResidentProfileEntity attendee : attendeeList) {
            if (attendees.contains(attendee)) {
                if (!attendee.getEventsToAttend().contains(eventEntity)) {
                    attendee.getEventsToAttend().add(eventEntity);
                }
            } else {
                attendee.getEventsToAttend().remove(eventEntity);
            }
        }
        eventEntity.setAttendees(attendees);
        LOGGER.log(Level.INFO, "Ended trying to replace attendees related to event with id = {0}", eventId);
        return eventEntity.getAttendees();
    }

    /**
     * Unlinks an attendee from a event
     *
     * @param eventId Id from event
     * @param attendeeId Id from attendee
     */
    public void removeResidentProfile(Long eventId, Long attendeeId) {
        LOGGER.log(Level.INFO, "Trying to delete an attendee from event with id = {0}", eventId);
        ResidentProfileEntity attendeeEntity = attendeePersistence.find(attendeeId);
        EventEntity eventEntity = eventPersistence.find(eventId);
        eventEntity.getAttendees().remove(attendeeEntity);
        LOGGER.log(Level.INFO, "Finished removing an attendee from event with id = {0}", eventId);
    }

}
