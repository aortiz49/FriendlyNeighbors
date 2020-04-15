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
package co.edu.uniandes.csw.neighborhood.resources;
//===================================================
// Imports
//===================================================

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
 * Class that represents the "locations" resource.
 *
 * @author aortiz49
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class LocationResource {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to display messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationResource.class.getName());

    /**
     * Injects LocationLogic dependencies.
     */
    @Inject
    private LocationLogic locationLogic;

//===================================================
// REST API
//===================================================
    /**
     *
     * Creates a new location with the information received in the body of the petition and returns
     * a new identical object with and auto-generated id by the database.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the businesses
     * @param pLocation {@link LocationDTO} the location to be created
     *
     * @return JSON {@link LocationDTO} the saved location with auto-generated id
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is an error when
     * creating the location
     */
    @POST
    public LocationDTO createLocation(@PathParam("neighborhoodId") Long pNeighborhoodId,
            LocationDTO pLocation) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Creating Location : input: {0}", pLocation.getName());

        LocationEntity locationEntity = locationLogic.createLocation(pNeighborhoodId,
                pLocation.toEntity());

        LocationDTO newLocationDTO = new LocationDTO(locationLogic.getLocation(
                pNeighborhoodId, locationEntity.getId()));

        LOGGER.log(Level.INFO, "Finished creating Location : input: {0}", pLocation.getName());
        return newLocationDTO;
    }

    /**
     * Creates a new location with the information received in the body of the petition and returns
     * a new identical object with auto-generated id by the data base.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the locations
     *
     * @return JSONArray {@link LocationDTO} - All the locations in a neighborhood if found.
     * Otherwise, an empty list.
     */
    @GET
    public List<LocationDTO> getAllLocations(@PathParam("neighborhoodId") Long pNeighborhoodId) {

        LOGGER.log(Level.INFO, "Looking for all locations in neighborhood: "
                + "input: {0}", pNeighborhoodId);

        List<LocationDTO> locations = listEntity2DTO(locationLogic.getLocations(pNeighborhoodId));

        LOGGER.log(Level.INFO, "Ended looking for all locations from resources: "
                + "output: {0}", locations);
        return locations;
    }

    /**
     * Looks for the location with id received in the URL and returns it.
     *
     * @param pNeighborhoodId the neighborhood containing the location
     * @param pLocationId the desired location id
     *
     * @return JSON {@link PostDetailDTO} - Wanted post DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} - Logic error if not
     * found
     */
    @GET
    @Path("{locationId: \\d+}")
    public LocationDTO getLocation(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("locationId") Long pLocationId) {

        LOGGER.log(Level.INFO, "Looking for  location from resource: input: {0}", pLocationId);

        LocationEntity locationEntity = locationLogic.getLocation(pNeighborhoodId, pLocationId);

        if (locationEntity == null) {
            throw new WebApplicationException("The Resource /neighborhoods/"
                    + pLocationId + " does not exist.", 404);
        }

        LocationDTO detailDTO = new LocationDTO(locationEntity);

        LOGGER.log(Level.INFO, "Ended looking for location from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates a location with id with the information contained in the request body.
     *
     * @param pNeighborhoodId the neighborhood containing the location
     * @param pLocationId the location id of the resource to be modified.
     * @param pLocation the location to update with
     *
     * @return the updated location
     * @throws BusinessLogicException if the location cannot be updated
     * @throws WebApplicationException if the location cannot be updated
     *
     */
    @PUT
    @Path("{locationId: \\d+}")
    public LocationDTO updateLocation(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("locationId") Long pLocationId,
            LocationDTO pLocation) throws BusinessLogicException, WebApplicationException {

        LOGGER.log(Level.INFO, "Updating location: input: location: {0} , "
                + "neighborhood: {1}", new Object[]{pLocationId, pLocation.getId()});

        pLocation.setId(pLocationId);

        if (locationLogic.getLocation(pNeighborhoodId, pLocationId) == null) {
            throw new WebApplicationException("Resource /locations/" + pLocationId
                    + " does not exist.", 404);
        }

        LocationDTO detailDTO = new LocationDTO(locationLogic.updateLocation(pNeighborhoodId,
                pLocation.toEntity()));

        LOGGER.log(Level.INFO, "Ended updating location from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the location with the associated id received by the URL.
     *
     * @param pNeighborhoodId the neighborhood containing the location
     * @param pLocationId id of the location to be deleted
     *
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} Logic error if not
     * found
     */
    @DELETE
    @Path("{locationId: \\d+}")
    public void deleteLocation(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("locationId") Long pLocationId) throws WebApplicationException {

        LOGGER.log(Level.INFO, "locationResource deleteLocation: input: {0}", pLocationId);

        if (locationLogic.getLocation(pNeighborhoodId, pLocationId) == null) {
            throw new WebApplicationException("The resource /locations/" + pLocationId + " does not exist.", 404);
        }

        locationLogic.deleteLocation(pNeighborhoodId, pLocationId);

        LOGGER.info("locationResource deleteLocation: output: void");
    }

    /**
     * Converts a location entity list to a location DTO list .
     *
     * @param pEntityList location entity list to be converted.
     * @return location DTO list
     */
    private List<LocationDTO> listEntity2DTO(List<LocationEntity> entityList) {
        List<LocationDTO> list = new ArrayList<>();
        for (LocationEntity entity : entityList) {
            list.add(new LocationDTO(entity));
        }
        return list;
    }

}
