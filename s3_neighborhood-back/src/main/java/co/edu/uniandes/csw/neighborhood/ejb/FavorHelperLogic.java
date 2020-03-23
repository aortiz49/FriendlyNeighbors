/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
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
     * Associates helper with favor
     *
     * @param neighId parent neighborhood
     * @param favorId id from favor entity
     * @param helperId id from helper
     * @return associated helper
     */
    public ResidentProfileEntity associateHelperToFavor(Long favorId, Long helperId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between helper with id {0} and  favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId, neighId);

        favorEntity.getCandidates().add(helperEntity);

        LOGGER.log(Level.INFO, "Association created between helper with id {0} and favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
        return helperPersistence.find(helperId, neighId);
    }

    /**
     * Gets a collection of helper entities associated with favor
     *
     * @param neighId parent neighborhood
     * @param favorId id from favor entity
     * @return collection of helper entities associated with favor
     */
    public List<ResidentProfileEntity> getHelpers(Long favorId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all helpers belonging to favor with id = {0} from neighborhood {1}", new Object[]{favorId, neighId});
        return favorPersistence.find(favorId, neighId).getCandidates();
    }

    /**
     * Gets helper associated with favor
     *
     * @param neighId parent neighborhood
     * @param favorId id from favor
     * @param helperId id from associated entity
     * @return associated helper
     * @throws BusinessLogicException If helper is not associated
     */
    public ResidentProfileEntity getHelper(Long favorId, Long helperId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about helper with id {0} from favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
        List<ResidentProfileEntity> helpers = favorPersistence.find(favorId, neighId).getCandidates();
        ResidentProfileEntity helperResidentProfiles = helperPersistence.find(helperId, neighId);
        int index = helpers.indexOf(helperResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about helper with id {0} from favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
        if (index >= 0) {
            return helpers.get(index);
        }
        throw new BusinessLogicException("There is no association between helper and favor");
    }

    /**
     * Replaces helpers associated with favor
     *
     * @param neighId parent neighborhood
     * @param favorId id from favor
     * @param helpers collection of helper to associate with favor
     * @return A new collection associated to favor
     */
    public List<ResidentProfileEntity> replaceHelpers(Long favorId, List<ResidentProfileEntity> helpers, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace helpers related to favor with id {0} from neighborhood {1}", new Object[]{favorId, neighId});
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);
        List<ResidentProfileEntity> helperList = helperPersistence.findAll(neighId);
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
        LOGGER.log(Level.INFO, "Ended trying to replace helpers related to favor with id {0} from neighborhood {1}", new Object[]{favorId, neighId});
        return favorEntity.getCandidates();
    }

    /**
     * Unlinks helper from favor
     *
     * @param neighId parent neighborhood
     * @param favorId Id from favor
     * @param helperId Id from helper
     */
    public void removeHelper(Long favorId, Long helperId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between helper with id {0} and  favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId, neighId);
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);
        favorEntity.getCandidates().remove(helperEntity);
        LOGGER.log(Level.INFO, "Association deleted between helper with id {0} and  favor with id {1}, from neighbothood {2}", new Object[]{helperId, favorId, neighId});
    }

}
