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
 * This class handles persistence for CommentEntity. The connection is
 * stablished by Entity Manager from javax.persistance to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class CommentPersistence {

    private static final Logger LOGGER = Logger.getLogger(CommentPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a resident within DB
     *
     * @param commentEntity comment object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public CommentEntity create(CommentEntity commentEntity) {
        LOGGER.log(Level.INFO, "Creating a new comment");

        em.persist(commentEntity);
        LOGGER.log(Level.INFO, "Comment created");
        return commentEntity;
    }

    /**
     * Returns all comments from DB.
     *
     * @return a list with all comments found in DB.
     */
    public List<CommentEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all comments");

        TypedQuery query = em.createQuery("select u from CommentEntity u", CommentEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a comment with the id given by argument
     *
     * @param commentId: id from comment to be found.
     * @return a comment.
     */
    public CommentEntity find(Long commentId) {
        LOGGER.log(Level.INFO, "Querying for comment with id={0}", commentId);

        return em.find(CommentEntity.class, commentId);
    }

    /**
     * Updates a comment with the modified comment given by argument.
     *
     * @param commentEntity: the modified comment. Por
     * @return the updated comment
     */
    public CommentEntity update(CommentEntity commentEntity) {
        LOGGER.log(Level.INFO, "Updating comment with id={0}", commentEntity.getId());
        return em.merge(commentEntity);
    }

    /**
     * Deletes from DB a comment with the id given by argument
     *
     * @param commentId: id from comment to be deleted.
     */
    public void delete(Long commentId) {

        LOGGER.log(Level.INFO, "Deleting comment wit id={0}", commentId);
        CommentEntity commentEntity = em.find(CommentEntity.class, commentId);
        em.remove(commentEntity);
    }

}
