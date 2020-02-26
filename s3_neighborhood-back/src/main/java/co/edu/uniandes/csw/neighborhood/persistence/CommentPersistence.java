/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author v.cardonac1
 */
@Stateless
public class CommentPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(CommentPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;
    
    /**
     * Method to persist the entity in the database.
     *
     * @param commentEntity comment object to be created in the database.
     * @return returns the entity created with an id given by the database.
     */
    public CommentEntity create(CommentEntity commentEntity) {
        LOGGER.log(Level.INFO, "Creating a new comment");
        em.persist(commentEntity);
        LOGGER.log(Level.INFO, "Comment created");
        return commentEntity;
    }
    
    /**
     * Returns all comments from the database.
     *
     * @return a list with all the comments found in the database, "select u from CommentEntity u" is like a "select 
     * from CommentEntity;" - "SELECT * FROM table_name" en SQL.
     */
    public List<CommentEntity> findAll() {
        LOGGER.log(Level.INFO, "Consulting all comments");
        TypedQuery q = em.createQuery("select u from CommentEntity u", CommentEntity.class);
        return q.getResultList();
    }
    
    /**
     * Search if there is any comment with the id that is sent as an argument
     *
     * @param commentId: id corresponding to the comment sought
     * @return a comment.
     */
    public CommentEntity find(Long commentId) {
        LOGGER.log(Level.INFO, "Consulting comment with the id = {0}", commentId);
        return em.find(CommentEntity.class, commentId);
        
    }
    
    /**
     * update a comment.
     *
     * @param commentEntity: the comment that comes with the new changes.
     * @return a comment with the changes applied
     */
    public CommentEntity update(CommentEntity commentEntity) {
        LOGGER.log(Level.INFO, "Updating comment with the id = {0}", commentEntity.getId());
        return em.merge(commentEntity);
    }

    /**
     *
     * Delete a comment from the database receiving the id of the comment as an argument
     *
     * @param commentId: id corresponding to the comment to delete.
     */
    public void delete(Long commentId) {
        LOGGER.log(Level.INFO, "Deleting comment with id = {0}", commentId);
        CommentEntity entity = em.find(CommentEntity.class, commentId);
        em.remove(entity);
    }
}