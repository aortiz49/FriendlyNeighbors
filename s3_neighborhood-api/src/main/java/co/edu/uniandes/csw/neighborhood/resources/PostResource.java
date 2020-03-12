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
@Path("posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PostResource {

    private static final Logger LOGGER = Logger.getLogger(PostResource.class.getName());

    @Inject
    private PostLogic postLogic;

    /**
     * Creates a new post with the information received in request, then an
     * identical object is returned with an id generated by DB
     *
     * @param post {@link PostDTO} - Resident to be persisted
     * @return JSON {@link PostDTO} - Auto-generated post to be persisted
     */
    @POST
    public PostDTO createPost(PostDTO post) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating post from resource: input: {0}", post);

        PostDTO postDTO = new PostDTO(postLogic.createPost(post.toEntity()));

        LOGGER.log(Level.INFO, "Created post from resource: output: {0}", postDTO);
        return postDTO;
    }

    /**
     * Looks for all posts on application and returns them.
     *
     * @return JSONArray {@link PostDetailDTO} - All the posts on application
     * if found. Otherwise, an empty list.
     */
    @GET
    public List<PostDetailDTO> getPosts() {
        LOGGER.info("Looking for all posts from resources: input: void");
        List<PostDetailDTO> posts = listEntity2DTO(postLogic.getPosts());
        LOGGER.log(Level.INFO, "Ended looking for all posts from resources: output: {0}", posts);
        return posts;
    }

    /**
     * Looks for the post with id received in the URL y returns it.
     *
     * @param postsId Id from wanted post. Must be a sequence of digits.
     * @return JSON {@link PostDetailDTO} - Wanted post DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{postsId: \\d+}")
    public PostDetailDTO getPost(@PathParam("postsId") Long postsId) {
        LOGGER.log(Level.INFO, "Looking for  post from resource: input: {0}", postsId);
        PostEntity postEntity = postLogic.getPost(postsId);
        if (postEntity == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(postEntity);
        LOGGER.log(Level.INFO, "Ended looking for post from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates post with id from URL with the information contained in request
     * body.
     *
     * @param postId ID from post to be updated. Must be a sequence of digits.
     * @param post {@link PostDetailDTO} Resident to be updated.
     * @return JSON {@link PostDetailDTO} - Updated post
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{postsId: \\d+}")
    public PostDetailDTO updatePost(@PathParam("postsId") Long postsId, PostDetailDTO post) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating post from resource: input: authorsId: {0} , author: {1}", new Object[]{postsId, post});
        post.setId(postsId);
        if (postLogic.getPost(postsId) == null) {
             throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO detailDTO = new PostDetailDTO(postLogic.updatePost(post.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating post from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the post with the associated id received in URL
     *
     * @param postsId id from post to be deleted
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{postsId: \\d+}")
    public void deletePost(@PathParam("postsId") Long postsId) {
        LOGGER.log(Level.INFO, "Deleting post from resource: input: {0}", postsId);
        if (postLogic.getPost(postsId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        postLogic.deletePost(postsId);
        LOGGER.info("Post deleted from resource: output: void");
    }

    /**
     *
     * Connects /posts route with /members route which are dependent of post
     * resource, by redirecting to the service managing the URL segment in
     * charge of the members
     *
     * @param postId id from post from which the resource is being accessed
     * @return members resource from the specified post
     */

    @Path("{postsId: \\d+}/author")
    public Class<PostResidentProfileResource> getPostResidentProfileResource(@PathParam("postsId") Long postsId) {
        if (postLogic.getPost(postsId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        return PostResidentProfileResource.class;
    }

    /**
     * Converts an entity list to a DTO list for posts.
     *
     * @param entityList Resident entity list to be converted.
     * @return Resident DTO list
     */
    private List<PostDetailDTO> listEntity2DTO(List<PostEntity> entityList) {
        List<PostDetailDTO> list = new ArrayList<>();
        for (PostEntity entity : entityList) {
            list.add(new PostDetailDTO(entity));
        }
        return list;
    }
}