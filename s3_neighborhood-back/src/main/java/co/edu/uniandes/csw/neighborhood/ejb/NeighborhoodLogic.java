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

//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection with the businessPersistence for the Business entity.
 *
 * @author aortiz49
 */
@Stateless
public class NeighborhoodLogic {

//===================================================
// Attributes
//===================================================
    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(NeighborhoodEntity.class.getName());

    /**
     * The injected business persistence object.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists a new neighborhood
     *
     * @param pBusinessEntity the Neighborhood entity
     * @return the neighborhood entity after it is persisted
     * @throws BusinessLogicException if the new neighborhood violates the business rules
     */
    public NeighborhoodEntity createNeighborhood(NeighborhoodEntity pNeighborhoodEntity) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin neighborhood creation process");

        // verify business rules for creating a new business
        verifyCreationBusinessRules(pNeighborhoodEntity);

        // create the neighborhood
        NeighborhoodEntity createdEntity = neighborhoodPersistence.create(pNeighborhoodEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End neighborhood creation process");
        return createdEntity;
    }

    /**
     * Returns all the neighborhoods in the database.
     *
     * @return list of neighborhoods
     */
    public List<NeighborhoodEntity> getNeighborhoods() {
        LOGGER.log(Level.INFO, "Begin consulting all neighborhood");
        List<NeighborhoodEntity> neighborhoods = neighborhoodPersistence.findAll();
        LOGGER.log(Level.INFO, "End consulting all neighborhood");
        return neighborhoods;
    }

    /**
     * Finds a neighborhood by ID.
     *
     * @return the found neighborhood, null if not found
     */
    public NeighborhoodEntity getNeighborhood(Long pId) {
        LOGGER.log(Level.INFO, "Begin search for neighborhood with Id = {0}", pId);
        NeighborhoodEntity bookEntity = neighborhoodPersistence.find(pId);
        if (bookEntity == null) {
            LOGGER.log(Level.SEVERE, "The neighborhood with Id = {0} doesn't exist", pId);
        }
        LOGGER.log(Level.INFO, "End search for neighborhood with Id = {0}", pId);
        return bookEntity;
    }

    /**
     * Finds a neighborhood by name.
     *
     * @return the found neighborhood, null if not found
     */
    public NeighborhoodEntity getNeighborhoodByName(String pName) {
        LOGGER.log(Level.INFO, "Begin search for neighborhood with name = {0}", pName);
        NeighborhoodEntity bookEntity = neighborhoodPersistence.findByName(pName);
        if (bookEntity == null) {
            LOGGER.log(Level.SEVERE, "The neighborhood with name = {0} doesn't exist", pName);
        }
        LOGGER.log(Level.INFO, "End search for neighborhood with name = {0}", pName);
        return bookEntity;
    }

    /**
     * Update a neighborhood with a given name.
     *
     * @param pNeighborhoodId the Id of the neighborhood to update
     * @param pNeighborhood the new neighborhood
     * @return the neighborhood entity after the update
     * @throws BusinessLogicException if the new neighborhood violates the business rules
     */
    public NeighborhoodEntity updateNeighborhood(Long pNeighborhoodId, NeighborhoodEntity pNeighborhood) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for neighborhood with id = {0}", pNeighborhoodId);

        // update neighborhood
        NeighborhoodEntity newEntity = neighborhoodPersistence.update(pNeighborhood);
        LOGGER.log(Level.INFO, "End the update process for neighborhood with id = {0}", pNeighborhood.getName());
        return newEntity;
    }

    /**
     * Delete a neighborhood by ID.
     *
     * @param neighborhoodID the id of the neighborhood to delete.
     * @throws BusinessLogicException if the neighborhood has businesses, groups, locations, or
     * residents associated to it
     */
    public void deleteNeighborhood(Long neighborhoodID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the deletion process for neighborhood with id = {0}", neighborhoodID);

        List<ResidentProfileEntity> residents = neighborhoodPersistence.find(neighborhoodID).getResidents();
        List<BusinessEntity> businesses = neighborhoodPersistence.find(neighborhoodID).getBusinesses();
        List<LocationEntity> locations = neighborhoodPersistence.find(neighborhoodID).getPlaces();
        List<GroupEntity> groups = neighborhoodPersistence.find(neighborhoodID).getGroups();

        // if there are entities associated to the neighborhood, do not delete
        if (!residents.isEmpty() || !businesses.isEmpty()
                || !locations.isEmpty() | !groups.isEmpty()) {
            throw new BusinessLogicException(
                    "Cannot delete the neighborhood! It has entities associated to it!");
        }

        // if there are no entities associated to the neighborhood, safely delete
        neighborhoodPersistence.delete(neighborhoodID);

        LOGGER.log(Level.INFO, "End the update process for neighborhood with id = {0}", neighborhoodID);
    }

    /**
     * Verifies that the neighborhood is valid.
     *
     * @param pNeighborhoodEntity neighborhood to verify
     * @return true if the neighborhood is valid. False otherwise
     * @throws BusinessLogicException if the neighborhood doesn't satisfy the business rules
     */
    private boolean verifyCreationBusinessRules(NeighborhoodEntity pNeighborhoodEntity)
            throws BusinessLogicException {

        boolean valid = true;

        // 1. The neighborhood's name cannot be null
        if (pNeighborhoodEntity.getName() == null) {
            throw new BusinessLogicException("The neighborhood's name cannot be null!");
        }

        // 2. The neighborhood cannot already exist with the same name
        if (neighborhoodPersistence.findByName(pNeighborhoodEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood with this name already exists!");
        }

        // 3. The neighborhood cannot already exist
        if (neighborhoodPersistence.find(pNeighborhoodEntity.getId()) != null) {
            throw new BusinessLogicException("The neighborhood already exists!");
        }

        return valid;

    }

}
