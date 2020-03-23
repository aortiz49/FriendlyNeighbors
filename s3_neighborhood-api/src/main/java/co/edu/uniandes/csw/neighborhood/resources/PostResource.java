/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.PostDTO;
import co.edu.uniandes.csw.neighborhood.dtos.PostDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "posts".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PostResource {

    private static final Logger LOGGER = Logger.getLogger(PostResource.class.getName());

    @Inject
    private PostLogic postLogic;

    /**
     * Looks for all posts on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link PostDetailDTO} - All the posts on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<PostDetailDTO> getPosts(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all posts from resources: input: void");
        List<PostDetailDTO> posts = listEntity2DTO(postLogic.getPosts(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all posts from resources: output: {0}", posts);
        return posts;
    }

    /**
     * Looks for the post with id received in the URL y returns it.
     *
     * @param postsId Id from wanted post. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link PostDetailDTO} - Wanted post DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{postsId: \\d+}")
    public PostDetailDTO getPost(@PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  post from resource: input: {0}", postsId);
        PostEntity postEntity = postLogic.getPost(postsId, neighId);
        if (postEntity == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(postEntity);
        LOGGER.log(Level.INFO, "Ended looking for post from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates post with id from URL with the information contained in
     * request body.
     *
     * @param postsId ID from post to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param post {@link PostDetailDTO} Post to be updated.
     * @return JSON {@link PostDetailDTO} - Updated post
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{postsId: \\d+}")
    public PostDetailDTO updateAuthor(@PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId, PostDetailDTO post) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating post from resource: input: authorsId: {0} , author: {1}", new Object[]{postsId, post});
        post.setId(postsId);
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(postLogic.updatePost(post.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating post from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the post with the associated id received in URL
     *
     * @param postsId id from post to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{postsId: \\d+}")
    public void deletePost(@PathParam("postsId") Long postsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Deleting post from resource: input: {0}", postsId);
        if (postLogic.getPost(postsId, neighId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        postLogic.deletePost(postsId, neighId);
        LOGGER.info("Post deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for posts.
     *
     * @param entityList Post entity list to be converted.
     * @return Post DTO list
     */
    private List<PostDetailDTO> listEntity2DTO(List<PostEntity> entityList) {
        List<PostDetailDTO> list = new ArrayList<>();
        for (PostEntity entity : entityList) {
            list.add(new PostDetailDTO(entity));
        }
        return list;
    }


}
