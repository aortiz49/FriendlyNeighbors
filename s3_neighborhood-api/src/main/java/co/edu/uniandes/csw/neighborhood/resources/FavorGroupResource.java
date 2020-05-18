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
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.FavorDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.FavorGroupLogic;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "groups/{id}/favors".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FavorGroupResource {

    private static final Logger LOGGER = Logger.getLogger(FavorGroupResource.class.getName());

    @Inject
    private FavorGroupLogic groupFavorLogic;

    @Inject
    private FavorLogic favorLogic;

    /**
     * Associates a favor with existing group
     *
     * @param favorsId id from favor to be associated
     * @param groupsId id from group
     * @param neighId parent neighborhood
     * @return JSON {@link FavorDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{favorsId: \\d+}")
    public FavorDetailDTO associateFavorToGroup(@PathParam("groupsId") Long groupsId, @PathParam("favorsId") Long favorsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating favor to group from resource: input: groupsId {0} , favorsId {1}", new Object[]{groupsId, favorsId});
        if (favorLogic.getFavor(favorsId, neighId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorEntity e = groupFavorLogic.associateFavorToGroup(groupsId, favorsId, neighId);
        
        FavorDetailDTO detailDTO = new FavorDetailDTO(e);

        LOGGER.log(Level.INFO, "Ended associating favor to group from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the favors associated to a group and returns it
     *
     * @param groupsId id from group whose favors are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link FavorDetailDTO} - favors found in group. An
     * empty list if none is found
     */
    @GET
    public List<FavorDetailDTO> getFavors(@PathParam("groupsId") Long groupsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for favors from resources: input: {0}", groupsId);
        List<FavorDetailDTO> list = favorsListEntity2DTO(groupFavorLogic.getFavors(groupsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for favors from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a favor with specified ID by URL which is associated with 
     * group and returns it
     *
     * @param favorsId id from wanted favor
     * @param groupsId id from group whose favor is wanted
     * @param neighId parent neighborhood
     * @return {@link FavorDetailDTO} - favor found inside group
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if favor not found
     */
    @GET
    @Path("{favorsId: \\d+}")
    public FavorDetailDTO getFavor(@PathParam("groupsId") Long groupsId, @PathParam("favorsId") Long favorsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for favor: input: groupsId {0} , favorsId {1}", new Object[]{groupsId, favorsId});
        if (favorLogic.getFavor(favorsId, neighId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorDetailDTO detailDTO = new FavorDetailDTO(groupFavorLogic.getFavor(groupsId, favorsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for favor: output: {0}", detailDTO);
        return detailDTO;
    }


    /**
     * Removes a favor from group
     *
     * @param groupsId id from group whose favor is to be removed
     * @param favorsId id from favor to be removed
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{favorsId: \\d+}")
    public void removeFavor(@PathParam("groupsId") Long groupsId, @PathParam("favorsId") Long favorsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Removing favor from group: input: groupsId {0} , favorsId {1}", new Object[]{groupsId, favorsId});
        if (favorLogic.getFavor(favorsId, neighId) == null) {
                 throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        groupFavorLogic.removeFavor(groupsId, favorsId, neighId);
        LOGGER.info("Ended removing favor from group: output: void");
    }

    /**
     * Converts an entity list with favors to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<FavorDetailDTO> favorsListEntity2DTO(List<FavorEntity> entityList) {
        List<FavorDetailDTO> list = new ArrayList<>();
        for (FavorEntity entity : entityList) {
            list.add(new FavorDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with favors to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<FavorEntity> favorsListDTO2Entity(List<FavorDetailDTO> dtos) {
        List<FavorEntity> list = new ArrayList<>();
        for (FavorDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
