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
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
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
public class CommentPostLogic {

    private static final Logger LOGGER = Logger.getLogger(CommentPostLogic.class.getName());

    @Inject
    private CommentPersistence commentPersistence;

    @Inject
    private PostPersistence postPersistence;


    /**
     * /**
     * Gets a collection of comments entities associated with  post
     *
     * @param neighId ID from parent neighborhood
     * @param postId ID from post entity
     * @return collection of comment entities associated with  post
     */
    public List<CommentEntity> getComments(Long postId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all comments belonging to post with id {0} from neighborhood {1}", new Object[]{postId, neighId});
        return postPersistence.find(postId, neighId).getComments();
    }

    /**
     * Gets a comment entity associated with  post
     *
     * @param postId Id from post
     * @param neighId ID from parent neighborhood
     * @param commentId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If comment is not associated
     */
    public CommentEntity getComment(Long postId, Long commentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding comment with id {0} associated to post with id {1}, from neighbothood {2}", new Object[]{commentId, postId, neighId});
        List<CommentEntity> comments = postPersistence.find(postId, neighId).getComments();
        CommentEntity CommentEntity = commentPersistence.find(commentId, neighId);
        int index = comments.indexOf(CommentEntity);
        LOGGER.log(Level.INFO, "Found comment with id {0} associated to post with id {1}, from neighbothood {2}", new Object[]{commentId, postId, neighId});
        if (index >= 0) {
            return comments.get(index);
        }
        throw new BusinessLogicException("Comment is not associated with post");
    }

    /**
     * Replaces comments associated with  post
     *
     * @param neighId ID from parent neighborhood
     * @param postId Id from post
     * @param comments Collection of comment to associate with post
     * @return A new collection associated to post
     */
    public List<CommentEntity> replaceComments(Long postId, List<CommentEntity> comments, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace comments related to post with id {0} from neighborhood {1}", new Object[]{postId, neighId});
        PostEntity post = postPersistence.find(postId, neighId);
        List<CommentEntity> commentList = commentPersistence.findAll(neighId);
        for (CommentEntity comment : commentList) {
            if (comments.contains(comment)) {
                comment.setPost(post);
            } else if (comment.getAuthor() != null && comment.getAuthor().equals(post)) {
                comment.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced comments related to post with id {0} from neighborhood {1}", new Object[]{postId, neighId});
        return comments;
    }

    /**
     * Removes a comment from post. Comment is no longer in DB
     *
     * @param postID Id from post
     * @param neighId ID from parent neighborhood
     * @param commentId Id from comment
     */
    public void removeComment(Long postID, Long commentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting comment with id {0} associated to post with id {1}, from neighbothood {2}", new Object[]{commentId, postID, neighId});

        commentPersistence.delete(getComment(postID, commentId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted comment with id {0} associated to post with id {1}, from neighbothood {2}", new Object[]{commentId, postID, neighId});
    }
}
