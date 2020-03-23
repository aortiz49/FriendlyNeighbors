
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
     * Associates favor with helper
     *
     * @param neighId parent neighborhood
     * @param helperId id from helper entity
     * @param favorId id from favor
     * @return associated favor
     */
    public FavorEntity associateFavorToHelper(Long helperId, Long favorId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between favor with id {0} and  helper with id {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId, neighId);
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);

        favorEntity.getCandidates().add(helperEntity);

        LOGGER.log(Level.INFO, "Association created between favor with id {0} and  helper with id  {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
        return favorPersistence.find(favorId, neighId);
    }

    /**
     * Gets a collection of favor entities associated with helper
     *
     * @param neighId parent neighborhood
     * @param helperId id from helper entity
     * @return collection of favor entities associated with helper
     */
    public List<FavorEntity> getFavors(Long helperId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all favors belonging to helper with id {0} from neighborhood {1}", new Object[]{helperId, neighId});
        return helperPersistence.find(helperId, neighId).getFavorsToHelp();
    }

    /**
     * Gets an favor associated with helper
     *
     * @param neighId parent neighborhood
     * @param helperId id from helper
     * @param favorId id from associated entity
     * @return associated favor
     * @throws BusinessLogicException If favor is not associated
     */
    public FavorEntity getFavor(Long helperId, Long favorId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about favor with id {0} from helper with {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
        List<FavorEntity> favors = helperPersistence.find(helperId, neighId).getFavorsToHelp();
        FavorEntity favorFavors = favorPersistence.find(favorId, neighId);
        int index = favors.indexOf(favorFavors);
        LOGGER.log(Level.INFO, "Finish query about favor with id {0} from helper with {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
        if (index >= 0) {
            return favors.get(index);
        }
        throw new BusinessLogicException("There is no association between helper and favor");
    }

    /**
     * Replaces favors associated with helper
     *
     * @param neighId parent neighborhood
     * @param helperId id from helper
     * @param favors collection of favor to associate with helper
     * @return A new collection associated to helper
     */
    public List<FavorEntity> replaceFavors(Long helperId, List<FavorEntity> favors, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace favors related to helper with id {0} from neighborhood {1}", new Object[]{helperId, neighId});
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId, neighId);
        List<FavorEntity> favorList = favorPersistence.findAll(neighId);
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
        LOGGER.log(Level.INFO, "Ended trying to replace favors related to helper with id {0} from neighborhood {1}", new Object[]{helperId, neighId});
        return helperEntity.getFavorsToHelp();
    }

    /**
     * Unlinks favor from helper
     *
     * @param neighId parent neighborhood
     * @param helperId Id from helper
     * @param favorId Id from favor
     */
    public void removeFavor(Long helperId, Long favorId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between favor with id {0} and  helper with id {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
        ResidentProfileEntity helperEntity = helperPersistence.find(helperId, neighId);
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);
        favorEntity.getCandidates().remove(helperEntity);
        LOGGER.log(Level.INFO, "Association deleted between favor with id {0} and  helper with id {1}, from neighbothood {2}", new Object[]{favorId, helperId, neighId});
    }
}
