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
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between location and event.
 *
 * @author aortiz49
 */
@Stateless
public class EventLocationLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(EventLocationLogic.class.getName());

    /**
     * Dependency injection for location persistence.
     */
    @Inject
    private LocationPersistence locationPersistence;

    /**
     * Dependency injection for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Event to a Location.
     *
     * @param pEventId event id
     * @param pLocationId location id
     * @return the event instance that was associated to the location
     * @throws BusinessLogicException when the location or event don't exist
     */
    public EventEntity addEventToLocation(Long pEventId, Long pLocationId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between event and location with id = {0}", pEventId);

        // finds existing location
        LocationEntity locationEntity = locationPersistence.find(pLocationId);

        // location must exist
        if (locationEntity == null) {
            throw new BusinessLogicException("The location must exist.");
        }

        // finds existing event
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // event must exist
        if (eventEntity == null) {
            throw new BusinessLogicException("The event must exist.");
        }

        // add the event to the location
        locationEntity.getEvents().add(eventEntity);

        // add the location to the event
        eventEntity.setLocation(locationEntity);

        LOGGER.log(Level.INFO, "End association between event and location with id = {0}", pEventId);
        return eventEntity;
    }

    /**
     * Gets a collection of event entities associated with a location
     *
     * @param pLocationId the location id
     * @return collection of event entities associated with a location
     */
    public List<EventEntity> getEvents(Long pLocationId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to location with id = {0}", pLocationId);

        // returns the list of all events
        return locationPersistence.find(pLocationId).getEvents();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pLocationId the location id
     * @param pEventId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long pLocationId, Long pEventId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding event with id = {0} from location with = " + pEventId, pLocationId);

        // gets all the events in a location
        List<EventEntity> events = locationPersistence.find(pLocationId).getEvents();

        // the busines that was found
        int index = events.indexOf(eventPersistence.find(pEventId));

        // logs end
        LOGGER.log(Level.INFO, "Finish event query with id = {0} from location with = " + pEventId, pLocationId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Event is not associated with the location");
        }

        return events.get(index);
    }

  /**
     * Replaces events associated with a resident
     *
     * @param pLocationId Id from resident
     * @param pEventsList Collection of event to associate with resident
     * @return A new collection associated to resident
     */
    public List<EventEntity> replaceEvents(Long pLocationId, List<EventEntity> pEventsList) {
       LOGGER.log(Level.INFO, "Trying to replace events related to location con id = {0}", pLocationId);
        LocationEntity location = locationPersistence.find(pLocationId);
        List<EventEntity> eventList = eventPersistence.findAll();
        for (EventEntity event : eventList) {
            if (pEventsList.contains(event)) {
                event.setLocation(location);
            } else if (event.getLocation()!= null && event.getLocation().equals(location)) {
                event.setLocation(null);
            }
        }
        LOGGER.log(Level.INFO, "Ended trying to replace events related to location con id = {0}", pLocationId);
        return pEventsList;
    }

    /**
     * Removes a event from a location.
     *
     * @param pLocationId Id from resident
     * @param pEventId Id from service
     */
    public void removeEvent(Long pLocationId, Long pEventId) {
        LOGGER.log(Level.INFO, "Start removing an event from location with id = {0}", pEventId);

        // desired location
        LocationEntity locationEntity = locationPersistence.find(pLocationId);

        // event to delete
        EventEntity eventEntity = eventPersistence.find(pEventId);

        // event to remove from location   
        locationEntity.getEvents().remove(eventEntity);
        
        // location to remove from event
        eventEntity.setLocation(null);

        LOGGER.log(Level.INFO, "Finished removing an event from location con id = {0}", pEventId);
    }
}
