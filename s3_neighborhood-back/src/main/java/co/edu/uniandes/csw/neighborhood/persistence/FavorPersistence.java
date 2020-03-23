package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for FavorEntity. The connection is set by
 * Entity Manager from javax.persistence to the SQL DB.
 *
 * @author v.cardonac1
 */
@Stateless
public class FavorPersistence {

    private static final Logger LOGGER = Logger.getLogger(FavorPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a favor within DB
     *
     * @param favorEntity favor object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public FavorEntity create(FavorEntity favorEntity) {
        LOGGER.log(Level.INFO, "Creating a new favor ");

        em.persist(favorEntity);
        LOGGER.log(Level.INFO, "Favor created");
        return favorEntity;
    }

    /**
     * Returns all favors from DB belonging to a neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @return a list with ll favors found in DB belonging to a neighborhood.
     */
    public List<FavorEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all favors from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From FavorEntity e where e.author.neighborhood.id = :neighID", FavorEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a favor with the id and neighborhood id given by argument
     *
     * @param favorId: id from favor to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a favor.
     */
    public FavorEntity find(Long favorId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for favor with id {0} belonging to neighborhood  {1}", new Object[]{favorId, neighborhood_id});

        FavorEntity e = em.find(FavorEntity.class, favorId);

        if (e != null) {
            if (e.getAuthor() == null || e.getAuthor().getNeighborhood() == null || e.getAuthor().getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Favor " + favorId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a favor with the modified favor given by argument belonging to a
     * neighborhood.
     *
     * @param favorEntity: the modified favor.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated favor
     */
    public FavorEntity update(FavorEntity favorEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating favor with id={0}", favorEntity.getId());

        find(favorEntity.getId(), neighborhood_id);

        return em.merge(favorEntity);
    }

    /**
     * Deletes from DB a favor with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param favorId: id from favor to be deleted.
     */
    public void delete(Long favorId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting favor with id={0}", favorId);
        FavorEntity e = find(favorId, neighborhood_id);

        em.remove(e);
    }

}
