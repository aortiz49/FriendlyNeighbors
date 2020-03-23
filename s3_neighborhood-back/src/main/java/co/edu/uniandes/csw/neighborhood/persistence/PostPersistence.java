package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for PostEntity. The connection is set by
 * Entity Manager from javax.persistance to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class PostPersistence {

    private static final Logger LOGGER = Logger.getLogger(PostPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a post within DB
     *
     * @param postEntity post object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public PostEntity create(PostEntity postEntity) {
        LOGGER.log(Level.INFO, "Creating a new post ");

        em.persist(postEntity);
        LOGGER.log(Level.INFO, "Post created");
        return postEntity;
    }

    /**
     * Returns all posts from DB belonging to a neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @return a list with ll posts found in DB belonging to a neighborhood.
     */
    public List<PostEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all posts from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From PostEntity e where e.author.neighborhood.id = :neighID", PostEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a post with the id and neighborhood id given by argument
     *
     * @param postId: id from post to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a post.
     */
    public PostEntity find(Long postId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for post with id {0} belonging to neighborhood  {1}", new Object[]{postId, neighborhood_id});

        PostEntity e = em.find(PostEntity.class, postId);
        if (e != null) {
            if (e.getAuthor() == null || e.getAuthor().getNeighborhood() == null || e.getAuthor().getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Post " + postId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a post with the modified post given by argument belonging to a
     * neighborhood.
     *
     * @param postEntity: the modified post.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated post
     */
    public PostEntity update(PostEntity postEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating post with id={0}", postEntity.getId());

        find(postEntity.getId(), neighborhood_id);

        return em.merge(postEntity);
    }

    /**
     * Deletes from DB a post with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param postId: id from post to be deleted.
     */
    public void delete(Long postId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting post with id={0}", postId);
        PostEntity e = find(postId, neighborhood_id);

        em.remove(e);
    }

}
