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

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between neighborhood and group.
 *
 * @author aortiz49
 */
@Stateless
public class GroupNeighborhoodLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(GroupNeighborhoodLogic.class.getName());

    /**
     * Dependency injection for neighborhood persistence.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    /**
     * Dependency injection for group persistence.
     */
    @Inject
    private GroupPersistence groupPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Group to a Neighborhood.
     *
     * @param pGroupId group id
     * @param pNeighborhoodId neighborhood id
     * @return the group instance that was associated to the neighborhood
     * @throws BusinessLogicException when the neighborhood or group don't exist
     */
    public GroupEntity addGroupToNeighborhood(Long pGroupId, Long pNeighborhoodId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between group and neighborhood with id = {0}", pGroupId);

        // finds existing neighborhood
        NeighborhoodEntity neighborhoodEntity = neighborhoodPersistence.find(pNeighborhoodId);

        // neighborhood must exist
        if (neighborhoodEntity == null) {
            throw new BusinessLogicException("The neighborhood must exist.");
        }

        // finds existing group
        GroupEntity groupEntity = groupPersistence.find(pGroupId);

        // group must exist
        if (groupEntity == null) {
            throw new BusinessLogicException("The group must exist.");
        }

        // set neighborhood of thr group
        groupEntity.setNeighborhood(neighborhoodEntity);

        // add the group to the neighborhood
        neighborhoodEntity.getGroups().add(groupEntity);

        LOGGER.log(Level.INFO, "End association between group and neighborhood with id = {0}", pGroupId);
        return groupEntity;
    }

    /**
     * Gets a collection of group entities associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @return collection of group entities associated with a neighborhood
     */
    public List<GroupEntity> getGroups(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Gets all groupes belonging to neighborhood with id = {0}", pNeighborhoodId);

        // returns the list of all groupes
        return neighborhoodPersistence.find(pNeighborhoodId).getGroups();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pGroupId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public GroupEntity getGroup(Long pNeighborhoodId, Long pGroupId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding group with id = {0} from neighborhood with = " + pGroupId, pNeighborhoodId);

        // gets all the groupes in a neighborhood
        List<GroupEntity> groupes = neighborhoodPersistence.find(pNeighborhoodId).getGroups();

        // the busines that was found
        int index = groupes.indexOf(groupPersistence.find(pGroupId));

        // logs end
        LOGGER.log(Level.INFO, "Finish group query with id = {0} from neighborhood with = " + pGroupId, pNeighborhoodId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Group is not associated with the neighborhood");
        }

        return groupes.get(index);
    }

    /**
     * Replaces groups associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pNewGroupesList Collection of service to associate with resident
     * @return A new collection associated to resident
     */
    public List<GroupEntity> replaceGroupes(Long pNeighborhoodId, List<GroupEntity> pNewGroupesList) {

        //logs start 
        LOGGER.log(Level.INFO, "Start replacing groupes related to neighborhood with id = {0}", pNeighborhoodId);

        // finds the neighborhood
        NeighborhoodEntity neighborhood = neighborhoodPersistence.find(pNeighborhoodId);

        // finds all the groupes
        List<GroupEntity> currentGroupesList = groupPersistence.findAll();

        // for all groupes in the database, check if a group in the new list already exists. 
        // if this group exists, change the neighborhood it is associated with
        for (int i = 0; i < currentGroupesList.size(); i++) {
            GroupEntity current = currentGroupesList.get(i);
            if (pNewGroupesList.contains(current)) {
                current.setNeighborhood(neighborhood);
            } // if the current group in the list has the desired neighborhood as its neighborhood,
            // set this neighborhood to null since it is not in the list of groupes we want the 
            // neighborhood to have
            else if (current.getNeighborhood().equals(neighborhood)) {
                current.setNeighborhood(null);
            }
        }

        // logs end
        LOGGER.log(Level.INFO, "End replacing groupes related to neighborhood with id = {0}", pNeighborhoodId);

        return pNewGroupesList;
    }

    /**
     * Removes a group from a neighborhood.
     *
     * @param pNeighborhoodId Id from resident
     * @param pGroupId Id from service
     */
    public void removeGroup(Long pNeighborhoodId, Long pGroupId) {
        LOGGER.log(Level.INFO, "Start removing a group from neighborhood with id = {0}", pGroupId);

        // removes the group from the database
        groupPersistence.delete(pGroupId);

        LOGGER.log(Level.INFO, "Finished removing a group from neighborhood with id = {0}", pGroupId);
    }
}
