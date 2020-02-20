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
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException; 


/**
 *
 * @author albayona
 */
@Stateless
public class CommentLogic {

   
    private static final Logger LOGGER = Logger.getLogger(CommentLogic.class.getName());
    
    @Inject
    private CommentPersistence persistence;

    
      public CommentEntity createComment(CommentEntity commentEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for comment has started");
        
         //must have a text
        if(commentEntity.getText()== null){
            throw new BusinessLogicException("A text has to be specified");
        }
        
         //must have a date
        if(commentEntity.getDate()== null){
            throw new BusinessLogicException("A date has to be specified");
        }
       

        persistence.create(commentEntity);
        LOGGER.log(Level.INFO, "Creation process for comment eneded");
        
        return commentEntity;
    }
    
   

    public void deleteComment(Long id) {
        
        LOGGER.log(Level.INFO, "Starting deleting process for comment with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for comment with id = {0}", id);
    }


    public List<CommentEntity> getComments() {
        
        LOGGER.log(Level.INFO, "Starting querying process for all comments");
        List<CommentEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all comments");
        return residents;
    }

    public CommentEntity getComment(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for comment with id ", id);
        CommentEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  comment with id", id);
        return resident;
    }

    
    

    public CommentEntity updateComment(CommentEntity commentEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for comment with id ", commentEntity.getId());
        
        CommentEntity original  = persistence.find(commentEntity.getId());

         //must have a text
        if(commentEntity.getText()== null){
            throw new BusinessLogicException("A text has to be specified");
        }
        
         //must have a date
        if(commentEntity.getDate()== null){
            throw new BusinessLogicException("A date has to be specified");
        }

        CommentEntity modified = persistence.update(commentEntity);
        LOGGER.log(Level.INFO, "Ended update process for comment with id ", commentEntity.getId());
        return modified;
    }

    


}
