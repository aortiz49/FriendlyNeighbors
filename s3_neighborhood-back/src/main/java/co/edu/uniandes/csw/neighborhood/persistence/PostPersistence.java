package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for PostEntity. The connection is stablished
 * by Entity Manager from javax.persistance to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class PostPersistence {

    private static final Logger LOGGER = Logger.getLogger(PostPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU" )
    protected EntityManager em;

    /**
     * Creates a post within DB
     *
     * @param postEntity post object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public PostEntity create(PostEntity postEntity) {
        LOGGER.log(Level.INFO, "Creating a new post");

        em.persist(postEntity);
        LOGGER.log(Level.INFO, "Post created");
        return postEntity;
    }

    
    /**
     * Returns all posts from DB.
     *
     * @return a list with all posts found in DB.
     */
    public List<PostEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all posts");
        
        TypedQuery query = em.createQuery("select u from PostEntity u", PostEntity.class);
       
        return query.getResultList();
    }
    
        /**
     * Looks for a post with the id given by argument
     *
     * @param postId: id from post to be found.
     * @return a post.
     */
    public PostEntity find(Long postId) {
        LOGGER.log(Level.INFO, "Querying for post with id={0}", postId);
       
        
        return em.find(PostEntity.class, postId);
    }


        /**
     * Updates a post with the modified post given by argument.
     *
     * @param postEntity: the modified post. 
     * @return the updated post
     */
    public PostEntity update(PostEntity postEntity) {
        LOGGER.log(Level.INFO, "Updating post with id={0}", postEntity.getId());
        return em.merge(postEntity);
    }
    
        /**
     * Deletes from DB a post with the id given by argument
     *
     * @param postId: id from post to be deleted.
     */
    public void delete(Long postId) {

        LOGGER.log(Level.INFO, "Deleting post wit id={0}", postId);
        PostEntity postEntity = em.find(PostEntity.class, postId);
        em.remove(postEntity);
    }
}
