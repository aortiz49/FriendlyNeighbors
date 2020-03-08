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

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
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
