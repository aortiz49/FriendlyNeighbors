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

import co.edu.uniandes.csw.neighborhood.dtos.PostDetailDTO;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ViewerPostLogic;
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
 * Class implementing resource "resident/{id}/postToView".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ViewerPostResource {

    private static final Logger LOGGER = Logger.getLogger(ViewerPostResource.class.getName());

    @Inject
    private ViewerPostLogic ViewerPostLogic;

    @Inject
    private PostLogic PostLogic;

    /**
     * Associates a Post with existing Viewer
     *
     * @param postsId id from Post to be associated
     * @param viewersId id from Viewer
     * @param neighId parent neighborhood
     * @return JSON {@link PostDetailDTO} -
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{postsId: \\d+}")
    public PostDetailDTO associatePostToResidentProfile(@PathParam("viewersId") Long viewersId, @PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating Post to Viewer from resource: input: viewersId {0} , postsId {1}", new Object[]{viewersId, postsId});
        if (PostLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /Posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(ViewerPostLogic.associatePostToViewer(viewersId, postsId, neighId));
        LOGGER.log(Level.INFO, "Ended associating Post to Viewer from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the Posts associated to a Viewer and returns it
     *
     * @param viewersId id from Viewer whose Posts are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - Posts found in Viewer. An
     * empty list if none is found
     */
    @GET
    public List<PostDetailDTO> getPosts(@PathParam("viewersId") Long viewersId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for Posts from resources: input: {0}", viewersId);
        List<PostDetailDTO> list = PostsListEntity2DTO(ViewerPostLogic.getPosts(viewersId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for Posts from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a Post with specified ID by URL which is associated with 
     * Viewer and returns it
     *
     * @param postsId id from wanted Post
     * @param viewersId id from Viewer whose Post is wanted
     * @param neighId parent neighborhood
     * @return {@link PostDetailDTO} - Post found inside Viewer
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if Post not found
     */
    @GET
    @Path("{postsId: \\d+}")
    public PostDetailDTO getPost(@PathParam("viewersId") Long viewersId, @PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for Post: input: viewersId {0} , postsId {1}", new Object[]{viewersId, postsId});
        if (PostLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /Posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(ViewerPostLogic.getPost(viewersId, postsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for Post: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * 
     * Updates a list from Posts inside a Viewer which is received in body
     *
     * @param viewersId  id from Viewer whose list of Posts is to be updated
     * @param Posts JSONArray {@link PostDetailDTO} - modified Posts list 
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<PostDetailDTO> replacePosts(@PathParam("viewersId") Long viewersId, List<PostDetailDTO> Posts, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Replacing Viewer Posts from resource: input: viewersId {0} , Posts {1}", new Object[]{viewersId, Posts});
        for (PostDetailDTO Post : Posts) {
            if (PostLogic.getPost(Post.getId(), neighId) == null) {
                     throw new WebApplicationException("Resource /Posts/" + Posts + " does not exist.", 404);
            }
        }
        List<PostDetailDTO> lista = PostsListEntity2DTO(ViewerPostLogic.replacePosts(viewersId, PostsListDTO2Entity(Posts), neighId));
        LOGGER.log(Level.INFO, "Ended replacing Viewer Posts from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a Post from Viewer
     *
     * @param viewersId id from Viewer whose Post is to be removed
     * @param postsId id from Post to be removed
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{postsId: \\d+}")
    public void removePost(@PathParam("viewersId") Long viewersId, @PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Removing Post from Viewer: input: viewersId {0} , postsId {1}", new Object[]{viewersId, postsId});
        if (PostLogic.getPost(postsId, neighId) == null) {
                 throw new WebApplicationException("Resource /Posts/" + postsId + " does not exist.", 404);
        }
        ViewerPostLogic.removePost(viewersId, postsId, neighId);
        LOGGER.info("Ended removing Post from Viewer: output: void");
    }

    /**
     * Converts an entity list with Posts to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<PostDetailDTO> PostsListEntity2DTO(List<PostEntity> entityList) {
        List<PostDetailDTO> list = new ArrayList<>();
        for (PostEntity entity : entityList) {
            list.add(new PostDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with Posts to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<PostEntity> PostsListDTO2Entity(List<PostDetailDTO> dtos) {
        List<PostEntity> list = new ArrayList<>();
        for (PostDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
