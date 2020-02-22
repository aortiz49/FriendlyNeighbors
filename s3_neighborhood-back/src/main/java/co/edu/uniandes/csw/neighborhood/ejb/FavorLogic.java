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

    
      public PostEntity createPost(PostEntity postEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for post has started");
        
         //must have a title
        if(postEntity.getTitle()== null){
            throw new BusinessLogicException("A title has to be specified");
        }
        
         //must have a description
        if(postEntity.getDescription()== null){
            throw new BusinessLogicException("A description has to be specified");
        }
       

        persistence.create(postEntity);
        LOGGER.log(Level.INFO, "Creation process for post eneded");
        
        return postEntity;
    }
    
   

    public void deletePost(Long id) {
        
        LOGGER.log(Level.INFO, "Starting deleting process for post with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for post with id = {0}", id);
    }


    public List<PostEntity> getPosts() {
        
        LOGGER.log(Level.INFO, "Starting querying process for all posts");
        List<PostEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all posts");
        return residents;
    }

    public PostEntity getPost(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for post with id ", id);
        PostEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  post with id", id);
        return resident;
    }

    
    

    public PostEntity updatePost(PostEntity postEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for post with id ", postEntity.getId());
        
        PostEntity original  = persistence.find(postEntity.getId());

         //must have a title
        if(postEntity.getTitle()== null){
            throw new BusinessLogicException("A title has to be specified");
        }
        
         //must have a description
        if(postEntity.getDescription()== null){
            throw new BusinessLogicException("A description has to be specified");
        }
       

        PostEntity modified = persistence.update(postEntity);
        LOGGER.log(Level.INFO, "Ended update process for post with id ", postEntity.getId());
        return modified;
    }

    


}
