/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.CommentDTO;
import co.edu.uniandes.csw.neighborhood.ejb.CommentLogic;
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
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
 * Class implementing resource "comments".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CommentResource {

    private static final Logger LOGGER = Logger.getLogger(CommentResource.class.getName());

    @Inject
    private CommentLogic commentLogic;

    /**
     * Looks for all comments on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link CommentDTO} - All the comments on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<CommentDTO> getComments(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all comments from resources: input: void");
        List<CommentDTO> comments = listEntity2DTO(commentLogic.getComments(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all comments from resources: output: {0}", comments);
        return comments;
    }

    /**
     * Looks for the comment with id received in the URL y returns it.
     *
     * @param commentsId Id from wanted comment. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link CommentDTO} - Wanted comment DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{commentsId: \\d+}")
    public CommentDTO getComment(@PathParam("commentsId") Long commentsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  comment from resource: input: {0}", commentsId);
        CommentEntity commentEntity = commentLogic.getComment(commentsId, neighId);
        if (commentEntity == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        CommentDTO detailDTO = new CommentDTO(commentEntity);
        LOGGER.log(Level.INFO, "Ended looking for comment from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates comment with id from URL with the information contained in
     * request body.
     *
     * @param commentsId ID from comment to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param comment {@link CommentDTO} Comment to be updated.
     * @return JSON {@link CommentDTO} - Updated comment
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{commentsId: \\d+}")
    public CommentDTO updateAuthor(@PathParam("commentsId") Long commentsId, @PathParam("neighborhoodId") Long neighId, CommentDTO comment) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating comment from resource: input: authorsId: {0} , author: {1}", new Object[]{commentsId, comment});
        comment.setId(commentsId);
        if (commentLogic.getComment(commentsId, neighId) == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        CommentDTO detailDTO = new CommentDTO(commentLogic.updateComment(comment.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating comment from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the comment with the associated id received in URL
     *
     * @param commentsId id from comment to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{commentsId: \\d+}")
    public void deleteComment(@PathParam("commentsId") Long commentsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Deleting comment from resource: input: {0}", commentsId);
        if (commentLogic.getComment(commentsId, neighId) == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        commentLogic.deleteComment(commentsId, neighId);
        LOGGER.info("Comment deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for comments.
     *
     * @param entityList Comment entity list to be converted.
     * @return Comment DTO list
     */
    private List<CommentDTO> listEntity2DTO(List<CommentEntity> entityList) {
        List<CommentDTO> list = new ArrayList<>();
        for (CommentEntity entity : entityList) {
            list.add(new CommentDTO(entity));
        }
        return list;
    }


}
