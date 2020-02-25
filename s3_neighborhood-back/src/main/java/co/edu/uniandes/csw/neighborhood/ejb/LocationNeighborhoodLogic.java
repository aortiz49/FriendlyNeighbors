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
public class LocationNeighborhoodLogic {
    
    private static final Logger LOGGER = Logger.getLogger(LocationNeighborhoodLogic.class.getName());
    
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;
    
    @Inject
    private LocationPersistence locationPersistence;
    
    public LocationEntity associateLocationToNeighborhood(Long locationId, Long neighborhoodId){
        LOGGER.log(Level.INFO, "Trying to add location with id - {0}", locationId);
        LocationEntity locationEntity = locationPersistence.find(locationId);
        NeighborhoodEntity neighborhoodEntity = neighborhoodPersistence.find(neighborhoodId);
        
        locationEntity.setNeighborhood(neighborhoodEntity);
        LOGGER.log(Level.INFO, "Location is associatedd with neighborhood with id = {0}", neighborhoodId);
        return locationPersistence.find(locationId);
    }
    
    public List<LocationEntity> getLocations(Long neighborhoodId){
        LOGGER.log(Level.INFO, "Gets all locations belonging to neighborhood with id = {0}", neighborhoodId);
        return neighborhoodPersistence.find(neighborhoodId).getPlaces();
    }
    
    public LocationEntity getLocation(Long locationId, Long neighborhoodId) throws BusinessLogicException{
        LOGGER.log(Level.INFO, "Finding location with id = {0} from neighborhood with id = " + neighborhoodId, locationId);
        List<LocationEntity> locations = neighborhoodPersistence.find(neighborhoodId).getPlaces();
        LocationEntity location = locationPersistence.find(locationId);
        int index = locations.indexOf(location);
        LOGGER.log(Level.INFO, "Finish query about locations id = {0} from neighborhood with id = "+ neighborhoodId, locationId);
        if(index >= 0){
            return locations.get(index);
        }
        throw new BusinessLogicException("There is no association between location and neighborhood");
    }
    
    public List<LocationEntity> replaceLocations(List<LocationEntity> locations, Long neighborhoodId){
        LOGGER.log(Level.INFO, "Trying to replace locations related to neighborhood with id = {0}", neighborhoodId);
        NeighborhoodEntity neighborhoodEntity = neighborhoodPersistence.find(neighborhoodId);
        List<LocationEntity> locationsList = locationPersistence.findAll();
        for(LocationEntity location: locationsList){
            
            if(locations.contains(location)){
                if(location.getNeighborhood() != neighborhoodEntity){
                    location.setNeighborhood(neighborhoodEntity);
                }
            } else{
                location.setNeighborhood(null);
            }

        }
        neighborhoodEntity.setPlaces(locations);
        LOGGER.log(Level.INFO, "Ended trying to replace locations related to neighborhood with id = {0}", neighborhoodId);
        return neighborhoodEntity.getPlaces();
    }
    
    public void removeLocation(Long locationId, Long neighborhoodId){
        LOGGER.log(Level.INFO, "Trying to delete a locations from neighborhood with id = {0}", neighborhoodId);
        LocationEntity locationEntity = locationPersistence.find(locationId);
        NeighborhoodEntity neighborhoodEntity = neighborhoodPersistence.find(neighborhoodId);
        neighborhoodEntity.getPlaces().remove(locationEntity);
        LOGGER.log(Level.INFO, "Finished removing a location from neighborhood with id = {0}", neighborhoodId);
        
    }
    
    
}
