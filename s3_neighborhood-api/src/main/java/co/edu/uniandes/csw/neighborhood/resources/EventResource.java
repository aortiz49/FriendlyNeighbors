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

import co.edu.uniandes.csw.neighborhood.dtos.FavorDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "favors".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class EventResource {

    private static final Logger LOGGER = Logger.getLogger(FavorResource.class.getName());

    @Inject
    private FavorLogic favorLogic;

    /**
     * Looks for all favors on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link FavorDetailDTO} - All the favors on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<FavorDetailDTO> getFavors(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all favors from resources: input: void");
        List<FavorDetailDTO> favors = listEntity2DTO(favorLogic.getFavors(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all favors from resources: output: {0}", favors);
        return favors;
    }

    /**
     * Looks for the favor with id received in the URL y returns it.
     *
     * @param favorsId Id from wanted favor. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link FavorDetailDTO} - Wanted favor DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{favorsId: \\d+}")
    public FavorDetailDTO getFavor(@PathParam("favorsId") Long favorsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  favor from resource: input: {0}", favorsId);
        FavorEntity favorEntity = favorLogic.getFavor(favorsId, neighId);
        if (favorEntity == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorDetailDTO detailDTO = new FavorDetailDTO(favorEntity);
        LOGGER.log(Level.INFO, "Ended looking for favor from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates favor with id from URL with the information contained in
     * request body.
     *
     * @param favorsId ID from favor to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param favor {@link FavorDetailDTO} Favor to be updated.
     * @return JSON {@link FavorDetailDTO} - Updated favor
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{favorsId: \\d+}")
    public FavorDetailDTO updateAuthor(@PathParam("favorsId") Long favorsId, @PathParam("neighborhoodId") Long neighId, FavorDetailDTO favor) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating favor from resource: input: authorsId: {0} , author: {1}", new Object[]{favorsId, favor});
        favor.setId(favorsId);
        if (favorLogic.getFavor(favorsId, neighId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorDetailDTO detailDTO = new FavorDetailDTO(favorLogic.updateFavor(favor.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating favor from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the favor with the associated id received in URL
     *
     * @param favorsId id from favor to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{favorsId: \\d+}")
    public void deleteFavor(@PathParam("favorsId") Long favorsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Deleting favor from resource: input: {0}", favorsId);
        if (favorLogic.getFavor(favorsId, neighId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        favorLogic.deleteFavor(favorsId, neighId);
        LOGGER.info("Favor deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for favors.
     *
     * @param entityList Favor entity list to be converted.
     * @return Favor DTO list
     */
    private List<FavorDetailDTO> listEntity2DTO(List<FavorEntity> entityList) {
        List<FavorDetailDTO> list = new ArrayList<>();
        for (FavorEntity entity : entityList) {
            list.add(new FavorDetailDTO(entity));
        }
        return list;
    }
    
        /**
     *
     * Connects /favors route with /helpers route which are dependent of favor
     * resource, by redirecting to the service managing the URL segment in
     * charge of the helpers
     *
     * @param favorsId id from favor from which the resource is being accessed
     * @param neighId parent neighborhood
     * @return helpers resource from the specified favor
     */

    @Path("{favorsId: \\d+}/helpers")
    public Class<FavorHelperResource> getFavorHelperResource(@PathParam("favorsId") Long favorsId, @PathParam("neighborhoodId") Long neighId) {
        if (favorLogic.getFavor(favorsId, neighId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        return FavorHelperResource.class;
    }



}
