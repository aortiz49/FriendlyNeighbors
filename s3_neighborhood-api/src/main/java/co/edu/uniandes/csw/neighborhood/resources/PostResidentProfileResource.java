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

import co.edu.uniandes.csw.neighborhood.dtos.PostDTO;
import co.edu.uniandes.csw.neighborhood.dtos.PostDetailDTO;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
import co.edu.uniandes.csw.neighborhood.ejb.PostResidentProfileLogic;
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
 * Class implementing resource "resident/{postId}/posts".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResidentProfileResource {

    private static final Logger LOGGER = Logger.getLogger(PostResidentProfileResource.class.getName());

    @Inject
    private PostResidentProfileLogic residentPostLogic;

    @Inject
    private PostLogic postLogic;

    /**
     * Creates a post with existing resident
     *
     * @param post postId from post to be associated
     * @param residentsId postId from resident
     * @param neighId parent neighborhood
     * @return JSON {@link PostDetailDTO} -
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} 
     * Logic error if not found
     */
    @POST
    public PostDetailDTO createPost(@PathParam("residentsId") Long residentsId, PostDTO post,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating post for resident from resource: input: residentsId {0} , postsId {1}", new Object[]{residentsId, post.getId()});

        PostEntity entity = null;

        entity = postLogic.createPost(post.toEntity(), residentsId, neighId);
        
        PostDetailDTO dto = new PostDetailDTO(postLogic.getPost(entity.getId(), neighId));
        LOGGER.log(Level.INFO, "Ended creating post for resident from resource: output: {0}", dto.getId());
        return dto;
    }

    /**
     * Looks for all the posts associated to a resident and returns it
     *
     * @param residentsId postId from resident whose posts are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - posts found in resident. An
     * empty list if none is found
     */
    @GET
    public List<PostDetailDTO> getPosts(@PathParam("residentsId") Long residentsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for posts from resources: input: {0}", residentsId);
        List<PostDetailDTO> list = postsListEntity2DTO(residentPostLogic.getPosts(residentsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for posts from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a post with specified ID by URL which is associated with 
     * resident and returns it
     *
     * @param postsId postId from wanted post
     * @param residentsId postId from resident whose post is wanted
     * @param neighId parent neighborhood
     * @return {@link PostDetailDTO} - post found inside resident
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if post not found
     */
    @GET
    @Path("{postsId: \\d+}")
    public PostDetailDTO getPost(@PathParam("residentsId") Long residentsId, @PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for post: input: residentsId {0} , postsId {1}", new Object[]{residentsId, postsId});
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(residentPostLogic.getPost(residentsId, postsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for post: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     *
     * Updates a list from posts inside a resident which is received in body
     *
     * @param residentsId postId from resident whose list of posts is to be updated
     * @param posts JSONArray {@link PostDetailDTO} - modified posts list
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<PostDetailDTO> replacePosts(@PathParam("residentsId") Long residentsId, List<PostDetailDTO> posts, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Replacing resident posts from resource: input: residentsId {0} , posts {1}", new Object[]{residentsId, posts});
        for (PostDetailDTO post : posts) {
            if (postLogic.getPost(post.getId(), neighId) == null) {
                throw new WebApplicationException("Resource /posts/" + posts + " does not exist.", 404);
            }
        }
        List<PostDetailDTO> lista = postsListEntity2DTO(residentPostLogic.replacePosts(residentsId, postsListDTO2Entity(posts), neighId));
        LOGGER.log(Level.INFO, "Ended replacing resident posts from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a post from resident
     *
     * @param residentsId postId from resident whose post is to be removed
     * @param postsId postId from post to be removed
     * @param neighId parent neighborhood
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} 
     * Error if not found
     */
    @DELETE
    @Path("{postsId: \\d+}")
    public void removePost(@PathParam("residentsId") Long residentsId, @PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Removing post from resident: input: residentsId {0} , postsId {1}", new Object[]{residentsId, postsId});
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        residentPostLogic.removePost(residentsId, postsId, neighId);
        LOGGER.info("Ended removing post from resident: output: void");
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
