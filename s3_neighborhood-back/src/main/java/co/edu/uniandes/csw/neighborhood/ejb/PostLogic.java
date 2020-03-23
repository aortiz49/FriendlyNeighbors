/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;

/**
 *
 * @author albayona
 */
@Stateless
public class PostLogic {

    private static final Logger LOGGER = Logger.getLogger(PostLogic.class.getName());

    @Inject
    private PostPersistence persistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Creates a post
     *
     * @param postEntity post entity to be created
     * @param residentId author
     * @param neighId parent neighborhood
     * @return created entity
     * @throws BusinessLogicException if business rules are not met
     */
    public PostEntity createPost(PostEntity postEntity, Long residentId, Long neighId) throws BusinessLogicException {

        ResidentProfileEntity r = residentPersistence.find(residentId, neighId);

        LOGGER.log(Level.INFO, "Creation process for post has started");

        //must have a title
        if (postEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (postEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a date
        if (postEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A publish date has to be specified");
        }

        postEntity.setAuthor(r);

        persistence.create(postEntity);
        LOGGER.log(Level.INFO, "Creation process for post eneded");

        return postEntity;
    }

    /**
     * Deletes a post by ID from neighborhood
     *
     * @param neighID parent neighborhood
     * @param id of post to be deleted
     */
    public void deletePost(Long id, Long neighID) {

        LOGGER.log(Level.INFO, "Starting deleting process for post with id = {0}", id);
        persistence.delete(id, neighID);
        LOGGER.log(Level.INFO, "Ended deleting process for post with id = {0}", id);
    }

    /**
     * Get all post entities from neighborhood
     *
     * @param neighID parent neighborhood
     * @return all of post entities
     */
    public List<PostEntity> getPosts(Long neighID) {

        LOGGER.log(Level.INFO, "Starting querying process for all posts");
        List<PostEntity> residents = persistence.findAll(neighID);
        LOGGER.log(Level.INFO, "Ended querying process for all posts");
        return residents;
    }

    /**
     * Gets a post by id from neighborhood
     *
     * @param neighID parent neighborhood
     * @param id from entity post
     * @return entity post found
     */
    public PostEntity getPost(Long id, Long neighID) {
        LOGGER.log(Level.INFO, "Starting querying process for post with id {0}", id);
        PostEntity resident = persistence.find(id, neighID);
        LOGGER.log(Level.INFO, "Ended querying process for  post with id {0}", id);
        return resident;
    }

    /**
     * Updates a post from neighborhood
     *
     * @param neighID parent neighborhood
     * @param postEntity to be updated
     * @return the entity with the updated post
     */
    public PostEntity updatePost(PostEntity postEntity, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for post with id {0}", postEntity.getId());

        //must have a title
        if (postEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (postEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a date
        if (postEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A publish date has to be specified");
        }

        PostEntity original = persistence.find(postEntity.getId(), neighID);
        postEntity.setAuthor(original.getAuthor());

        PostEntity modified = persistence.update(postEntity, neighID);
        LOGGER.log(Level.INFO, "Ended update process for post with id {0}", postEntity.getId());
        return modified;
    }

}
