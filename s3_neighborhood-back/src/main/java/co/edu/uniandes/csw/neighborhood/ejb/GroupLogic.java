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
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;

/**
 *
 * @author albayona
 */
@Stateless
public class GroupLogic {

    private static final Logger LOGGER = Logger.getLogger(GroupLogic.class.getName());

    @Inject
    private ResidentProfilePersistence residentPersistence;

    @Inject
    private GroupPersistence persistence;

    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    /**
     * Creates a group
     *
     * @param groupEntity group entity to be created
     * @param neighId parent neighborhood
     * @return crested entity
     * @throws BusinessLogicException if business rules are not met
     */
    public GroupEntity createGroup(GroupEntity groupEntity, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for group has started");

        NeighborhoodEntity persistedNeighborhood = neighborhoodPersistence.find(neighId);

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

        // must have a neighborhood
        if (persistedNeighborhood == null) {
            throw new BusinessLogicException("The group must have an existing neighborhood!");
        }

        groupEntity.setNeighborhood(persistedNeighborhood);

        persistence.create(groupEntity);
        LOGGER.log(Level.INFO, "Creation process for group eneded");

        return groupEntity;
    }

    /**
     * Deletes a group by ID
     *
     * @param id of group to be deleted
     * @param neighId parent neighborhood
     */
    public void deleteGroup(Long id, Long neighId) {

        LOGGER.log(Level.INFO, "Starting deleting process for group with id = {0}", id);
        persistence.delete(id, neighId);
        LOGGER.log(Level.INFO, "Ended deleting process for group with id = {0}", id);
    }

    /**
     * Get all group entities
     *
     * @param neighId parent neighborhood
     * @return all of group entities
     */
    public List<GroupEntity> getGroups(Long neighId) {

        LOGGER.log(Level.INFO, "Starting querying process for all groups");
        List<GroupEntity> residents = persistence.findAll(neighId);
        LOGGER.log(Level.INFO, "Ended querying process for all groups");
        return residents;
    }

    /**
     * Gets a group by id
     *
     * @param id from entity group
     * @param neighId parent neighborhood
     * @return entity group found
     */
    public GroupEntity getGroup(Long id, Long neighId) {
        LOGGER.log(Level.INFO, "Starting querying process for group with id {0}", id);
        GroupEntity resident = persistence.find(id, neighId);
        LOGGER.log(Level.INFO, "Ended querying process for  group with id {0}", id);
        return resident;
    }

    /**
     * Updates a group
     *
     * @param groupEntity to be updated
     * @param neighId parent neighborhood
     * @return the entity with the updated group
     */
    public GroupEntity updateGroup(GroupEntity groupEntity, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for group with id {0}", groupEntity.getId());

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

        NeighborhoodEntity neighborhood = neighborhoodPersistence.find(neighId);

        groupEntity.setNeighborhood(neighborhood);

        GroupEntity modified = persistence.update(groupEntity, neighId);
        LOGGER.log(Level.INFO, "Ended update process for group with id {0}", groupEntity.getId());
        return modified;
    }

    public GroupEntity associatePictureToGroup(Long groupId, Long neighId, String pic) throws BusinessLogicException {
        GroupEntity entity = persistence.find(groupId, neighId);

        entity.getAlbum().add(pic);
        return persistence.find(groupId, neighId);
    }

    public List<ResidentProfileEntity> getPotentialMembers(Long group, Long neighID) {

        List<ResidentProfileEntity> s1 = residentPersistence.findAll(neighID);
        List<ResidentProfileEntity> s2 = getGroup(group, neighID).getMembers();
        s1.removeAll(s2);

        return s1;

    }

}
