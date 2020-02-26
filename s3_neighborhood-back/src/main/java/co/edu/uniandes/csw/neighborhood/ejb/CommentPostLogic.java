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
     * Associates a comment with a post
     *
     * @param postId ID from post entity
     * @param commentId ID from comment entity
     * @return associated comment entity
     */
    public CommentEntity associateCommentToResident(Long commentId, Long postId) {
       LOGGER.log(Level.INFO, "Trying to add comment to post with id = {0}", postId);
        PostEntity PostEntity = postPersistence.find(postId);
        CommentEntity CommentEntity = commentPersistence.find(commentId);
        CommentEntity.setPost(PostEntity);
      LOGGER.log(Level.INFO, "Comment is associated with post with id = {0}", postId);
        return CommentEntity;
    }

    /**
    /**
     * Gets a collection of comments entities associated with a post 
     * @param postId ID from post entity
     * @return collection of comment entities associated with a post 
     */
    public List<CommentEntity> getComments(Long postId) {
          LOGGER.log(Level.INFO, "Gets all comments belonging to post with id = {0}", postId);
        return postPersistence.find(postId).getComments();
    }

 /**
     * Gets a comment entity associated with  a post
     *
     * @param postId Id from post
     * @param commentId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated 
     */
    public CommentEntity getComment(Long postId, Long commentId) throws BusinessLogicException {
              LOGGER.log(Level.INFO, "Finding event with id = {0} from post with = " + postId, commentId);
        List<CommentEntity> comments = postPersistence.find(postId).getComments();
        CommentEntity CommentEntity = commentPersistence.find(commentId);
        int index = comments.indexOf(CommentEntity);
            LOGGER.log(Level.INFO, "Finish query about event with id = {0} from post with = " + postId, commentId);
        if (index >= 0) {
            return comments.get(index);
        }
        throw new BusinessLogicException("Comment is no associated with post");
    }


  /**
     * Replaces comments associated with a post
     *
     * @param postId Id from post
     * @param comments Collection of comment to associate with post
     * @return A new collection associated to post
     */
    public List<CommentEntity> replaceComments(Long postId, List<CommentEntity> comments) {
       LOGGER.log(Level.INFO, "Trying to replace comments related to post con id = {0}", postId);
        PostEntity post = postPersistence.find(postId);
        List<CommentEntity> commentList = commentPersistence.findAll();
        for (CommentEntity comment : commentList) {
            if (comments.contains(comment)) {
                comment.setPost(post);
            } else if (comment.getAuthor()!= null && comment.getAuthor().equals(post)) {
                comment.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Ended trying to replace comments related to post con id = {0}", postId);
        return comments;
    }
    
    /**
     * Removes a comment from a post. Comment is no longer in DB
     *
     * @param postId Id from post
     * @param eventId Id from comment     
     */
    public void removeComment(Long postId, Long eventId) {
         LOGGER.log(Level.INFO, "Trying to delete a comment from post con id = {0}", postId);
      commentPersistence.delete(postId);

       LOGGER.log(Level.INFO, "Finished removing a comment from post con id = {0}", postId);
        }
}
