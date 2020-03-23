package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for EventEntity. The connection is set by
 * Entity Manager from javax.persistence to the SQL DB.
 *
 * @author aortiz49
 */
@Stateless
public class EventPersistence {

    private static final Logger LOGGER = Logger.getLogger(EventPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a event within DB
     *
     * @param eventEntity event object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public EventEntity create(EventEntity eventEntity) {
        LOGGER.log(Level.INFO, "Creating a new event ");

        em.persist(eventEntity);
        LOGGER.log(Level.INFO, "Event created");
        return eventEntity;
    }

    /**
     * Returns all events from DB belonging to a neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @return a list with ll events found in DB belonging to a neighborhood.
     */
    public List<EventEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all events from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From EventEntity e where e.host.neighborhood.id = :neighID", EventEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a event with the id and neighborhood id given by argument
     *
     * @param eventId: id from event to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a event.
     */
    public EventEntity find(Long eventId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for event with id {0} belonging to neighborhood  {1}", new Object[]{eventId, neighborhood_id});

        EventEntity e = em.find(EventEntity.class, eventId);
        if (e != null) {
            if (e.getHost() == null || e.getHost().getNeighborhood() == null || e.getHost().getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Event " + eventId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a event with the modified event given by argument belonging to a
     * neighborhood.
     *
     * @param eventEntity: the modified event.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated event
     */
    public EventEntity update(EventEntity eventEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating event with id={0}", eventEntity.getId());

        find(eventEntity.getId(), neighborhood_id);

        return em.merge(eventEntity);
    }

    /**
     * Deletes from DB a event with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param eventId: id from event to be deleted.
     */
    public void delete(Long eventId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting event with id={0}", eventId);
        EventEntity e = find(eventId, neighborhood_id);

        em.remove(e);
    }

    /**
     * Finds am event by title.
     *
     * @param pName the title of the event to search for
     * @return null if the event doesn't exist. If the event exists, return the
     * first one
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

}
