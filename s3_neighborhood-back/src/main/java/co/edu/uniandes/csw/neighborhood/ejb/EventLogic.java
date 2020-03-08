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

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class that implements the connection to persistence class of Event.
 *
 * @author aortiz49
 */
@Stateless
public class EventLogic {
//===================================================
// Imports
//===================================================

    /**
     * Creates a logger for console output.
     */
    private static final Logger LOGGER = Logger.getLogger(EventLogic.class.getName());

    /**
     * Injects dependencies for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

    /**
     * Injects dependencies for location persistence.
     */
    @Inject
    private LocationPersistence locationPersistence;

    /**
     * Creates a new event.
     *
     * @param pEventEntity the event entity to be persisted.
     * @return the persisted event entity
     * @throws BusinessLogicException if the event creation violates the business rules
     */
    public EventEntity createEvent(EventEntity pEventEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Start the event creation process");

        // verify business rules
        verifyBusinessCreationRules(pEventEntity);

        // create the event
        eventPersistence.create(pEventEntity);
        LOGGER.log(Level.INFO, "Termines the event creation process");
        return pEventEntity;
    }

    /**
     * Returns all the events in the database.
     *
     * @return list of events
     */
    public List<EventEntity> getAllEvents() {
        LOGGER.log(Level.INFO, "Begin consulting all events");
        List<EventEntity> events = eventPersistence.findAll();
        LOGGER.log(Level.INFO, "End consulting all events");
        return events;
    }

    /**
     * Finds an event by ID.
     *
     * @return the found event, null if not found
     */
    public EventEntity getEvent(Long pId) {
        LOGGER.log(Level.INFO, "Begin search for event with Id = {0}", pId);
        EventEntity entity = eventPersistence.find(pId);
        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The event with Id = {0} doesn't exist", pId);
        }
        LOGGER.log(Level.INFO, "End search for event with Id = {0}", pId);
        return entity;
    }

    /**
     * Finds an event by title.
     *
     * @return the found event, null if not found
     */
    public EventEntity getEventByTitle(String pTitle) {
        LOGGER.log(Level.INFO, "Begin search for event with title = {0}", pTitle);
        EventEntity eventEntity = eventPersistence.findByTitle(pTitle);
        if (eventEntity == null) {
            LOGGER.log(Level.SEVERE, "The event with title = {0} doesn't exist", pTitle);
        }
        LOGGER.log(Level.INFO, "End search for event with title = {0}", pTitle);
        return eventEntity;
    }

    /**
     * Updates an event with a given Id.
     *
     * @param pEventId the Id of the event to update
     * @param pEvent the new event
     * @return the event entity after the update
     * @throws BusinessLogicException if the new event violates the business rules
     */
    public EventEntity updateEvent(Long pEventId, EventEntity pEvent) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for event with id = {0}", pEventId);

        // update event
        EventEntity newEntity = eventPersistence.update(pEvent);
        LOGGER.log(Level.INFO, "End the update process for event with id = {0}", pEvent.getId());
        return newEntity;
    }

    /**
     * Deletes an event by ID.
     *
     * @param eventId the ID of the event to be deleted
     *
     */
    public void deleteevent(Long eventId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the delettion process for event with id = {0}", eventId);
        eventPersistence.delete(eventId);
        LOGGER.log(Level.INFO, "End the delettion process for event with id = {0}", eventId);
    }

    /**
     * Verifies that the the event is valid.
     *
     * @param pEventEntity event to verify
     * @return true if the business is valid. False otherwise
     * @throws BusinessLogicException if the event doesn't satisfy the business rules
     */
    private boolean verifyBusinessCreationRules(EventEntity pEventEntity) throws BusinessLogicException {
        boolean valid = true;

        // the neighborhood the potential business belongs to 
        LocationEntity eventLocation = pEventEntity.getLocation();

        // 1. The event must have a location
        if (eventLocation == null) {
            throw new BusinessLogicException("The event must have a location.");
        } // 2. The location to which the event will be added to must already exist
        else if (eventPersistence.find(eventLocation.getId()) == null) {
            throw new BusinessLogicException("The event's location doesn't exist.");
        } // 3. The title of the event cannot be null
        else if (pEventEntity.getTitle() == null) {
            throw new BusinessLogicException("The event title cannot be null.");
        }

        return valid;
    }
}
