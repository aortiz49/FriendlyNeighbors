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
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Event. It connects via the Entity Manager in
 * javax.persistance with a SQL database.
 *
 * @author aortiz49
 */
@Stateless
public class EventPersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the event persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            EventPersistence.class.getName());

    /**
     * The entity manager that will access the Event table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a event in the database.
     *
     * @param pEventEntity event object to be created in the database
     * @return the created event with an id given by the database
     */
    public EventEntity create(EventEntity pEventEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new event");

        // makes the entity instance managed and persistent
        em.persist(pEventEntity);
        LOGGER.log(Level.INFO, "Event created");

        return pEventEntity;
    }

    /**
     * Returns all events in the database.
     *
     * @return a list containing every event in the database. select u from EventEntity u" is akin
     * to a "SELECT * from EventEntity" in SQL.
     */
    public List<EventEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all events");

        // Create a typed event entity query to find all neighborhoods 
        // in the database. 
        TypedQuery<EventEntity> query = em.createQuery(
                "select u from EventEntity u", EventEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a event with the id given by the parameter.
     *
     * @param pEventId the id corresponding to the event
     * @return the found event
     */
    public EventEntity find(Long pEventId) {
        LOGGER.log(Level.INFO, "Consulting event with id={0}",
                pEventId);

        return em.find(EventEntity.class, pEventId);
    }

    /**
     * Finds am event by title.
     *
     * @param pName the title of the event to search for
     * @return null if the event doesn't exist. If the event exists, return the first one
     */
    public EventEntity findByTitle(String pTitle) {
        LOGGER.log(Level.INFO, "Consulting event by title ", pTitle);

        // creates a query to search for events with the title given by the parameter. ":pTitle" is a placeholder that must be replaced
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
     * Updates an event.
     *
     * @param pEventEntity the event with the modifications. For example, the name could have
     * changed. In that case, we must use this update method.
     * @return the event with the updated changes
     */
    public EventEntity update(EventEntity pEventEntity) {
        LOGGER.log(Level.INFO, "Updating event with id = {0}",
                pEventEntity.getId());
        return em.merge(pEventEntity);
    }

    /**
     * Deletes an event.
     * <p>
     *
     * Deletes the event with the associated Id.
     *
     * @param pEventId the id of the event to be deleted
     */
    public void delete(Long pEventId) {
        LOGGER.log(Level.INFO, "Deleting event with id = {0}",
                pEventId);
        EventEntity reviewEntity = em.find(EventEntity.class,
                pEventId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO,
                "Exiting the deletion of event with id = {0}",
                pEventId);
    }

}
