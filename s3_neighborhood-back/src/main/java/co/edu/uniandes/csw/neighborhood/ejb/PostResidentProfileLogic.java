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
     * /**
     * Gets a collection of posts entities associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId ID from resident entity
     * @return collection of post entities associated with  resident
     */
    public List<PostEntity> getPosts(Long residentId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all posts belonging to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return residentPersistence.find(residentId, neighId).getPosts();
    }

    /**
     * Gets a post entity associated with  resident
     *
     * @param residentId Id from resident
     * @param neighId ID from parent neighborhood
     * @param postId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If post is not associated
     */
    public PostEntity getPost(Long residentId, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding post with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{postId, residentId, neighId});
        List<PostEntity> posts = residentPersistence.find(residentId, neighId).getPosts();
        PostEntity PostEntity = postPersistence.find(postId, neighId);
        int index = posts.indexOf(PostEntity);
        LOGGER.log(Level.INFO, "Found post with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{postId, residentId, neighId});
        if (index >= 0) {
            return posts.get(index);
        }
        throw new BusinessLogicException("Post is not associated with resident");
    }

    /**
     * Replaces posts associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId Id from resident
     * @param posts Collection of post to associate with resident
     * @return A new collection associated to resident
     */
    public List<PostEntity> replacePosts(Long residentId, List<PostEntity> posts, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace posts related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        ResidentProfileEntity resident = residentPersistence.find(residentId, neighId);
        List<PostEntity> postList = postPersistence.findAll(neighId);
        for (PostEntity post : postList) {
            if (posts.contains(post)) {
                post.setAuthor(resident);
            } else if (post.getAuthor() != null && post.getAuthor().equals(resident)) {
                post.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced posts related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return posts;
    }

    /**
     * Removes a post from resident. Post is no longer in DB
     *
     * @param residentID Id from resident
     * @param neighId ID from parent neighborhood
     * @param postId Id from post
     */
    public void removePost(Long residentID, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting post with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{postId, residentID, neighId});

        postPersistence.delete(getPost(residentID, postId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted post with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{postId, residentID, neighId});
    }
}
