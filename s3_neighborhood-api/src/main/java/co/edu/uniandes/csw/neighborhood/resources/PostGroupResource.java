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
import co.edu.uniandes.csw.neighborhood.ejb.PostGroupLogic;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
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
 * Class implementing resource "groups/{id}/posts".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostGroupResource {

    private static final Logger LOGGER = Logger.getLogger(PostGroupResource.class.getName());

    @Inject
    private PostGroupLogic groupPostLogic;

    @Inject
    private PostLogic postLogic;

    /**
     * Associates a post with existing group
     *
     * @param postsId id from post to be associated
     * @param groupsId id from group
     * @param neighId parent neighborhood
     * @return JSON {@link PostDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{postsId: \\d+}")
    public PostDetailDTO associatePostToGroup(@PathParam("groupsId") Long groupsId, @PathParam("postsId") Long postsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating post to group from resource: input: groupsId {0} , postsId {1}", new Object[]{groupsId, postsId});
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostEntity e = groupPostLogic.associatePostToGroup(groupsId, postsId, neighId);
        
        PostDetailDTO detailDTO = new PostDetailDTO(e);

        LOGGER.log(Level.INFO, "Ended associating post to group from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the posts associated to a group and returns it
     *
     * @param groupsId id from group whose posts are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - posts found in group. An
     * empty list if none is found
     */
    @GET
    public List<PostDetailDTO> getPosts(@PathParam("groupsId") Long groupsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for posts from resources: input: {0}", groupsId);
        List<PostDetailDTO> list = postsListEntity2DTO(groupPostLogic.getPosts(groupsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for posts from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a post with specified ID by URL which is associated with 
     * group and returns it
     *
     * @param postsId id from wanted post
     * @param groupsId id from group whose post is wanted
     * @param neighId parent neighborhood
     * @return {@link PostDetailDTO} - post found inside group
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if post not found
     */
    @GET
    @Path("{postsId: \\d+}")
    public PostDetailDTO getPost(@PathParam("groupsId") Long groupsId, @PathParam("postsId") Long postsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for post: input: groupsId {0} , postsId {1}", new Object[]{groupsId, postsId});
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(groupPostLogic.getPost(groupsId, postsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for post: output: {0}", detailDTO);
        return detailDTO;
    }


    /**
     * Removes a post from group
     *
     * @param groupsId id from group whose post is to be removed
     * @param postsId id from post to be removed
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{postsId: \\d+}")
    public void removePost(@PathParam("groupsId") Long groupsId, @PathParam("postsId") Long postsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Removing post from group: input: groupsId {0} , postsId {1}", new Object[]{groupsId, postsId});
        if (postLogic.getPost(postsId, neighId) == null) {
                 throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        groupPostLogic.removePost(groupsId, postsId, neighId);
        LOGGER.info("Ended removing post from group: output: void");
    }

    /**
     * Converts an entity list with posts to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<PostDetailDTO> postsListEntity2DTO(List<PostEntity> entityList) {
        List<PostDetailDTO> list = new ArrayList<>();
        for (PostEntity entity : entityList) {
            list.add(new PostDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with posts to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<PostEntity> postsListDTO2Entity(List<PostDetailDTO> dtos) {
        List<PostEntity> list = new ArrayList<>();
        for (PostDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
