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

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between group and event.
 *
 * @author aortiz49
 */
@Stateless
public class EventGroupLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(EventGroupLogic.class.getName());

    /**
     * Dependency injection for group persistence.
     */
    @Inject
    private GroupPersistence groupPersistence;

    /**
     * Dependency injection for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Event to a Group.
     *
     * @param pEventId event id
     * @param pGroupId group id
     * @return the event instance that was associated to the group
     * @throws BusinessLogicException when the group or event don't exist
     */
    public EventEntity addEventToGroup(Long pEventId, Long pGroupId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between event and group with id = {0}", pEventId);

        // finds existing group
        GroupEntity groupEntity = groupPersistence.find(pGroupId);

        // group must exist
        if (groupEntity == null) {
            throw new BusinessLogicException("The group must exist.");
        }

        // finds existing event
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // event must exist
        if (eventEntity == null) {
            throw new BusinessLogicException("The event must exist.");
        }

        // add the event to the group
        groupEntity.getEvents().add(eventEntity);

        // add the group to the event
        eventEntity.getGroups().add(groupEntity);

        LOGGER.log(Level.INFO, "End association between event and group with id = {0}", pEventId);
        return eventEntity;
    }

    /**
     * Gets a collection of event entities associated with a group
     *
     * @param pGroupId the group id
     * @return collection of event entities associated with a group
     */
    public List<EventEntity> getEvents(Long pGroupId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to group with id = {0}", pGroupId);

        // returns the list of all events
        return groupPersistence.find(pGroupId).getEvents();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pGroupId the group id
     * @param pEventId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long pGroupId, Long pEventId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding event with id = {0} from group with = " + pEventId, pGroupId);

        // gets all the events in a group
        List<EventEntity> events = groupPersistence.find(pGroupId).getEvents();

        // the busines that was found
        int index = events.indexOf(eventPersistence.find(pEventId));

        // logs end
        LOGGER.log(Level.INFO, "Finish event query with id = {0} from group with = " + pEventId, pGroupId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Event is not associated with the group");
        }

        return events.get(index);
    }

    /**
     * Replaces events associated with a group
     *
     * @param pGroupId the group id
     * @param pNewEventsList Collection of service to associate with resident
     * @return A new collection associated to resident
     */
    public List<EventEntity> replaceEvents(Long pGroupId, List<EventEntity> pNewEventsList) {

        //logs start 
        LOGGER.log(Level.INFO, "Start replacing events related to group with id = {0}", pGroupId);

        // finds the group
        GroupEntity group = groupPersistence.find(pGroupId);

        // finds all the events
        List<EventEntity> currentEventsList = eventPersistence.findAll();

        // replaces events attended by a group.
        // for all events in the database, check if an event in the new list already exists. 
        // if the current event exists in the new list but doesn't already contain the group, add 
        // the group to the current event.
        for (int i = 0; i < currentEventsList.size(); i++) {
            EventEntity current = currentEventsList.get(i);
            if (pNewEventsList.contains(current) && !current.getGroups().contains(group)) {
                current.getGroups().add(group);

            } // if the current event already has the group, remove it since it is not in the list
            // of events we want the group to have
            else if (current.getGroups().contains(group)) {
                current.getGroups().remove(group);
            }
        }

        // logs end
        LOGGER.log(Level.INFO, "End replacing events related to group with id = {0}", pGroupId);

        return pNewEventsList;
    }

    /**
     * Removes a event from a group.
     *
     * @param pGroupId Id from resident
     * @param pEventId Id from service
     */
    public void removeEvent(Long pGroupId, Long pEventId) {
        LOGGER.log(Level.INFO, "Start removing an event from group with id = {0}", pEventId);

        // desired group
        GroupEntity groupEntity = groupPersistence.find(pGroupId);

        // event to delete
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // event to remove from group   
        groupEntity.getEvents().remove(eventEntity);
        
        // group to remove from event
        eventEntity.getGroups().remove(groupEntity);

        LOGGER.log(Level.INFO, "Finished removing an event from group con id = {0}", pEventId);
    }
}
