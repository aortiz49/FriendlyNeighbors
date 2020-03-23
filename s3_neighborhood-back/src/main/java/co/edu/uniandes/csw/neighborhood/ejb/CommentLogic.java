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

import co.edu.uniandes.csw.neighborhood.persistence.CommentPersistence;
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;

/**
 *
 * @author albayona
 *
 */
@Stateless
public class CommentLogic {

    private static final Logger LOGGER = Logger.getLogger(CommentLogic.class.getName());

    @Inject
    private CommentPersistence persistence;

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Creates a comment
     *
     * @param commentEntity comment entity to be created
     * @param postId post of comment
     * @param authorId author of comment
     * @param neighId parent neighborhood
     * @return crested entity
     * @throws BusinessLogicException if business rules are not met
     */
    public CommentEntity createComment(CommentEntity commentEntity, Long postId, Long authorId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for comment has started");

        PostEntity p = postPersistence.find(postId, neighId);
        ResidentProfileEntity r = residentPersistence.find(authorId, neighId);

        //must have a text
        if (commentEntity.getText() == null) {
            throw new BusinessLogicException("A text has to be specified");
        }

        //must have a date
        if (commentEntity.getDate() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }

        commentEntity.setPost(p);
        commentEntity.setAuthor(r);

        persistence.create(commentEntity);
        LOGGER.log(Level.INFO, "Creation process for comment eneded");

        return commentEntity;
    }

    /**
     * Deletes a comment by ID
     *
     * @param id of comment to be deleted
     * @param neighId parent neighborhood
     */
    public void deleteComment(Long id, Long neighId) {

        LOGGER.log(Level.INFO, "Starting deleting process for comment with id = {0}", id);
        persistence.delete(id, neighId);
        LOGGER.log(Level.INFO, "Ended deleting process for comment with id = {0}", id);
    }

    /**
     * Get all comment entities
     *
     * @param neighId parent neighborhood
     * @return all of comment entities
     */
    public List<CommentEntity> getComments(Long neighId) {

        LOGGER.log(Level.INFO, "Starting querying process for all comments");
        List<CommentEntity> residents = persistence.findAll(neighId);
        LOGGER.log(Level.INFO, "Ended querying process for all comments");
        return residents;
    }

    /**
     * Gets a comment by id
     *
     * @param id from entity comment
     * @param neighId parent neighborhood
     * @return entity comment found
     */
    public CommentEntity getComment(Long id, Long neighId) {
        LOGGER.log(Level.INFO, "Starting querying process for comment with id ", id);
        CommentEntity resident = persistence.find(id, neighId);
        LOGGER.log(Level.INFO, "Ended querying process for  comment with id", id);
        return resident;
    }

    /**
     * Updates a comment
     *
     * @param commentEntity to be updated
     * @param neighId parent neighborhood
     * @return the entity with the updated comment
     * @throws
     * co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if
     * business rules are not met
     */
    public CommentEntity updateComment(CommentEntity commentEntity, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for comment with id ", commentEntity.getId());

        //must have a text
        if (commentEntity.getText() == null) {
            throw new BusinessLogicException("A text has to be specified");
        }

        //must have a date
        if (commentEntity.getDate() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }

        CommentEntity original = persistence.find(commentEntity.getId(), neighId);
        commentEntity.setAuthor(original.getAuthor());
        commentEntity.setPost(original.getPost());

        CommentEntity modified = persistence.update(commentEntity, neighId);
        LOGGER.log(Level.INFO, "Ended update process for comment with id ", commentEntity.getId());
        return modified;
    }

}
