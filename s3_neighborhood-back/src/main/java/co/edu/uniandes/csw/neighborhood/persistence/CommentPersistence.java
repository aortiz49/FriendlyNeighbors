package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for CommentEntity. The connection is set by
 * Entity Manager from javax.persistence to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class CommentPersistence {

    private static final Logger LOGGER = Logger.getLogger(CommentPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a comment within DB
     *
     * @param commentEntity comment object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public CommentEntity create(CommentEntity commentEntity) {
        LOGGER.log(Level.INFO, "Creating a new comment ");

        em.persist(commentEntity);
        LOGGER.log(Level.INFO, "Comment created");
        return commentEntity;
    }

    /**
     * Returns all comments from DB belonging to a neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @return a list with ll comments found in DB belonging to a neighborhood.
     */
    public List<CommentEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all comments from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From CommentEntity e where e.author.neighborhood.id = :neighID", CommentEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a comment with the id and neighborhood id given by argument
     *
     * @param commentId: id from comment to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a comment.
     */
    public CommentEntity find(Long commentId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for comment with id {0} belonging to neighborhood  {1}", new Object[]{commentId, neighborhood_id});

        CommentEntity e = em.find(CommentEntity.class, commentId);

        if (e != null) {
            if (e.getAuthor() == null || e.getAuthor().getNeighborhood() == null || e.getAuthor().getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Comment " + commentId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a comment with the modified comment given by argument belonging to a
     * neighborhood.
     *
     * @param commentEntity: the modified comment.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated comment
     */
    public CommentEntity update(CommentEntity commentEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating comment with id={0}", commentEntity.getId());

        find(commentEntity.getId(), neighborhood_id);

        return em.merge(commentEntity);
    }

    /**
     * Deletes from DB a comment with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param commentId: id from comment to be deleted.
     */
    public void delete(Long commentId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting comment with id={0}", commentId);
        CommentEntity e = find(commentId, neighborhood_id);

        em.remove(e);
    }

}
