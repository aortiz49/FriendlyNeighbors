/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
public class EventGroupLogic {

    private static final Logger LOGGER = Logger.getLogger(EventLogic.class.getName());

    @Inject
    private EventPersistence eventPersistence;

    @Inject
    private GroupPersistence groupPersistence;

    /**
     * Associates event with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @param eventId id from event
     * @return associated event
     */
    public EventEntity associateEventToGroup(Long groupId, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between event with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);

        groupEntity.getEvents().add(eventEntity);

        LOGGER.log(Level.INFO, "Association created between event with id {0} and group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
        return eventPersistence.find(eventId, neighId);
    }

    /**
     * Gets a collection of event entities associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @return collection of event entities associated with group
     */
    public List<EventEntity> getEvents(Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to group with id = {0} from neighborhood {1}", new Object[]{groupId, neighId});
        return groupPersistence.find(groupId, neighId).getEvents();
    }

    /**
     * Gets event associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group
     * @param eventId id from associated entity
     * @return associated event
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long groupId, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about event with id {0} from group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
        List<EventEntity> events = groupPersistence.find(groupId, neighId).getEvents();
        EventEntity eventEvents = eventPersistence.find(eventId, neighId);
        int index = events.indexOf(eventEvents);
        LOGGER.log(Level.INFO, "Finish query about event with id {0} from group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
        if (index >= 0) {
            return events.get(index);
        }
        throw new BusinessLogicException("There is no association between event and group");
    }



    /**
     * Unlinks event from group
     *
     * @param neighId parent neighborhood
     * @param groupId Id from group
     * @param eventId Id from event
     */
    public void removeEvent(Long groupId, Long eventId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between event with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        groupEntity.getEvents().remove(eventEntity);
        LOGGER.log(Level.INFO, "Association deleted between event with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{eventId, groupId, neighId});
    }

}
