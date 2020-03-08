/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between event and group.
 *
 * @author aortiz49
 */
@Stateless
public class GroupEventLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(GroupEventLogic.class.getName());

    /**
     * Dependency injection for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

    /**
     * Dependency injection for group persistence.
     */
    @Inject
    private GroupPersistence groupPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Group to a Event.
     *
     * @param pGroupId group id
     * @param pEventId event id
     * @return the group instance that was associated to the event
     * @throws BusinessLogicException when the event or group don't exist
     */
    public GroupEntity addGroupToEvent(Long pGroupId, Long pEventId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between group and event with id = {0}", pGroupId);

        // finds existing event
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // event must exist
        if (eventEntity == null) {
            throw new BusinessLogicException("The event must exist.");
        }

        // finds existing group
        GroupEntity groupEntity = groupPersistence.find(pGroupId);

        // group must exist
        if (groupEntity == null) {
            throw new BusinessLogicException("The group must exist.");
        }

        // add the group to the event
        eventEntity.getGroups().add(groupEntity);

        // add the event to the group
        groupEntity.getEvents().add(eventEntity);

        LOGGER.log(Level.INFO, "End association between group and event with id = {0}", pGroupId);
        return groupEntity;
    }

    /**
     * Gets a collection of group entities associated with a event
     *
     * @param pEventId the event id
     * @return collection of group entities associated with a event
     */
    public List<GroupEntity> getGroups(Long pEventId) {
        LOGGER.log(Level.INFO, "Gets all groups belonging to event with id = {0}", pEventId);

        // returns the list of all groups
        return eventPersistence.find(pEventId).getGroups();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pEventId the event id
     * @param pGroupId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If group is not associated
     */
    public GroupEntity getGroup(Long pEventId, Long pGroupId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding group with id = {0} from event with = " + pGroupId, pEventId);

        // gets all the groups in a event
        List<GroupEntity> groups = eventPersistence.find(pEventId).getGroups();

        // the busines that was found
        int index = groups.indexOf(groupPersistence.find(pGroupId));

        // logs end
        LOGGER.log(Level.INFO, "Finish group query with id = {0} from event with = " + pGroupId, pEventId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Group is not associated with the event");
        }

        return groups.get(index);
    }

    /**
     * Replaces groups associated with a event
     *
     * @param pEventId the event id
     * @param pNewGroupsList Collection of service to associate with resident
     * @return A new collection associated to resident
     */
    public List<GroupEntity> replaceGroups(Long pEventId, List<GroupEntity> pNewGroupsList) {

        //logs start 
        LOGGER.log(Level.INFO, "Start replacing groups related to event with id = {0}", pEventId);

        // finds the event
        EventEntity event = eventPersistence.find(pEventId);

        // finds all the groups
        List<GroupEntity> currentGroupsList = groupPersistence.findAll();

        // replaces groups attended by a event.
        // for all groups in the database, check if an group in the new list already exists. 
        // if the current group exists in the new list but doesn't already contain the event, add 
        // the event to the current group.
        for (int i = 0; i < currentGroupsList.size(); i++) {
            GroupEntity current = currentGroupsList.get(i);
            if (pNewGroupsList.contains(current) && !current.getEvents().contains(event)) {
                current.getEvents().add(event);

            } // if the current group already has the event, remove it since it is not in the list
            // of groups we want the event to have
            else if (current.getEvents().contains(event)) {
                current.getEvents().remove(event);
            }
        }

        // logs end
        LOGGER.log(Level.INFO, "End replacing groups related to event with id = {0}", pEventId);

        return pNewGroupsList;
    }

    /**
     * Removes a group from a event.
     *
     * @param pEventId Id from resident
     * @param pGroupId Id from service
     */
    public void removeGroup(Long pEventId, Long pGroupId) {
        LOGGER.log(Level.INFO, "Start removing a group from event with id = {0}", pGroupId);

        // desired event
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // group to delete
        GroupEntity groupEntity = groupPersistence.find(pGroupId);

        // event to remove from group   
        eventEntity.getGroups().remove(groupEntity);

        LOGGER.log(Level.INFO, "Finished removing a group from event con id = {0}", pGroupId);
    }
}
