/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
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
public class HelperFavorLogic {

    private static final Logger LOGGER = Logger.getLogger(HelperFavorLogic.class.getName());

    @Inject
    private FavorPersistence favorPersistence;

    @Inject
    private ResidentProfilePersistence helperPersistence;

    /**
     * Associates an favor with a helper
     *
     * @param helperId ID from helper entity
     * @param favorId ID from favor entity
     * @return associated favor entity
     */
    public FavorEntity associateFavorToAttenddee(Long helperId, Long favorId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to associate favor with helper with id = {0}", helperId);
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId);
        FavorEntity favorEntity = favorPersistence.find(favorId);

        if (helperEntity.getNeighborhood().getId() != favorEntity.getAuthor().getNeighborhood().getId()) {
            throw new BusinessLogicException("Favor and attendee must belong to the same neighborhood");
        }

        favorEntity.getCandidates().add(helperEntity);

        LOGGER.log(Level.INFO, "Favor is associated with helper with id = {0}", helperId);
        return favorPersistence.find(favorId);
    }

    /**
     * Gets a collection of favor entities associated with a helper
     *
     * @param helperId ID from helper entity
     * @return collection of favor entities associated with a helper
     */
    public List<FavorEntity> getFavors(Long helperId) {
        LOGGER.log(Level.INFO, "Gets all favors belonging to helper with id = {0}", helperId);
        return helperPersistence.find(helperId).getFavorsToHelp();
    }

    /**
     * Gets an favor entity associated with a a helper
     *
     * @param helperId Id from helper
     * @param favorId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If favor is not associated
     */
    public FavorEntity getFavor(Long helperId, Long favorId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding favor with id = {0} from helper with = " + helperId, favorId);
        List<FavorEntity> favors = helperPersistence.find(helperId).getFavorsToHelp();
        FavorEntity favorFavors = favorPersistence.find(favorId);
        int index = favors.indexOf(favorFavors);
        LOGGER.log(Level.INFO, "Finish query about favor with id = {0} from helper with = " + helperId, favorId);
        if (index >= 0) {
            return favors.get(index);
        }
        throw new BusinessLogicException("There is no association between helper and favor");
    }

    /**
     * Replaces favors associated with a helper
     *
     * @param helperId Id from helper
     * @param favors Collection of favor to associate with helper
     * @return A new collection associated to helper
     */
    public List<FavorEntity> replaceFavors(Long helperId, List<FavorEntity> favors) {
        LOGGER.log(Level.INFO, "Trying to replace favors related to helper with id = {0}", helperId);
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId);
        List<FavorEntity> favorList = favorPersistence.findAll();
        for (FavorEntity favor : favorList) {
            if (favors.contains(favor)) {
                if (!favor.getCandidates().contains(helperEntity)) {
                    favor.getCandidates().add(helperEntity);
                }
            } else {
                favor.getCandidates().remove(helperEntity);
            }
        }
        helperEntity.setFavorsToHelp(favors);
        LOGGER.log(Level.INFO, "Ended trying to replace favors related to helper with id = {0}", helperId);
        return helperEntity.getFavorsToHelp();
    }

    /**
     * Unlinks an favor from a helper
     *
     * @param helperId Id from helper
     * @param favorId Id from favor
     */
    public void removeFavor(Long helperId, Long favorId) {
        LOGGER.log(Level.INFO, "Trying to delete an favor from helper with id = {0}", helperId);
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId);
        FavorEntity favorEntity = favorPersistence.find(favorId);
        favorEntity.getCandidates().remove(helperEntity);
        LOGGER.log(Level.INFO, "Finished removing an favor from helper with id = {0}", helperId);
    }
}
