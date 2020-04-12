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
 * Class that implements the connection for the relations between ResidentProfile and Event.
 *
 * @author aortiz49
 */
@Stateless
public class EventResidentProfileLogic {
//===================================================
// Attributes
//===================================================

    /**
     * Creates a logger for console output.
     */
    private static final Logger LOGGER = Logger.getLogger(EventResidentProfileLogic.class.getName());

    /**
     * Injects dependencies for event persistence.
     */
    @Inject
    private EventPersistence eventPersistence;

    /**
     * Injects dependencies for resident profile persistence.
     */
    @Inject
    private ResidentProfilePersistence residentProfilePersistence;

//===================================================
// CRUD Methods
//==================================================
    /**
     * Returns all events hosted by a resident.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pHostId ID from resident entity
     *
     * @return collection of event entities associated with resident
     */
    public List<EventEntity> getHostedEvents(Long pNeighborhoodId, Long pHostId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to resident with id {0} "
                + "from neighborhood {1}", new Object[]{pHostId, pNeighborhoodId});
        
        return residentProfilePersistence.find(pHostId, pNeighborhoodId).getEvents();
    }

    /**
     * Returns an event hosted by a resident.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pHostId the event host's
     * @param pEventId the hosted event's id
     *
     * @return the event entity
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getHostedEvent(Long pNeighborhoodId, Long pHostId, Long pEventId) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Finding event with id {0} associated to resident with id {1}, "
                + "from neighbothood {2}", new Object[]{pEventId, pHostId, pNeighborhoodId});

        // gets all the events hosted by a resident
        List<EventEntity> events = residentProfilePersistence.
                find(pHostId, pNeighborhoodId).getEvents();

        // the event that was found
        int index = events.indexOf(eventPersistence.find(pNeighborhoodId, pEventId));

        LOGGER.log(Level.INFO, "Finished finding event with id {0} associated to "
                + "resident with id {1}, from neighbothood {2}",
                new Object[]{pEventId, pHostId, pNeighborhoodId});

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Business is not associated with the residentProfile");
        }

        return events.get(index);
    }

    /**
     * Replaces events hosted by a resident
     *
     * @param pNeighborhoodId the id of neighborhood where the event took place
     * @param pHostId the id of the event host
     * @param pEventList the list of hosted events
     *
     * @return A new business collection associated to a resident
     */
    public List<EventEntity> replaceHostedEvents(Long pNeighborhoodId, Long pHostId,
            List<EventEntity> pEventList) {

        LOGGER.log(Level.INFO, "Replacing events hosted by resident with id {0} from "
                + "neighborhood {1}", new Object[]{pHostId, pNeighborhoodId});

        // the event host
        ResidentProfileEntity host = residentProfilePersistence.find(pHostId, pNeighborhoodId);

        // list of all events in the neighborhood
        List<EventEntity> eventList = eventPersistence.findAll(pNeighborhoodId);

        // for every event in the neighborhood..
        for (EventEntity event : eventList) {

            // if the the new list contains an event in the neighborhood, set the new host
            if (pEventList.contains(event)) {
                event.setHost(host);
            } // if the new list doesn't contain an event in the neighborhood, 
            // and the host is the desired host, set the host of that event to null
            else if (event.getHost() != null && event.getHost().equals(host)) {
                event.setHost(null);
            }
        }

        LOGGER.log(Level.INFO, "Replaced all events related to resident id {0} from "
                + "neighborhood {1}", new Object[]{pHostId, pNeighborhoodId});

        return pEventList;
    }

    /**
     * Removes an event hosted by a resident.
     *
     * @param pNeighborhoodId the id of neighborhood the event is in
     * @param pHostId the id of the event host
     * @param pEventId the id of the hosted event
     *
     * @throws BusinessLogicException if the event to be deleted doesn't exist
     */
    public void removeHostedEvent(Long pNeighborhoodId, Long pHostId, Long pEventId)
            throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Deleting event with id {0} hosted by resident with id {1}, "
                + "from neighborhood {2}", new Object[]{pEventId, pHostId, pNeighborhoodId});

        Long toDelete = getHostedEvent(pNeighborhoodId, pHostId, pEventId).getId();

        eventPersistence.delete(pNeighborhoodId, toDelete);

        LOGGER.log(Level.INFO, "Deleted event with id {0} hosted by resident with id {1}, "
                + "from neighborhood {2}", new Object[]{pEventId, pHostId, pNeighborhoodId});
    }
}
