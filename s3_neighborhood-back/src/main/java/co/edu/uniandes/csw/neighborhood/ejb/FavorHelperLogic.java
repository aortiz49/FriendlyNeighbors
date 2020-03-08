/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author albayona
 */
@Stateless
public class FavorHelperLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfileLogic.class.getName());

    @Inject
    private ResidentProfilePersistence helperPersistence;

    @Inject
    private FavorPersistence favorPersistence;

    /**
     * Associates a helper with an favor
     *
     * @param favorId ID from favor entity
     * @param helperId ID from helper entity
     * @return associated helper entity
     */
    public ResidentProfileEntity associateResidentProfileToFavor(Long favorId, Long helperId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to add helper to favor with id = {0}", favorId);

        FavorEntity favorEntity = favorPersistence.find(favorId);

        ResidentProfileEntity helperEntity = helperPersistence.find(helperId);

        if (helperEntity.getNeighborhood().getId() != favorEntity.getAuthor().getNeighborhood().getId()) {
            throw new BusinessLogicException("Favor and helper must belong to the same neighborhood");
        }

        if (helperEntity.getId() == favorEntity.getAuthor().getId()) {
            throw new BusinessLogicException("Author cannot be a helper");
        }

        favorEntity.getCandidates().add(helperEntity);

        LOGGER.log(Level.INFO, "Resident is associated with favor with id = {0}", favorId);
        return helperPersistence.find(helperId);
    }

    /**
     * Gets a collection of helper entities associated with an favor
     *
     * @param favorId ID from favor entity
     * @return collection of helper entities associated with an favor
     */
    public List<ResidentProfileEntity> getResidentProfiles(Long favorId) {
        LOGGER.log(Level.INFO, "Gets all helpers belonging to favor with id = {0}", favorId);
        return favorPersistence.find(favorId).getCandidates();
    }

    /**
     * Gets an helper entity associated with an favor
     *
     * @param favorId Id from favor
     * @param helperId Id from associated entity
     * @return associated helper entity
     * @throws BusinessLogicException If helper is not associated
     */
    public ResidentProfileEntity getResidentProfile(Long favorId, Long helperId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding helper with id = {0} from favor with = " + favorId, helperId);
        List<ResidentProfileEntity> helpers = favorPersistence.find(favorId).getCandidates();
        ResidentProfileEntity helperResidentProfiles = helperPersistence.find(helperId);
        int index = helpers.indexOf(helperResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about helper with id = {0} from favor with = " + favorId, helperId);
        if (index >= 0) {
            return helpers.get(index);
        }
        throw new BusinessLogicException("There is no association between helper and favor");
    }

    /**
     * Replaces helpers associated with a favor
     *
     * @param favorId Id from favor
     * @param helpers Collection of helper to associate with favor
     * @return A new collection associated to favor
     */
    public List<ResidentProfileEntity> replaceResidentProfiles(Long favorId, List<ResidentProfileEntity> helpers) {
        LOGGER.log(Level.INFO, "Trying to replace helpers related to favor with id = {0}", favorId);
        FavorEntity favorEntity = favorPersistence.find(favorId);
        List<ResidentProfileEntity> helperList = helperPersistence.findAll();
        for (ResidentProfileEntity helper : helperList) {
            if (helpers.contains(helper)) {
                if (!helper.getFavorsToHelp().contains(favorEntity)) {
                    helper.getFavorsToHelp().add(favorEntity);
                }
            } else {
                helper.getFavorsToHelp().remove(favorEntity);
            }
        }
        favorEntity.setCandidates(helpers);
        LOGGER.log(Level.INFO, "Ended trying to replace helpers related to favor with id = {0}", favorId);
        return favorEntity.getCandidates();
    }

    /**
     * Unlinks a helper from a favor
     *
     * @param favorId Id from favor
     * @param helperId Id from helper
     */
    public void removeResidentProfile(Long favorId, Long helperId) {
        LOGGER.log(Level.INFO, "Trying to delete an helper from favor with id = {0}", favorId);
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId);
        FavorEntity favorEntity = favorPersistence.find(favorId);
        favorEntity.getCandidates().remove(helperEntity);
        LOGGER.log(Level.INFO, "Finished removing an helper from favor with id = {0}", favorId);
    }

}
