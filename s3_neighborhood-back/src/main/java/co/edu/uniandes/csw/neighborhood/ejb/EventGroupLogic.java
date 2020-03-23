
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
public class EventGroupLogic {

    private static final Logger LOGGER = Logger.getLogger(EventGroupLogic.class.getName());

    @Inject
    private GroupPersistence groupPersistence;

    @Inject
    private EventPersistence eventPersistence;

    /**
     * Associates group with event
     *
     * @param neighId parent neighborhood
     * @param eventId id from event entity
     * @param groupId id from group
     * @return associated group
     */
    public GroupEntity associateGroupToEvent(Long eventId, Long groupId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between group with id {0} and  event with id {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);

        groupEntity.getEvents().add(eventEntity);

        LOGGER.log(Level.INFO, "Association created between group with id {0} and  event with id  {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
        return groupPersistence.find(groupId, neighId);
    }

    /**
     * Gets a collection of group entities associated with event
     *
     * @param neighId parent neighborhood
     * @param eventId id from event entity
     * @return collection of group entities associated with event
     */
    public List<GroupEntity> getGroups(Long eventId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all groups belonging to event with id {0} from neighborhood {1}", new Object[]{eventId, neighId});
        return eventPersistence.find(eventId, neighId).getGroups();
    }

    /**
     * Gets group associated with event
     *
     * @param neighId parent neighborhood
     * @param eventId id from event
     * @param groupId id from associated entity
     * @return associated group
     * @throws BusinessLogicException If group is not associated
     */
    public GroupEntity getGroup(Long eventId, Long groupId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about group with id {0} from event with {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
        List<GroupEntity> groups = eventPersistence.find(eventId, neighId).getGroups();
        GroupEntity groupGroups = groupPersistence.find(groupId, neighId);
        int index = groups.indexOf(groupGroups);
        LOGGER.log(Level.INFO, "Finish query about group with id {0} from event with {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
        if (index >= 0) {
            return groups.get(index);
        }
        throw new BusinessLogicException("There is no association between event and group");
    }

    /**
     * Replaces groups associated with event
     *
     * @param neighId parent neighborhood
     * @param eventId id from event
     * @param groups collection of group to associate with event
     * @return A new collection associated to event
     */
    public List<GroupEntity> replaceGroups(Long eventId, List<GroupEntity> groups, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace groups related to event with id {0} from neighborhood {1}", new Object[]{eventId, neighId});
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);
        List<GroupEntity> groupList = groupPersistence.findAll(neighId);
        for (GroupEntity group : groupList) {
            if (groups.contains(group)) {
                if (!group.getEvents().contains(eventEntity)) {
                    group.getEvents().add(eventEntity);
                }
            } else {
                group.getEvents().remove(eventEntity);
            }
        }
        eventEntity.setGroups(groups);
        LOGGER.log(Level.INFO, "Ended trying to replace groups related to event with id {0} from neighborhood {1}", new Object[]{eventId, neighId});
        return eventEntity.getGroups();
    }

    /**
     * Unlinks group from event
     *
     * @param neighId parent neighborhood
     * @param eventId Id from event
     * @param groupId Id from group
     */
    public void removeGroup(Long eventId, Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between group with id {0} and  event with id {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
        EventEntity eventEntity = eventPersistence.find(eventId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        groupEntity.getEvents().remove(eventEntity);
        LOGGER.log(Level.INFO, "Association deleted between group with id {0} and  event with id {1}, from neighbothood {2}", new Object[]{groupId, eventId, neighId});
    }
}
