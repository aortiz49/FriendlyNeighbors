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
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

/**
 *
 * @author albayona
 */
@Stateless
public class PostLogic {

    private static final Logger LOGGER = Logger.getLogger(PostLogic.class.getName());

    @Inject
    private PostPersistence persistence;

    /**
     * Creates a post
     *
     * @param postEntity post entity to be created
     * @return crested entity
     * @throws BusinessLogicException if business rules are not met
     */
    public PostEntity createPost(PostEntity postEntity) throws BusinessLogicException {
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
            throw new BusinessLogicException("A description has to be specified");
        }

        PostEntity entity = persistence.create(postEntity);
        LOGGER.log(Level.INFO, "Creation process for post eneded");

        return persistence.find(entity.getId());
    }

    /**
     * Deletes a post by ID
     *
     * @param id of post to be deleted
     */
    public void deletePost(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for post with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for post with id = {0}", id);
    }

    /**
     * Get all post entities
     *
     * @return all of post entities
     */
    public List<PostEntity> getPosts() {

        LOGGER.log(Level.INFO, "Starting querying process for all posts");
        List<PostEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all posts");
        return residents;
    }

    /**
     * Gets a post by id
     *
     * @param id from entity post
     * @return entity post found
     */
    public PostEntity getPost(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for post with id ", id);
        PostEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  post with id", id);
        return resident;
    }

    /**
     * Updates a post
     *
     * @param postEntity to be updated
     * @return the entity with the updated post
     */
    public PostEntity updatePost(PostEntity postEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for post with id ", postEntity.getId());

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
            throw new BusinessLogicException("A description has to be specified");
        }

        PostEntity modified = persistence.update(postEntity);
        LOGGER.log(Level.INFO, "Ended update process for post with id ", postEntity.getId());
        return modified;
    }

}
