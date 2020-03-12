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

import co.edu.uniandes.csw.neighborhood.dtos.CommentDTO;
import co.edu.uniandes.csw.neighborhood.dtos.CommentDTO;

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.ejb.CommentLogic;
import co.edu.uniandes.csw.neighborhood.ejb.CommentPostLogic;
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
 * Class implementing resource "post/{commentId}/comments".
 *
 * @post v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentPostResource {

    private static final Logger LOGGER = Logger.getLogger(CommentPostResource.class.getName());

    @Inject
    private CommentPostLogic postCommentLogic;

    @Inject
    private CommentLogic commentLogic;

    /**
     * Creates a comment with an existing post
     *
     * @param commentsId commentId from comment to be associated
     * @param postsId commentId from post
     * @return JSON {@link CommentDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{commentsId: \\d+}")
    public CommentDTO createCommentForPost(@PathParam("postsId") Long postsId, CommentDTO comment) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating comment for post from resource: input: postsId {0} , commentsId {1}", new Object[]{postsId, comment.getId()});

        CommentEntity entity = null;

        entity = commentLogic.createComment(comment.toEntity());
        
        Long commentId = entity.getId();
        
        postCommentLogic.associateCommentToPost(commentId, postsId);

        CommentDTO dto = new CommentDTO(commentLogic.getComment(commentId));
        LOGGER.log(Level.INFO, "Ended creating comment for post from resource: output: {0}", dto.getId());
        return dto;
    }

    /**
     * Looks for all the comments associated to a post and returns it
     *
     * @param postsId commentId from post whose comments are wanted
     * @return JSONArray {@link CommentDTO} - comments found in post. An
     * empty list if none is found
     */
    @GET
    public List<CommentDTO> getComments(@PathParam("postsId") Long postsId) {
        LOGGER.log(Level.INFO, "Looking for comments from resources: input: {0}", postsId);
        List<CommentDTO> list = commentsListEntity2DTO(postCommentLogic.getComments(postsId));
        LOGGER.log(Level.INFO, "Ended looking for comments from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a comment with specified ID by URL which is associated with a
     * post and returns it
     *
     * @param commentsId commentId from wanted comment
     * @param postsId commentId from post whose comment is wanted
     * @return {@link CommentDTO} - comment found inside post
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if comment not found
     */
    @GET
    @Path("{commentsId: \\d+}")
    public CommentDTO getComment(@PathParam("postsId") Long postsId, @PathParam("commentsId") Long commentsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for comment: input: postsId {0} , commentsId {1}", new Object[]{postsId, commentsId});
        if (commentLogic.getComment(commentsId) == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        CommentDTO detailDTO = new CommentDTO(postCommentLogic.getComment(postsId, commentsId));
        LOGGER.log(Level.INFO, "Ended looking for comment: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     *
     * Updates a list from comments inside a post which is received in body
     *
     * @param postsId commentId from post whose list of comments is to be updated
     * @param comments JSONArray {@link CommentDTO} - modified comments list
     * @return JSONArray {@link CommentDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<CommentDTO> replaceComments(@PathParam("postsId") Long postsId, List<CommentDTO> comments) {
        LOGGER.log(Level.INFO, "Replacing post comments from resource: input: postsId {0} , comments {1}", new Object[]{postsId, comments});
        for (CommentDTO comment : comments) {
            if (commentLogic.getComment(comment.getId()) == null) {
                throw new WebApplicationException("Resource /comments/" + comments + " does not exist.", 404);
            }
        }
        List<CommentDTO> lista = commentsListEntity2DTO(postCommentLogic.replaceComments(postsId, commentsListDTO2Entity(comments)));
        LOGGER.log(Level.INFO, "Ended replacing post comments from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a comment from a post
     *
     * @param postsId commentId from post whose comment is to be removed
     * @param commentsId commentId from comment to be removed
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{commentsId: \\d+}")
    public void removeComment(@PathParam("postsId") Long postsId, @PathParam("commentsId") Long commentsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Removing comment from post: input: postsId {0} , commentsId {1}", new Object[]{postsId, commentsId});
        if (commentLogic.getComment(commentsId) == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        postCommentLogic.removeComment(postsId, commentsId);
        LOGGER.info("Ended removing comment from post: output: void");
    }

    /**
     * Converts an entity list with comments to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<CommentDTO> commentsListEntity2DTO(List<CommentEntity> entityList) {
        List<CommentDTO> list = new ArrayList<>();
        for (CommentEntity entity : entityList) {
            list.add(new CommentDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with comments to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<CommentEntity> commentsListDTO2Entity(List<CommentDTO> dtos) {
        List<CommentEntity> list = new ArrayList<>();
        for (CommentDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
