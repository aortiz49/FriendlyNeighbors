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

import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence; 
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity; 
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException; 


/**
 *
 * @author  * @author 

 */
@Stateless
public class FavorLogic {

   
    private static final Logger LOGGER = Logger.getLogger(FavorLogic.class.getName());
    
    @Inject
    private FavorPersistence persistence;

    
      public FavorEntity createFavor(FavorEntity favorEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for favor has started");
        

        persistence.create(favorEntity);
        LOGGER.log(Level.INFO, "Creation process for favor eneded");
        
        return favorEntity;
    }
    
   

    public void deleteFavor(Long id) {
        
        LOGGER.log(Level.INFO, "Starting deleting process for favor with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for favor with id = {0}", id);
    }


    public List<FavorEntity> getFavors() {
        
        LOGGER.log(Level.INFO, "Starting querying process for all favors");
        List<FavorEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all favors");
        return residents;
    }

    public FavorEntity getFavor(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for favor with id ", id);
        FavorEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  favor with id", id);
        return resident;
    }

      

    public FavorEntity updateFavor(FavorEntity favorEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for favor with id ", favorEntity.getId());
        

        FavorEntity modified = persistence.update(favorEntity);
        LOGGER.log(Level.INFO, "Ended update process for favor with id ", favorEntity.getId());
        return modified;
    }

    


}
