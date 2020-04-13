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

import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection between LocationPersistence and LocationEntity.
 *
 * @author v.cardonac1, aortiz49
 * @version 2.0 (This version filters the location by neighborhood)
 */
@Stateless
public class LocationLogic {
//===================================================
// Attributes
//===================================================

    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessLogic.class.getName());

    /**
     * The injected location persistence dependencies.
     */
    @Inject
    private LocationPersistence locationPersistence;

    /**
     * The injected neighborhood persistence dependencies.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;
//===================================================
// CRUD Method
//===================================================

    /**
     * Creates and persists a new location.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the location
     * @param pLocationEntity the entity of type Location of the new location to be persisted
     *
     * @return the location entity after it is persisted
     * @throws BusinessLogicException if the new location violates the location rules
     */
    public LocationEntity createLocation(Long pNeighborhoodId,
            LocationEntity pLocationEntity) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin creating a location");

        // 1. No two locations can have the same name
        if (locationPersistence.findByName(pLocationEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood already has a location with that name!");
        }

        NeighborhoodEntity neigh = neighborhoodPersistence.find(pNeighborhoodId);

        if (neigh == null) {
            throw new BusinessLogicException("The neighborhood doesn't exist!");
        }

        // set the location's neighborhood 
        pLocationEntity.setNeighborhood(neigh);

        // verify location rules for creating a new location
        verifyBusinessRules(pLocationEntity);

        // create the location
        LocationEntity createdEntity = locationPersistence.create(pLocationEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End creating a location");
        return createdEntity;
    }

    /**
     * Returns all the locations in the database.
     *
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return list of locations
     */
    public List<LocationEntity> getLocations(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Begin consulting all locations");
        List<LocationEntity> locations = locationPersistence.findAll(pNeighborhoodId);
        LOGGER.log(Level.INFO, "End consulting all locations");
        return locations;
    }

    /**
     * Finds a location by ID.
     *
     * @param pNeighborhoodId the id from parent neighborhood.
     * @param pLocationId the id corresponding to the location
     *
     *
     * @return the found location, null if not found
     */
    public LocationEntity getLocation(Long pNeighborhoodId, Long pLocationId) {
        LOGGER.log(Level.INFO, "Begin search for location with Id = {0}", pLocationId);
        LocationEntity entity = locationPersistence.find(pNeighborhoodId, pLocationId);

        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The location with Id = {0} doesn't exist", pLocationId);
        }
        LOGGER.log(Level.INFO, "End search for bsiness with Id = {0}", pLocationId);
        return entity;
    }

    /**
     * Finds a location by name.
     *
     * @param pName the name of the location to find
     *
     * @return the found location, null if not found
     */
    public LocationEntity getLocationByName(String pName) {
        LOGGER.log(Level.INFO, "Begin search for location with name = {0}", pName);
        LocationEntity locationEntity = locationPersistence.findByName(pName);
        if (locationEntity == null) {
            LOGGER.log(Level.SEVERE, "The location with name = {0} doesn't exist", pName);
        }
        LOGGER.log(Level.INFO, "End search for location with name = {0}", pName);
        return locationEntity;
    }

    /**
     * Update a location with given Id.
     *
     * @param pNeighborhoodId the id from parent neighborhood.
     * @param pLocationEntity the new location
     *
     * @return the location entity after the update
     * @throws BusinessLogicException if the new location violates the location rules
     */
    public LocationEntity updateLocation(Long pNeighborhoodId, LocationEntity pLocationEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for location with id = {0}", pLocationEntity.getId());

        // find original location
        LocationEntity original = locationPersistence.find(pNeighborhoodId, pLocationEntity.getId());

        // No two ocations can have the same name
        if (!original.getName().equals(pLocationEntity.getName())) {

            if (locationPersistence.findByName(pLocationEntity.getName()) != null) {
                throw new BusinessLogicException("The neighborhood already has a location with that name!");
            }
        }

        // verifies that the new location meets the rules
        verifyBusinessRules(pLocationEntity);

        // sets the location's neighborhood
        pLocationEntity.setNeighborhood(original.getNeighborhood());

        // update location
        LocationEntity newEntity = locationPersistence.update(pNeighborhoodId, pLocationEntity);

        LOGGER.log(Level.INFO, "End the update process for location with id = {0}",
                pLocationEntity.getName());
        return newEntity;

    }

    /**
     * Deletes a location by ID.
     *
     * @param pNeighborhoodId the id from parent neighborhood.
     * @param pLocationId the ID of the location to be deleted
     *
     */
    public void deleteLocation(Long pNeighborhoodId, Long pLocationId) {
        LOGGER.log(Level.INFO, "Begin the delete process for location with id = {0}", pLocationId);

        if (locationPersistence.find(pNeighborhoodId, pLocationId) != null) {
            locationPersistence.delete(pNeighborhoodId, pLocationId);
        }
        LOGGER.log(Level.INFO, "End the delete process for location with id = {0}", pLocationId);
    }

    /**
     * Verifies that the the location is valid.
     *
     * @param pLocationEntity location to verify
     *
     * @throws BusinessLogicException if the location doesn't satisfy the location rules
     */
    private void verifyBusinessRules(LocationEntity pLocationEntity) throws BusinessLogicException {

        // 2. The name of the location cannot be null
        if (pLocationEntity.getName() == null) {
            throw new BusinessLogicException("The location name cannot be null!");
        }

        // 3. the Address must not be null
        if (pLocationEntity.getAddress() == null) {
            throw new BusinessLogicException("The location address cannot be null!");
        }

        // 4. Open time must not be null
        if (pLocationEntity.getOpenTime() == null) {
            throw new BusinessLogicException("The location opening time cannot be null!");
        }

        // 5. Close time must not be null
        if (pLocationEntity.getCloseTime() == null) {
            throw new BusinessLogicException("The location closing time cannot be null!");
        }

        String openingTime = pLocationEntity.getOpenTime();
        String closingTime = pLocationEntity.getCloseTime();

        // date format for the opening and closing times
        DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");

        // placeholder dates represening the times
        Date open = null;
        Date close = null;

        // 6. Open and closing times must be legal hh:mm aa format
        try {
            open = timeFormat.parse(openingTime);
            close = timeFormat.parse(closingTime);
        } catch (ParseException e) {
            throw new BusinessLogicException("The location times have an illegal format!");
        }

        // 7. Open time must come before the closing time
        if (open.compareTo(close) > 0) {
            throw new BusinessLogicException("The location opening time must come before the closing time!");
        }

        // 8. The latitude must not be null
        if (pLocationEntity.getLatitude() == null) {
            throw new BusinessLogicException("The location latitude cannot be null!");
        }

        // 9. The longitude must not be null
        if (pLocationEntity.getLongitude() == null) {
            throw new BusinessLogicException("The location longitude cannot be null!");
        }

    }

}
