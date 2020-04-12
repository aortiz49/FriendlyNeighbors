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
package co.edu.uniandes.csw.neighborhood.persistence;
//===================================================
// Attributes
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for EventEntity. The connection is set by Entity Manager from the
 * persistence to the SQL DB.
 *
 * @author aortiz49
 */
@Stateless
public class EventPersistence {
//===================================================
// Attributes
//===================================================

    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(EventPersistence.class.getName());

    /**
     * The entity manager that will access the business table.
     */
    @PersistenceContext(unitName = "NeighborhoodPU")
    protected EntityManager em;

//===================================================
// CRUD MEthods
//===================================================
    /**
     * Creates a event within DB
     *
     * @param eventEntity event object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public EventEntity create(EventEntity eventEntity) {

        LOGGER.log(Level.INFO, "Creating a new event ");

        // makes the entity instance managed and persistent
        em.persist(eventEntity);

        LOGGER.log(Level.INFO, "Event created");
        return eventEntity;
    }

    /**
     * Returns all events from DB belonging to a neighborhood.
     *
     * @param pNeighborhoodId id from parent neighborhood
     *
     * @return a list with ll events found in DB belonging to a neighborhood.
     */
    public List<EventEntity> findAll(Long pNeighborhoodId) {

        LOGGER.log(Level.INFO, "Querying for all events from neighborhood ", pNeighborhoodId);

        TypedQuery query = em.createQuery("Select e From EventEntity e where "
                + "e.host.neighborhood.id = :pNeighborhoodId", EventEntity.class);

        query = query.setParameter("pNeighborhoodId", pNeighborhoodId);

        return query.getResultList();
    }

    /**
     * Looks for a event with the id and neighborhood id given by argument
     *
     * @param pNeighborhoodId id from parent neighborhood
     * @param pEventId id from event to be found
     *
     * @return the found event
     */
    public EventEntity find(Long pNeighborhoodId, Long pEventId) {
        LOGGER.log(Level.INFO, "Querying for event with id {0} belonging to neighborhood  {1}",
                new Object[]{pEventId, pNeighborhoodId});

        EventEntity event = em.find(EventEntity.class, pEventId);

        if (event != null) {
            if (event.getHost() == null || event.getHost().getNeighborhood() == null
                    || !event.getHost().getNeighborhood().getId().equals(pNeighborhoodId)) {
                throw new RuntimeException("Event " + pEventId + " does not belong to neighborhood " + pNeighborhoodId);
            }
        }

        return event;
    }

    /**
     * Finds am event by title.
     *
     * @param pTitle the title of the event to search for
     * 
     * @return null if the event doesn't exist. If the event exists, return the first one
     */
    public EventEntity findByTitle(String pTitle) {
        LOGGER.log(Level.INFO, "Consulting event by title ", pTitle);

        // creates a query to search for events with the title given by the parameter. 
        // ":pTitle" is a placeholder that must be replaced
        TypedQuery query = em.createQuery("Select e From EventEntity e where e.title = :pTitle", EventEntity.class);

        // the "pTitle" placeholder is replaced with the name of the parameter
        query = query.setParameter("pTitle", pTitle);

        // invokes the query and returns a list of results
        List<EventEntity> sameTitle = query.getResultList();
        EventEntity result;

        if (sameTitle == null || sameTitle.isEmpty()) {
            result = null;
        } else {
            result = sameTitle.get(0);
        }
        LOGGER.log(Level.INFO, "Exiting consultation of event by title ", pTitle);
        return result;
    }

    /**
     * Updates a event with the modified event given by argument belonging to a neighborhood.
     *
     * @param pNeighborhoodId id from parent neighborhood
     * @param pEventEntity the modified event.
     *
     * @return the updated event
     */
    public EventEntity update(Long pNeighborhoodId, EventEntity pEventEntity) {
        LOGGER.log(Level.INFO, "Updating event with id={0}", pEventEntity.getId());

        find(pNeighborhoodId, pEventEntity.getId());

        return em.merge(pEventEntity);
    }

    /**
     * Deletes from DB a event with the id given by argument belonging to a neighborhood.
     *
     * @param pNeighborhoodId id from parent neighborhood
     * @param pEventId id from event to be found
     */
    public void delete(Long pNeighborhoodId, Long pEventId) {

        LOGGER.log(Level.INFO, "Deleting event with id={0}", pEventId);
        EventEntity e = find(pNeighborhoodId,pEventId);

        em.remove(e);
    }

}
