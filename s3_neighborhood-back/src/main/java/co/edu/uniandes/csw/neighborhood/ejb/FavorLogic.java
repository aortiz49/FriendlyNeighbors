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
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;

/**
 *
 * @author * @author *
 */
@Stateless
public class FavorLogic {

    private static final Logger LOGGER = Logger.getLogger(FavorLogic.class.getName());

    @Inject
    private FavorPersistence persistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    public FavorEntity createFavor(FavorEntity favorEntity, Long residentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for favor has started");

        
        ResidentProfileEntity r = residentPersistence.find(residentId, neighId);
        
        //must have a title
        if (favorEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (favorEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a type
        if (favorEntity.getType() == null) {
            throw new BusinessLogicException("A type has to be specified");
        }

        //must have a date
        if (favorEntity.getDatePosted() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }
        
        favorEntity.setAuthor(r);

        persistence.create(favorEntity);

        LOGGER.log(Level.INFO, "Creation process for favor eneded");

        return favorEntity;
    }

    public void deleteFavor(Long id, Long neighID) {

        LOGGER.log(Level.INFO, "Starting deleting process for favor with id = {0}", id);
        persistence.delete(id, neighID);
        LOGGER.log(Level.INFO, "Ended deleting process for favor with id = {0}", id);
    }

    public List<FavorEntity> getFavors(Long neighID) {

        LOGGER.log(Level.INFO, "Starting querying process for all favors");
        List<FavorEntity> residents = persistence.findAll(neighID);
        LOGGER.log(Level.INFO, "Ended querying process for all favors");
        return residents;
    }

    public FavorEntity getFavor(Long id, Long neighID) {
        LOGGER.log(Level.INFO, "Starting querying process for favor with id ", id);
        FavorEntity resident = persistence.find(id, neighID);
        LOGGER.log(Level.INFO, "Ended querying process for  favor with id", id);
        return resident;
    }

    public FavorEntity updateFavor(FavorEntity favorEntity, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for favor with id ", favorEntity.getId());

        //must have a title
        if (favorEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (favorEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a type
        if (favorEntity.getType() == null) {
            throw new BusinessLogicException("A type has to be specified");
        }

        //must have a date
        if (favorEntity.getDatePosted() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }

        FavorEntity modified = persistence.update(favorEntity, neighID);
        LOGGER.log(Level.INFO, "Ended update process for favor with id ", favorEntity.getId());
        return modified;
    }

}
