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
 * @author albayona
 */
@Stateless
public class EventResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(EventResidentProfileLogic.class.getName());

    @Inject
    private EventPersistence eventPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;


    /**
     * /**
     * Gets a collection of events entities associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId ID from resident entity
     * @return collection of event entities associated with  resident
     */
    public List<EventEntity> getEvents(Long residentId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all events belonging to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return residentPersistence.find(residentId, neighId).getEvents();
    }

    /**
     * Gets a event entity associated with  resident
     *
     * @param residentId Id from resident
     * @param neighId ID from parent neighborhood
     * @param eventId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public EventEntity getEvent(Long residentId, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding event with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{eventId, residentId, neighId});
        List<EventEntity> events = residentPersistence.find(residentId, neighId).getEvents();
        EventEntity EventEntity = eventPersistence.find(eventId, neighId);
        int index = events.indexOf(EventEntity);
        LOGGER.log(Level.INFO, "Found event with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{eventId, residentId, neighId});
        if (index >= 0) {
            return events.get(index);
        }
        throw new BusinessLogicException("Event is not associated with resident");
    }

    /**
     * Replaces events associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId Id from resident
     * @param events Collection of event to associate with resident
     * @return A new collection associated to resident
     */
    public List<EventEntity> replaceEvents(Long residentId, List<EventEntity> events, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace events related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        ResidentProfileEntity resident = residentPersistence.find(residentId, neighId);
        List<EventEntity> eventList = eventPersistence.findAll(neighId);
        for (EventEntity event : eventList) {
            if (events.contains(event)) {
                event.setHost(resident);
            } else if (event.getHost() != null && event.getHost().equals(resident)) {
                event.setHost(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced events related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return events;
    }

    /**
     * Removes a event from resident. Event is no longer in DB
     *
     * @param residentID Id from resident
     * @param neighId ID from parent neighborhood
     * @param eventId Id from event
     */
    public void removeEvent(Long residentID, Long eventId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting event with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{eventId, residentID, neighId});

        eventPersistence.delete(getEvent(residentID, eventId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted event with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{eventId, residentID, neighId});
    }
}
