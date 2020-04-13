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
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
// Attributes
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
     * Injects dependencies for resident profile persistence.
     */
    @Inject
    private ResidentProfilePersistence residentPersistence;

//===================================================
// Services
//===================================================
    /**
     * Creates a new event.
     *
     * @param pNeighborhoodId the id of the neighborhood where the event takes place
     * @param pResidentId the id of the host
     * @param pLocationId the id of the location in the neighborhood where the event takes place
     * @param pEventEntity the event entity to be persisted
     *
     * @return the persisted event entity
     * @throws BusinessLogicException if the event creation violates the business rules
     */
    public EventEntity createEvent(Long pNeighborhoodId, Long pResidentId,
            Long pLocationId, EventEntity pEventEntity)
            throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Start the event creation process");

        // check the event host
        ResidentProfileEntity host = residentPersistence.find(pResidentId, pNeighborhoodId);

        if (host == null) {
            throw new BusinessLogicException("There is no owner!");
        }

        // sets the event's host
        pEventEntity.setHost(host);

        // check the event location
        LocationEntity location = locationPersistence.find(pNeighborhoodId,pLocationId);

        if (location == null) {
            throw new BusinessLogicException("There is no location!");
        }

        // sets the event's location
        pEventEntity.setLocation(location);

        // verifies business rules
        verifyBusinessRules(pEventEntity);

        // creates the event
        EventEntity createdEvent = eventPersistence.create(pEventEntity);

        LOGGER.log(Level.INFO, "Termines the event creation process");
        return createdEvent;
    }

    /**
     * Returns all the events in the neighborhood
     *
     * @param pNeighborhoodId the id of the neighborhood where the event takes place
     *
     * @return list of events
     */
    public List<EventEntity> getEvents(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Begin consulting all events in neighborhood {0}", pNeighborhoodId);
        List<EventEntity> events = eventPersistence.findAll(pNeighborhoodId);
        LOGGER.log(Level.INFO, "End consulting all events in neighborhood {0}", pNeighborhoodId);
        return events;
    }

    /**
     * Finds an event by ID.
     *
     * @param pNeighborhoodId the id of the neighborhood where the event takes place
     * @param pEventId the id of the event to be found
     *
     * @return the found event, null if not found
     */
    public EventEntity getEvent(Long pNeighborhoodId, Long pEventId) {

        LOGGER.log(Level.INFO, "Begin search for event with Id = {0}", pEventId);

        EventEntity entity = eventPersistence.find(pNeighborhoodId,pEventId);

        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The event with Id = {0} doesn't exist", pEventId);
        }

        LOGGER.log(Level.INFO, "End search for event with Id = {0}", pEventId);
        return entity;
    }

    /**
     * Finds an event by title.
     *
     * @param pTitle the title of the event
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
     * Updates an event with given Id.
     *
     * @param pNeighborhoodId the id of the neighborhood where the event takes place
     * @param pEvent the event to update
     *
     * @return the event entity after the update
     * @throws BusinessLogicException if the new event violates the business rules
     */
    public EventEntity updateEvent(Long pNeighborhoodId, EventEntity pEvent) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for event with id = {0}", pEvent.getId());

        // update event
        EventEntity original = eventPersistence.find(pNeighborhoodId, pEvent.getId());

        // verifies that the new business meets the rules
        verifyBusinessRules(pEvent);

        // set the host
        pEvent.setHost(original.getHost());

        // set the location
        pEvent.setLocation(original.getLocation());

        // update the entity
        EventEntity newEntity = eventPersistence.update(pNeighborhoodId, pEvent);

        LOGGER.log(Level.INFO, "End the update process for event with id = {0}", pEvent.getId());

        return newEntity;
    }

    /**
     * Deletes an event by ID.
     *
     * @param pNeighborhoodId the id of the neighborhood where the event takes place
     * @param pEventId the id of the event to be deleted
     *
     */
    public void deleteEvent(Long pNeighborhoodId, Long pEventId) {
        LOGGER.log(Level.INFO, "Begin the deletion process for event with id = {0}", pEventId);

        // the event doesn't exist
        if (eventPersistence.find(pNeighborhoodId, pEventId) != null) {
            eventPersistence.delete(pNeighborhoodId, pEventId);
        }

        LOGGER.log(Level.INFO, "End the deletion process for event with id = {0}", pEventId);
    }

    /**
     * Verifies that the the event is valid.
     *
     * @param pEventEntity event to verify
     *
     * @return true if the business is valid. False otherwise
     * @throws BusinessLogicException if the event doesn't satisfy the business rules
     */
    private boolean verifyBusinessRules(EventEntity pEventEntity) throws BusinessLogicException {
        boolean valid = true;

        // 1. The event must have a post date
        if (pEventEntity.getDatePosted() == null) {
            throw new BusinessLogicException("The event must have a post date!");
        }

        // 2. The event must have a host date
        if (pEventEntity.getDateOfEvent() == null) {
            throw new BusinessLogicException("The event must have a host date!");
        }

        // 3. The title of the event cannot be null
        if (pEventEntity.getTitle() == null) {
            throw new BusinessLogicException("The event must have a title!");
        }

        //4. The event must have a start time
        if (pEventEntity.getStartTime() == null) {
            throw new BusinessLogicException("The event must have a start time!");
        }

        //5. The event must have an end time
        if (pEventEntity.getEndTime() == null) {
            throw new BusinessLogicException("The event must have an end time!");
        }

        //6. The event must have a description
        if (pEventEntity.getDescription() == null) {
            throw new BusinessLogicException("The event must have a description!");
        }

        String startTime = pEventEntity.getStartTime();
        String endTime = pEventEntity.getEndTime();

        // date format for the opening and closing times
        DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");

        // placeholder dates represening the times
        Date start = null;
        Date end = null;

        // 7. Start and end times must be legal hh:mm aa format
        try {
            start = timeFormat.parse(startTime);
            end = timeFormat.parse(endTime);

        } catch (ParseException e) {
            throw new BusinessLogicException("The event times have an illegal format!");
        }

        // 8. Start time must come before the end time
        if (start.compareTo(end) > 0) {
            throw new BusinessLogicException("The event opening time must come before the closing time!");
        }

        return valid;
    }
}
