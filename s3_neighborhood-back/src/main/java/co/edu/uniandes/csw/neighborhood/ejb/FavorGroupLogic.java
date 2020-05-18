/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
public class FavorGroupLogic {

    private static final Logger LOGGER = Logger.getLogger(FavorLogic.class.getName());

    @Inject
    private FavorPersistence favorPersistence;

    @Inject
    private GroupPersistence groupPersistence;

    /**
     * Associates favor with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @param favorId id from favor
     * @return associated favor
     */
    public FavorEntity associateFavorToGroup(Long groupId, Long favorId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between favor with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);

        groupEntity.getFavors().add(favorEntity);

        LOGGER.log(Level.INFO, "Association created between favor with id {0} and group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
        return favorPersistence.find(favorId, neighId);
    }

    /**
     * Gets a collection of favor entities associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @return collection of favor entities associated with group
     */
    public List<FavorEntity> getFavors(Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all favors belonging to group with id = {0} from neighborhood {1}", new Object[]{groupId, neighId});
        return groupPersistence.find(groupId, neighId).getFavors();
    }

    /**
     * Gets favor associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group
     * @param favorId id from associated entity
     * @return associated favor
     * @throws BusinessLogicException If favor is not associated
     */
    public FavorEntity getFavor(Long groupId, Long favorId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about favor with id {0} from group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
        List<FavorEntity> favors = groupPersistence.find(groupId, neighId).getFavors();
        FavorEntity favorFavors = favorPersistence.find(favorId, neighId);
        int index = favors.indexOf(favorFavors);
        LOGGER.log(Level.INFO, "Finish query about favor with id {0} from group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
        if (index >= 0) {
            return favors.get(index);
        }
        throw new BusinessLogicException("There is no association between favor and group");
    }



    /**
     * Unlinks favor from group
     *
     * @param neighId parent neighborhood
     * @param groupId Id from group
     * @param favorId Id from favor
     */
    public void removeFavor(Long groupId, Long favorId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between favor with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
        FavorEntity favorEntity = favorPersistence.find(favorId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        groupEntity.getFavors().remove(favorEntity);
        LOGGER.log(Level.INFO, "Association deleted between favor with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{favorId, groupId, neighId});
    }

}
