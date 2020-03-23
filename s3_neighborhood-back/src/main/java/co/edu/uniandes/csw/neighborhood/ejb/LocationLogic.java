/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.LocationPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class LocationLogic {

    private static final Logger LOGGER = Logger.getLogger(LocationLogic.class.getName());

    @Inject
    private LocationPersistence persistence;
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    public LocationEntity createLocation(LocationEntity locationEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for location has started");

        // OpenTime can not be null
        if (locationEntity.getOpenTime() == null) {
            throw new BusinessLogicException("An open time has to be specified");
        }
        // name can not be null
        if (locationEntity.getName() == null) {
            throw new BusinessLogicException("A name has to be specified");
        }
        // Address can not be null
        if (locationEntity.getAddress() == null) {
            throw new BusinessLogicException("An address has to be specified");
        }
        // CloseTime can not be null
        if (locationEntity.getCloseTime() == null) {
            throw new BusinessLogicException("A close time has to be specified");
        }
        // OpenTime can not be null
        if (locationEntity.getOpenTime() == null) {
            throw new BusinessLogicException("An open time has to be specified");
        }

        LocationEntity entity = persistence.create(locationEntity);
        LOGGER.log(Level.INFO, "Creation process for location eneded");

        return persistence.find(entity.getId());
    }

    public void deleteLocation(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for location with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for location with id = {0}", id);
    }

    public List<LocationEntity> getLocations() {

        LOGGER.log(Level.INFO, "Starting querying process for all locations");
        List<LocationEntity> locations = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all locations");
        return locations;
    }

    public LocationEntity getLocation(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for location with id {0}", id);
        LocationEntity location = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  location with id {0}", id);
        return location;
    }

    public LocationEntity updateLocation(LocationEntity locationEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for location with id {0}", locationEntity.getId());

        // OpenTime can not be null
        if (locationEntity.getOpenTime() == null) {
            throw new BusinessLogicException("An open time has to be specified");
        }
        // name can not be null
        if (locationEntity.getName() == null) {
            throw new BusinessLogicException("A name has to be specified");
        }
        // Address can not be null
        if (locationEntity.getAddress() == null) {
            throw new BusinessLogicException("An address has to be specified");
        }
        // CloseTime can not be null
        if (locationEntity.getCloseTime() == null) {
            throw new BusinessLogicException("A close time has to be specified");
        }
        // OpenTime can not be null
        if (locationEntity.getOpenTime() == null) {
            throw new BusinessLogicException("An open time has to be specified");
        }

        LocationEntity modified = persistence.update(locationEntity);
        LOGGER.log(Level.INFO, "Ended update process for location with id {0}", locationEntity.getId());
        return modified;
    }
}
