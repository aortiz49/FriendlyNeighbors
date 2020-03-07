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

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/** 
 * @author albayona
 */
@Stateless
public class PostResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(PostResidentProfileLogic.class.getName());

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Associates a post with a resident
     *
     * @param residentId ID from resident entity
     * @param postId ID from post entity
     * @return associated post entity
     */
    public PostEntity associatePostToResident(Long postId, Long residentId) {
       LOGGER.log(Level.INFO, "Trying to add post to resident with id = {0}", residentId);
        ResidentProfileEntity ResidentProfileEntity = residentPersistence.find(residentId);
        PostEntity PostEntity = postPersistence.find(postId);
        PostEntity.setAuthor(ResidentProfileEntity);
      LOGGER.log(Level.INFO, "Post is associated with resident with id = {0}", residentId);
        return PostEntity;
    }

    /**
    /**
     * Gets a collection of posts entities associated with a resident 
     * @param residentId ID from resident entity
     * @return collection of post entities associated with a resident 
     */
    public List<PostEntity> getPosts(Long residentId) {
          LOGGER.log(Level.INFO, "Gets all posts belonging to resident with id = {0}", residentId);
        return residentPersistence.find(residentId).getPosts();
    }

 /**
     * Gets a post entity associated with  a resident
     *
     * @param residentId Id from resident
     * @param postId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated 
     */
    public PostEntity getPost(Long residentId, Long postId) throws BusinessLogicException {
              LOGGER.log(Level.INFO, "Finding event with id = {0} from resident with = " + residentId, postId);
        List<PostEntity> posts = residentPersistence.find(residentId).getPosts();
        PostEntity PostEntity = postPersistence.find(postId);
        int index = posts.indexOf(PostEntity);
            LOGGER.log(Level.INFO, "Finish query about event with id = {0} from resident with = " + residentId, postId);
        if (index >= 0) {
            return posts.get(index);
        }
        throw new BusinessLogicException("Post is no associated with resident");
    }


  /**
     * Replaces posts associated with a resident
     *
     * @param residentId Id from resident
     * @param posts Collection of post to associate with resident
     * @return A new collection associated to resident
     */
    public List<PostEntity> replacePosts(Long residentId, List<PostEntity> posts) {
       LOGGER.log(Level.INFO, "Trying to replace posts related to resident with id = {0}", residentId);
        ResidentProfileEntity resident = residentPersistence.find(residentId);
        List<PostEntity> postList = postPersistence.findAll();
        for (PostEntity post : postList) {
            if (posts.contains(post)) {
                post.setAuthor(resident);
            } else if (post.getAuthor()!= null && post.getAuthor().equals(resident)) {
                post.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Ended trying to replace posts related to resident with id = {0}", residentId);
        return posts;
    }
    
   


   /**
     * Removes a post from a resident. Post is no longer in DB
     *

     * @param postId Id from post     
     */
    public void removePost(Long residentID, Long postId) throws BusinessLogicException {
         LOGGER.log(Level.INFO, "Trying to delete a post from resident with id = {0}", postId);
      postPersistence.delete(getPost(residentID, postId).getId());

       LOGGER.log(Level.INFO, "Finished removing a post from resident with id = {0}", postId);
        }
}
