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

import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.PostViewerLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
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
 * Class implementing resource "posts/{id}/viewers".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostViewerResource {

    private static final Logger LOGGER = Logger.getLogger(PostViewerResource.class.getName());

    @Inject
    private PostViewerLogic postViewerLogic;

    @Inject
    private ResidentProfileLogic viewerLogic;

    /**
     * Associates a viewer with existing post
     *
     * @param viewersId id from viewer to be associated
     * @param postsId id from post
     * @param neighId parent neighborhood
     * @return JSON {@link ResidentProfileDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{viewersId: \\d+}")
    public ResidentProfileDetailDTO associateViewerToPost(@PathParam("postsId") Long postsId, @PathParam("viewersId") Long viewersId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating viewer to post from resource: input: postsId {0} , viewersId {1}", new Object[]{postsId, viewersId});
        if (viewerLogic.getResident(viewersId, neighId) == null) {
            throw new WebApplicationException("Resource /viewers/" + viewersId + " does not exist.", 404);
        }
        ResidentProfileEntity e = postViewerLogic.associateViewerToPost(postsId, viewersId, neighId);
        
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(e);

        LOGGER.log(Level.INFO, "Ended associating viewer to post from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the viewers associated to a post and returns it
     *
     * @param postsId id from post whose viewers are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link ResidentProfileDetailDTO} - viewers found in post. An
     * empty list if none is found
     */
    @GET
    public List<ResidentProfileDetailDTO> getViewers(@PathParam("postsId") Long postsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for viewers from resources: input: {0}", postsId);
        List<ResidentProfileDetailDTO> list = viewersListEntity2DTO(postViewerLogic.getViewers(postsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for viewers from resources: output: {0}", list);
        return list;
    }
    
    @Path("/potential")
    @GET
    public List<ResidentProfileDetailDTO> getPViewers(@PathParam("postsId") Long postsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for pViewers from resources: input: {0}", postsId);
          List<ResidentProfileDetailDTO> list = viewersListEntity2DTO(postViewerLogic.getPotentialViewers(postsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for pViewers from resources: output: {0}", list);
        return list;
    }
    

    /**
     * Looks for a viewer with specified ID by URL which is associated with 
     * post and returns it
     *
     * @param viewersId id from wanted viewer
     * @param postsId id from post whose viewer is wanted
     * @param neighId parent neighborhood
     * @return {@link ResidentProfileDetailDTO} - viewer found inside post
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if viewer not found
     */
    @GET
    @Path("{viewersId: \\d+}")
    public ResidentProfileDetailDTO getViewer(@PathParam("postsId") Long postsId, @PathParam("viewersId") Long viewersId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for viewer: input: postsId {0} , viewersId {1}", new Object[]{postsId, viewersId});
        if (viewerLogic.getResident(viewersId, neighId) == null) {
            throw new WebApplicationException("Resource /viewers/" + viewersId + " does not exist.", 404);
        }
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(postViewerLogic.getViewer(postsId, viewersId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for viewer: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * 
     * Updates a list from viewers inside a post which is received in body
     *
     * @param postsId  id from post whose list of viewers is to be updated
     * @param viewers JSONArray {@link ResidentProfileDetailDTO} - modified viewers list 
     * @param neighId  parent neighborhood
     * @return JSONArray {@link ResidentProfileDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<ResidentProfileDetailDTO> replaceViewers(@PathParam("postsId") Long postsId, List<ResidentProfileDetailDTO> viewers,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Replacing post viewers from resource: input: postsId {0} , viewers {1}", new Object[]{postsId, viewers});
        for (ResidentProfileDetailDTO viewer : viewers) {
            if (viewerLogic.getResident(viewer.getId(), neighId) == null) {
                     throw new WebApplicationException("Resource /viewers/" + viewers + " does not exist.", 404);
            }
        }
        List<ResidentProfileDetailDTO> lista = viewersListEntity2DTO(postViewerLogic.replaceViewers(postsId, viewersListDTO2Entity(viewers), neighId));
        LOGGER.log(Level.INFO, "Ended replacing post viewers from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a viewer from post
     *
     * @param postsId id from post whose viewer is to be removed
     * @param viewersId id from viewer to be removed
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{viewersId: \\d+}")
    public void removeViewer(@PathParam("postsId") Long postsId, @PathParam("viewersId") Long viewersId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Removing viewer from post: input: postsId {0} , viewersId {1}", new Object[]{postsId, viewersId});
        if (viewerLogic.getResident(viewersId, neighId) == null) {
                 throw new WebApplicationException("Resource /viewers/" + viewersId + " does not exist.", 404);
        }
        postViewerLogic.removeViewer(postsId, viewersId, neighId);
        LOGGER.info("Ended removing viewer from post: output: void");
    }

    /**
     * Converts an entity list with viewers to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<ResidentProfileDetailDTO> viewersListEntity2DTO(List<ResidentProfileEntity> entityList) {
        List<ResidentProfileDetailDTO> list = new ArrayList<>();
        for (ResidentProfileEntity entity : entityList) {
            list.add(new ResidentProfileDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with viewers to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<ResidentProfileEntity> viewersListDTO2Entity(List<ResidentProfileDetailDTO> dtos) {
        List<ResidentProfileEntity> list = new ArrayList<>();
        for (ResidentProfileDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
