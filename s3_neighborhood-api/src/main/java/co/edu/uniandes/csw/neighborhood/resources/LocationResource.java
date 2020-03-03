/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.LocationDTO;
import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author v.cardonac1
 */
@Path("locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class LocationResource {
    private static final Logger LOGGER = Logger.getLogger(LocationResource.class.getName());
    
    @Inject
    private LocationLogic locationLogic;
    
    @POST
    public LocationDTO createLocation(LocationDTO location) throws BusinessLogicException{
        LocationEntity entity = locationLogic.createLocation(location.toEntity());
        LocationDTO locationDTO = new LocationDTO(entity);
        return location;
    }
    
    @GET
    @Path("{locationsId: \\d+}")
    public LocationDTO geLocation(@PathParam("locationsId") Long locationsId) {
        LOGGER.log(Level.INFO, "Looking for  location from resource: input: {0}", locationsId);
        LocationEntity entity = locationLogic.getLocation(locationsId);
        if (entity == null) {
            throw new WebApplicationException("Resource /locations/" + locationsId + " does not exist.", 404);
        }
        LocationDTO locationDTO = new LocationDTO(entity);
        LOGGER.log(Level.INFO, "Ended looking for location from resource: output: {0}", locationDTO);
        return locationDTO;
    }
    
    @GET
    public List<LocationDTO> getLocations() {
        LOGGER.info("Looking for all locations from resources: input: void");
        List<LocationDTO> locations = listEntity2DTO(locationLogic.getLocations());
        LOGGER.log(Level.INFO, "Ended looking for all locations from resources: output: {0}", locations);
        return locations;
    }
    
    @DELETE
    @Path("{locationsId: \\d+}")
    public void deleteLocation(@PathParam("locationsId") Long locationsId) {
        LOGGER.log(Level.INFO, "Deleting location from resource: input: {0}", locationsId);
        if (locationLogic.getLocation(locationsId) == null) {
            throw new WebApplicationException("Resource /locations/" + locationsId + " does not exist.", 404);
        }
        locationLogic.deleteLocation(locationsId);
        LOGGER.info("Resident deleted from resource: output: void");
    }
    
    @PUT
    @Path("{locationsId: \\d+}")
    public LocationDTO updateLocation(@PathParam("locationsId") Long locationsId, LocationDTO location) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating location from resource: input: locationsId: {0} , locations: {1}", new Object[]{locationsId, location});
        location.setId(locationsId);
        if (locationLogic.getLocation(locationsId) == null) {
            throw new WebApplicationException("El recurso /locations/" + locationsId + " no existe.", 404);
        }
        LocationDTO locationDTO = new LocationDTO(locationLogic.updateLocation(location.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating location from resource: output: {0}", locationDTO);

        return locationDTO;
    }
    
    private List<LocationDTO> listEntity2DTO(List<LocationEntity> entityList) {
        List<LocationDTO> list = new ArrayList<>();
        for (LocationEntity entity : entityList) {
            list.add(new LocationDTO(entity));
        }
        return list;    
    }
    
}
