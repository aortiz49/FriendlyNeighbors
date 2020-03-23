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
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.CommentPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author albayona
 */
@Stateless
public class CommentResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(CommentResidentProfileLogic.class.getName());

    @Inject
    private CommentPersistence commentPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;


    /**
     * /**
     * Gets a collection of comments entities associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId ID from resident entity
     * @return collection of comment entities associated with  resident
     */
    public List<CommentEntity> getComments(Long residentId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all comments belonging to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return residentPersistence.find(residentId, neighId).getComments();
    }

    /**
     * Gets a comment entity associated with  resident
     *
     * @param residentId Id from resident
     * @param neighId ID from parent neighborhood
     * @param commentId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If comment is not associated
     */
    public CommentEntity getComment(Long residentId, Long commentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding comment with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{commentId, residentId, neighId});
        List<CommentEntity> comments = residentPersistence.find(residentId, neighId).getComments();
        CommentEntity CommentEntity = commentPersistence.find(commentId, neighId);
        int index = comments.indexOf(CommentEntity);
        LOGGER.log(Level.INFO, "Found comment with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{commentId, residentId, neighId});
        if (index >= 0) {
            return comments.get(index);
        }
        throw new BusinessLogicException("Comment is not associated with resident");
    }

    /**
     * Replaces comments associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId Id from resident
     * @param comments Collection of comment to associate with resident
     * @return A new collection associated to resident
     */
    public List<CommentEntity> replaceComments(Long residentId, List<CommentEntity> comments, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace comments related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        ResidentProfileEntity resident = residentPersistence.find(residentId, neighId);
        List<CommentEntity> commentList = commentPersistence.findAll(neighId);
        for (CommentEntity comment : commentList) {
            if (comments.contains(comment)) {
                comment.setAuthor(resident);
            } else if (comment.getAuthor() != null && comment.getAuthor().equals(resident)) {
                comment.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced comments related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return comments;
    }

    /**
     * Removes a comment from resident. Comment is no longer in DB
     *
     * @param residentID Id from resident
     * @param neighId ID from parent neighborhood
     * @param commentId Id from comment
     */
    public void removeComment(Long residentID, Long commentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting comment with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{commentId, residentID, neighId});

        commentPersistence.delete(getComment(residentID, commentId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted comment with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{commentId, residentID, neighId});
    }
}
