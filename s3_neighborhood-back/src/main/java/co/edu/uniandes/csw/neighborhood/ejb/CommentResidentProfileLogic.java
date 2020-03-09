/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.ejb;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.CommentPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between resident and comment.
 *
 * @author aortiz49
 */
@Stateless
public class CommentResidentProfileLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(ResidentProfileNeighborhoodLogic.class.getName());

    /**
     * Dependency injection for resident persistence.
     */
    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Dependency injection for comment persistence.
     */
    @Inject
    private CommentPersistence commentPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Comment to a ResidentProfile.
     *
     * @param pCommentId the comment id
     * @param pResidentProfileId the resident id
     * @return the comment instance that was associated to the resident
     * @throws BusinessLogicException when the neighborhood or resident don't exist
     */
    public CommentEntity addCommentToResidentProfile(Long pCommentId, Long pResidentProfileId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between comment and resident with id = {0}", pResidentProfileId);

        // finds existing resident
        ResidentProfileEntity residentEntity = residentPersistence.find(pResidentProfileId);

        // resident must exist
        if (residentEntity == null) {
            throw new BusinessLogicException("The neighborhood must exist.");
        }

        // finds existing comment
        CommentEntity commentEntity = commentPersistence.find(pCommentId);

        // comment must exist
        if (commentEntity == null) {
            throw new BusinessLogicException("The comment must exist.");
        }

        // set author of the comment
        commentEntity.setAuthor(residentEntity);

        // add the comment to the author
        residentEntity.getComments().add(commentEntity);

        LOGGER.log(Level.INFO, "End association between comment and resident with id = {0}", pResidentProfileId);
        return commentEntity;
    }

    /**
     * Gets a collection of comment entities associated with a resident
     *
     * @param pResidentProfileId the neighborhood id
     * @return collection of comment entities associated with a resident
     */
    public List<CommentEntity> getComments(Long pResidentProfileId) {
        LOGGER.log(Level.INFO, "Gets all comments belonging to resident with id = {0}", pResidentProfileId);

        // returns the list of all comment
        return residentPersistence.find(pResidentProfileId).getComments();
    }

    /**
     * Gets a comment entity associated with a resident
     *
     * @param pCommentId the comment id
     * @param pResidentProfileId the resident id
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public CommentEntity getComment(Long pCommentId, Long pResidentProfileId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding comment with id = {0} from resident with = " + pCommentId, pResidentProfileId);

        // gets all the comments in a neighborhood
        List<CommentEntity> comments = residentPersistence.find(pResidentProfileId).getComments();

        // the comment that was found
        int index = comments.indexOf(commentPersistence.find(pCommentId));

        // logs end
        LOGGER.log(Level.INFO, "Finish finding comment with id = {0} from resident with = " + pCommentId, pResidentProfileId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("ResidentProfile is not associated with the neighborhood");
        }

        return comments.get(index);
    }

    /**
     * Removes a comment from a resident.
     *
     * @param pCommentId the comment id
     * @param pResidentProfileId the resident id
     */
    public void removeComment(Long pCommentId, Long pResidentProfileId) {
        // gets the first resident from the list. 
         LOGGER.log(Level.INFO, "Start removing comment from resident with id = {0}", pCommentId);

        // desired resident
        ResidentProfileEntity resident = residentPersistence.find(pResidentProfileId);

        // comment to delete
        CommentEntity comment = commentPersistence.find(pCommentId);

        // remove comment from resident
        resident.getComments().remove(comment);
        
        // set comment's resident author to null 
        comment.setAuthor(null);

        LOGGER.log(Level.INFO, "Finished removing an event from group con id = {0}", pCommentId);

    }
}
