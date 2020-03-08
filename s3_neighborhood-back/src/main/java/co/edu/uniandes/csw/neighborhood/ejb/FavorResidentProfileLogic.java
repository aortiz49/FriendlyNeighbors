/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
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
 * @author albayona
 */
@Stateless
public class FavorResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(FavorResidentProfileLogic.class.getName());

    @Inject
    private FavorPersistence favorPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Associates a favor with a resident
     *
     * @param residentId ID from resident entity
     * @param favorId ID from favor entity
     * @return associated favor entity
     */
    public FavorEntity associateFavorToResident(Long favorId, Long residentId) {
        LOGGER.log(Level.INFO, "Trying to add favor to resident with id = {0}", residentId);
        ResidentProfileEntity ResidentProfileEntity = residentPersistence.find(residentId);
        
        FavorEntity FavorEntity = favorPersistence.find(favorId);
        
        FavorEntity.setAuthor(ResidentProfileEntity);
        
        LOGGER.log(Level.INFO, "Favor is associated with resident with id = {0}", residentId);
        return favorPersistence.find(favorId);
    }

    /**
     * /**
     * Gets a collection of favors entities associated with a resident
     *
     * @param residentId ID from resident entity
     * @return collection of favor entities associated with a resident
     */
    public List<FavorEntity> getFavors(Long residentId) {
        LOGGER.log(Level.INFO, "Gets all favors belonging to resident with id = {0}", residentId);
        return residentPersistence.find(residentId).getFavorsRequested();
    }

    /**
     * Gets a favor entity associated with a resident
     *
     * @param residentId Id from resident
     * @param favorId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If favor is not associated
     */
    public FavorEntity getFavor(Long residentId, Long favorId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding favor with id = {0} from resident with = " + residentId, favorId);
        List<FavorEntity> favors = residentPersistence.find(residentId).getFavorsRequested();
        FavorEntity FavorEntity = favorPersistence.find(favorId);
        int index = favors.indexOf(FavorEntity);
        LOGGER.log(Level.INFO, "Finish query about favor with id = {0} from resident with = " + residentId, favorId);
        if (index >= 0) {
            return favors.get(index);
        }
        throw new BusinessLogicException("Favor is not associated with resident");
    }

    /**
     * Replaces favors associated with a resident
     *
     * @param residentId Id from resident
     * @param favors Collection of favor to associate with resident
     * @return A new collection associated to resident
     */
    public List<FavorEntity> replaceFavors(Long residentId, List<FavorEntity> favors) {
        LOGGER.log(Level.INFO, "Trying to replace favors related to resident with id = {0}", residentId);
        ResidentProfileEntity resident = residentPersistence.find(residentId);
        List<FavorEntity> favorList = favorPersistence.findAll();
        for (FavorEntity favor : favorList) {
            if (favors.contains(favor)) {
                favor.setAuthor(resident);
            } else if (favor.getAuthor() != null && favor.getAuthor().equals(resident)) {
                favor.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Ended trying to replace favors related to resident with id = {0}", residentId);
        return favors;
    }

    /**
     * Removes a favor from a resident. Favor is no longer in DB
     *
     *
     * @param favorId Id from favor
     */
    public void removeFavor(Long residentID, Long favorId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to delete a favor from resident with id = {0}", favorId);
        favorPersistence.delete(getFavor(residentID, favorId).getId());

        LOGGER.log(Level.INFO, "Finished removing a favor from resident with id = {0}", favorId);
    }
}
