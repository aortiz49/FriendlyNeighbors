/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;

/**
 *
 * @author albayona
 */
@Stateless
public class GroupLogic {

    private static final Logger LOGGER = Logger.getLogger(GroupLogic.class.getName());

    @Inject
    private GroupPersistence persistence;
    
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    /**
     * Creates a group
     *
     * @param groupEntity group entity to be created
     * @return crested entity
     * @throws BusinessLogicException if business rules are not met
     */
    public GroupEntity createGroup(GroupEntity groupEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for group has started");

        //must have a name
        if (groupEntity.getName() == null) {
            throw new BusinessLogicException("A name has to be specified");
        }

        //must have a creation date
        if (groupEntity.getDateCreated() == null) {
            throw new BusinessLogicException("A creation date has to be specified");
        }

        //must have a description
        if (groupEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }
      
        NeighborhoodEntity neighborhood = groupEntity.getNeighborhood();

        // must have a neighborhood
        if (neighborhood == null) {
            throw new BusinessLogicException("The group must have a neighborhood!");
        }
        // must have an existing neighborhood
        if (neighborhoodPersistence.find(neighborhood.getId()) == null) {
            throw new BusinessLogicException("The group must have an existing  neighborhood!");
        }


         persistence.create(groupEntity);
        LOGGER.log(Level.INFO, "Creation process for group eneded");

        return groupEntity;
    }

    
    /**
     * Deletes a group by ID
     *
     * @param id of group to be deleted
     */
    public void deleteGroup(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for group with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for group with id = {0}", id);
    }

    /**
     * Get all group entities
     *
     * @return all of group entities
     */
    public List<GroupEntity> getGroups() {

        LOGGER.log(Level.INFO, "Starting querying process for all groups");
        List<GroupEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all groups");
        return residents;
    }

    /**
     * Gets a group by id
     *
     * @param id from entity group
     * @return entity group found
     */
    public GroupEntity getGroup(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for group with id ", id);
        GroupEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  group with id", id);
        return resident;
    }

    /**
     * Updates a group
     *
     * @param groupEntity to be updated
     * @return the entity with the updated group
     */
    public GroupEntity updateGroup(GroupEntity groupEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for group with id ", groupEntity.getId());

        //must have a name
        if (groupEntity.getName() == null) {
            throw new BusinessLogicException("A name has to be specified");
        }

        //must have a description
        if (groupEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a creation date
        if (groupEntity.getDateCreated() == null) {
            throw new BusinessLogicException("A creation date has to be specified");
        }
        
        NeighborhoodEntity neighborhood = groupEntity.getNeighborhood();

        // must have a neighborhood
        if (neighborhood == null) {
            throw new BusinessLogicException("The group must have a neighborhood!");
        }
        // must have an existing neighborhood
        if (neighborhoodPersistence.find(neighborhood.getId()) == null) {
            throw new BusinessLogicException("The group must have an existing  neighborhood!");
        }


        GroupEntity modified = persistence.update(groupEntity);
        LOGGER.log(Level.INFO, "Ended update process for group with id ", groupEntity.getId());
        return modified;
    }

}
