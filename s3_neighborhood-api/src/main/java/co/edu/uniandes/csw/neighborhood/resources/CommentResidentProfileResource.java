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
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.ejb.CommentLogic;
import co.edu.uniandes.csw.neighborhood.ejb.CommentResidentProfileLogic;

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
 * Class implementing resource "residents/{id}/comments".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResidentProfileResource {

    private static final Logger LOGGER = Logger.getLogger(CommentResidentProfileResource.class.getName());

    @Inject
    private CommentResidentProfileLogic residentCommentLogic;

    @Inject
    private CommentLogic commentLogic;



    /**
     * Looks for all the comments associated to a resident and returns it
     *
     * @param residentsId id from resident whose comments are wanted
     * @return JSONArray {@link CommentDTO} - comments found in resident. An
     * empty list if none is found
     */
    @GET
    public List<CommentDTO> getComments(@PathParam("residentsId") Long residentsId) {
        LOGGER.log(Level.INFO, "Looking for comments from resources: input: {0}", residentsId);
        List<CommentDTO> list = commentsListEntity2DTO(residentCommentLogic.getComments(residentsId));
        LOGGER.log(Level.INFO, "Ended looking for comments from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a comment with specified ID by URL which is associated with a
     * resident and returns it
     *
     * @param commentsId id from wanted comment
     * @param residentsId id from resident whose comment is wanted
     * @return {@link CommentDTO} - comment found inside resident
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if comment not found
     */
    @GET
    @Path("{commentsId: \\d+}")
    public CommentDTO getComment(@PathParam("residentsId") Long residentsId, @PathParam("commentsId") Long commentsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for comment: input: residentsId {0} , commentsId {1}", new Object[]{residentsId, commentsId});
        if (commentLogic.getComment(commentsId) == null) {
            throw new WebApplicationException("Resource /comments/" + commentsId + " does not exist.", 404);
        }
        CommentDTO detailDTO = new CommentDTO(residentCommentLogic.getComment(residentsId, commentsId));
        LOGGER.log(Level.INFO, "Ended looking for comment: output: {0}", detailDTO);
        return detailDTO;
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

}
