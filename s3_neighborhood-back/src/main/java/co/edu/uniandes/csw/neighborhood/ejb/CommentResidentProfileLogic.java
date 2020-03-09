/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.CommentPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between resident and
 * comment.
 *
 * @author aortiz49
 */
@Stateless
public class CommentResidentProfileLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(ResidentProfileNeighborhoodLogic.class.getName());

    /**
     * Dependency injection for resident persistence.
     */
    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Dependency injection for comment persistence.
     */
    @Inject
    private CommentPersistence commentPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Comment to a ResidentProfile.
     *
     * @param pCommentId the comment id
     * @param pResidentProfileId the resident id
     * @return the comment instance that was associated to the resident
     * @throws BusinessLogicException when the neighborhood or resident don't
     * exist
     */
    public CommentEntity addCommentToResidentProfile(Long pCommentId, Long pResidentProfileId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between comment and resident with id = {0}", pResidentProfileId);

        // finds existing resident
        ResidentProfileEntity residentEntity = residentPersistence.find(pResidentProfileId);

        // resident must exist
        if (residentEntity == null) {
            throw new BusinessLogicException("The neighborhood must exist.");
        }

        // finds existing comment
        CommentEntity commentEntity = commentPersistence.find(pCommentId);

        // comment must exist
        if (commentEntity == null) {
            throw new BusinessLogicException("The comment must exist.");
        }

        // set author of the comment
        commentEntity.setAuthor(residentEntity);

        // add the comment to the author
        residentEntity.getComments().add(commentEntity);

        LOGGER.log(Level.INFO, "End association between comment and resident with id = {0}", pResidentProfileId);
        return commentEntity;
    }

    /**
     * Gets a collection of resident entities associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @return collection of resident entities associated with a neighborhood
     */
    public List<ResidentProfileEntity> getResidentProfilees(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Gets all residentes belonging to neighborhood with id = {0}", pNeighborhoodId);

        // returns the list of all residentes
        return neighborhoodPersistence.find(pNeighborhoodId).getResidents();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pResidentProfileId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public ResidentProfileEntity getResidentProfile(Long pNeighborhoodId, Long pResidentProfileId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding resident with id = {0} from neighborhood with = " + pResidentProfileId, pNeighborhoodId);

        // gets all the residentes in a neighborhood
        List<ResidentProfileEntity> residentes = neighborhoodPersistence.find(pNeighborhoodId).getResidents();

        // the busines that was found
        int index = residentes.indexOf(residentPersistence.find(pResidentProfileId));

        // logs end
        LOGGER.log(Level.INFO, "Finish resident query with id = {0} from neighborhood with = " + pResidentProfileId, pNeighborhoodId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("ResidentProfile is not associated with the neighborhood");
        }

        return residentes.get(index);
    }

    /**
     * Replaces residents associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pNewResidentProfileesList Collection of service to associate with
     * resident
     * @return A new collection associated to resident
     */
    public List<ResidentProfileEntity> replaceResidentProfilees(Long pNeighborhoodId, List<ResidentProfileEntity> pNewResidentProfilesList) {

        //logs start 
        LOGGER.log(Level.INFO, "Start replacing residents associated to neighborhood with id = {0}", pNeighborhoodId);

        // finds the neighborhood
        NeighborhoodEntity neighborhood = neighborhoodPersistence.find(pNeighborhoodId);

        // finds all the residentes
        List<ResidentProfileEntity> currentResidentProfileesList = residentPersistence.findAll();

        // for all residentes in the database, check if a resident in the new list already exists. 
        // if this resident exists, change the neighborhood it is associated with
        for (int i = 0; i < currentResidentProfileesList.size(); i++) {
            ResidentProfileEntity current = currentResidentProfileesList.get(i);
            if (pNewResidentProfilesList.contains(current)) {
                current.setNeighborhood(neighborhood);
            } // if the current resident in the list has the desired neighborhood as its neighborhood,
            // set this neighborhood to null since it is not in the list of residentes we want the 
            // neighborhood to have
            else if (current.getNeighborhood().equals(neighborhood)) {
                current.setNeighborhood(null);
            }
        }

        // logs end
        LOGGER.log(Level.INFO, "End replacing residentes related to neighborhood with id = {0}", pNeighborhoodId);

        return pNewResidentProfilesList;
    }

    /**
     * Removes a resident from a neighborhood.
     *
     * @param pNeighborhoodId Id from resident
     * @param pResidentProfileId Id from service
     */
    public void removeResidentProfile(Long pNeighborhoodId, Long pResidentProfileId) {
        LOGGER.log(Level.INFO, "Start removing a resident from neighborhood with id = {0}", pResidentProfileId);

        // removes the resident from the database
        residentPersistence.delete(pResidentProfileId);

        LOGGER.log(Level.INFO, "Finished removing a resident from neighborhood con id = {0}", pResidentProfileId);
    }
}
