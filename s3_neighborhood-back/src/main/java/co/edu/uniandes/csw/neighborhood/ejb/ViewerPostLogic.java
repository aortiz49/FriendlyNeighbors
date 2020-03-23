
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author albayona
 */
@Stateless
public class ViewerPostLogic {

    private static final Logger LOGGER = Logger.getLogger(ViewerPostLogic.class.getName());

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private ResidentProfilePersistence viewerPersistence;

    /**
     * Associates post with viewer
     *
     * @param neighId parent neighborhood
     * @param viewerId id from viewer entity
     * @param postId id from post
     * @return associated post
     */
    public PostEntity associatePostToViewer(Long viewerId, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between post with id {0} and  viewer with id {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId, neighId);
        PostEntity postEntity = postPersistence.find(postId, neighId);

        postEntity.getViewers().add(viewerEntity);

        LOGGER.log(Level.INFO, "Association created between post with id {0} and  viewer with id  {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
        return postPersistence.find(postId, neighId);
    }

    /**
     * Gets a collection of post entities associated with viewer
     *
     * @param neighId parent neighborhood
     * @param viewerId id from viewer entity
     * @return collection of post entities associated with viewer
     */
    public List<PostEntity> getPosts(Long viewerId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all posts belonging to viewer with id {0} from neighborhood {1}", new Object[]{viewerId, neighId});
        return viewerPersistence.find(viewerId, neighId).getPostsToView();
    }

    /**
     * Gets post associated with viewer
     *
     * @param neighId parent neighborhood
     * @param viewerId id from viewer
     * @param postId id from associated entity
     * @return associated post
     * @throws BusinessLogicException If post is not associated
     */
    public PostEntity getPost(Long viewerId, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about post with id {0} from viewer with {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
        List<PostEntity> posts = viewerPersistence.find(viewerId, neighId).getPostsToView();
        PostEntity postPosts = postPersistence.find(postId, neighId);
        int index = posts.indexOf(postPosts);
        LOGGER.log(Level.INFO, "Finish query about post with id {0} from viewer with {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
        if (index >= 0) {
            return posts.get(index);
        }
        throw new BusinessLogicException("There is no association between viewer and post");
    }

    /**
     * Replaces posts associated with viewer
     *
     * @param neighId parent neighborhood
     * @param viewerId id from viewer
     * @param posts collection of post to associate with viewer
     * @return A new collection associated to viewer
     */
    public List<PostEntity> replacePosts(Long viewerId, List<PostEntity> posts, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace posts related to viewer with id {0} from neighborhood {1}", new Object[]{viewerId, neighId});
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId, neighId);
        List<PostEntity> postList = postPersistence.findAll(neighId);
        for (PostEntity post : postList) {
            if (posts.contains(post)) {
                if (!post.getViewers().contains(viewerEntity)) {
                    post.getViewers().add(viewerEntity);
                }
            } else {
                post.getViewers().remove(viewerEntity);
            }
        }
        viewerEntity.setPostsToView(posts);
        LOGGER.log(Level.INFO, "Ended trying to replace posts related to viewer with id {0} from neighborhood {1}", new Object[]{viewerId, neighId});
        return viewerEntity.getPostsToView();
    }

    /**
     * Unlinks post from viewer
     *
     * @param neighId parent neighborhood
     * @param viewerId Id from viewer
     * @param postId Id from post
     */
    public void removePost(Long viewerId, Long postId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between post with id {0} and  viewer with id {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId, neighId);
        PostEntity postEntity = postPersistence.find(postId, neighId);
        postEntity.getViewers().remove(viewerEntity);
        LOGGER.log(Level.INFO, "Association deleted between post with id {0} and  viewer with id {1}, from neighbothood {2}", new Object[]{postId, viewerId, neighId});
    }
}
