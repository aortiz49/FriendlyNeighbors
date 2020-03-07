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
     * Associates a event with a resident
     *
     * @param residentId ID from resident entity
     * @param eventId ID from event entity
     * @return associated event entity
     */
    public EventEntity associateEventToResident(Long eventId, Long residentId) {
       LOGGER.log(Level.INFO, "Trying to add event to resident with id = {0}", residentId);
        ResidentProfileEntity ResidentProfileEntity = residentPersistence.find(residentId);
        EventEntity EventEntity = eventPersistence.find(eventId);
        EventEntity.setHost(ResidentProfileEntity);
      LOGGER.log(Level.INFO, "Event is associated with resident with id = {0}", residentId);
        return EventEntity;
    }

    /**
    /**
     * Gets a collection of events entities associated with a resident 
     * @param residentId ID from resident entity
     * @return collection of event entities associated with a resident 
     */
    public List<EventEntity> getEvents(Long residentId) {
          LOGGER.log(Level.INFO, "Gets all events belonging to resident with id = {0}", residentId);
        return residentPersistence.find(residentId).getEvents();
    }

 /**
     * Gets a event entity associated with  a resident
     *
     * @param residentId Id from resident
     * @param eventId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated 
     */
    public EventEntity getEvent(Long residentId, Long eventId) throws BusinessLogicException {
              LOGGER.log(Level.INFO, "Finding event with id = {0} from resident with = " + residentId, eventId);
        List<EventEntity> events = residentPersistence.find(residentId).getEvents();
        EventEntity EventEntity = eventPersistence.find(eventId);
        int index = events.indexOf(EventEntity);
            LOGGER.log(Level.INFO, "Finish query about event with id = {0} from resident with = " + residentId, eventId);
        if (index >= 0) {
            return events.get(index);
        }
        throw new BusinessLogicException("Event is not associated with resident");
    }


  /**
     * Replaces events associated with a resident
     *
     * @param residentId Id from resident
     * @param events Collection of event to associate with resident
     * @return A new collection associated to resident
     */
    public List<EventEntity> replaceEvents(Long residentId, List<EventEntity> events) {
       LOGGER.log(Level.INFO, "Trying to replace events related to resident with id = {0}", residentId);
        ResidentProfileEntity resident = residentPersistence.find(residentId);
        List<EventEntity> eventList = eventPersistence.findAll();
        for (EventEntity event : eventList) {
            if (events.contains(event)) {
                event.setHost(resident);
            } else if (event.getHost()!= null && event.getHost().equals(resident)) {
                event.setHost(null);
            }
        }
        LOGGER.log(Level.INFO, "Ended trying to replace events related to resident with id = {0}", residentId);
        return events;
    }
    
   


   /**
     * Removes a event from a resident. Event is no longer in DB
     *

     * @param eventId Id from event     
     */
    public void removeEvent(Long residentID, Long eventId) throws BusinessLogicException {
         LOGGER.log(Level.INFO, "Trying to delete a event from resident with id = {0}", eventId);
      eventPersistence.delete(getEvent(residentID, eventId).getId());

       LOGGER.log(Level.INFO, "Finished removing a event from resident with id = {0}", eventId);
        }
}
