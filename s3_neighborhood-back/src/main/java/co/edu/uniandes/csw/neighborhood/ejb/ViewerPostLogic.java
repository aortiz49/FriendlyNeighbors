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
     * Associates an post with a viewer
     *
     * @param viewerId ID from viewer entity
     * @param postId ID from post entity
     * @return associated post entity
     */
    public PostEntity associatePostToViewer(Long viewerId, Long postId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to associate post with viewer with id = {0}", viewerId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId);
        PostEntity postEntity = postPersistence.find(postId);

        if (viewerEntity.getNeighborhood().getId() != postEntity.getAuthor().getNeighborhood().getId()) {
            throw new BusinessLogicException("Viewer and post must belong to the same neighborhood");
        }

        postEntity.getViewers().add(viewerEntity);

        LOGGER.log(Level.INFO, "Post is associated with viewer with id = {0}", viewerId);
        return postPersistence.find(postId);
    }

    /**
     * Gets a collection of post entities associated with a viewer
     *
     * @param viewerId ID from viewer entity
     * @return collection of post entities associated with a viewer
     */
    public List<PostEntity> getPosts(Long viewerId) {
        LOGGER.log(Level.INFO, "Gets all posts belonging to viewer with id = {0}", viewerId);
        return viewerPersistence.find(viewerId).getPostsToView();
    }

    /**
     * Gets an post entity associated with a a viewer
     *
     * @param viewerId Id from viewer
     * @param postId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If post is not associated
     */
    public PostEntity getPost(Long viewerId, Long postId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding post with id = {0} from viewer with = " + viewerId, postId);
        List<PostEntity> posts = viewerPersistence.find(viewerId).getPostsToView();
        PostEntity postPosts = postPersistence.find(postId);
        int index = posts.indexOf(postPosts);
        LOGGER.log(Level.INFO, "Finish query about post with id = {0} from viewer with = " + viewerId, postId);
        if (index >= 0) {
            return posts.get(index);
        }
        throw new BusinessLogicException("There is no association between viewer and post");
    }

    /**
     * Replaces posts associated with a viewer
     *
     * @param viewerId Id from viewer
     * @param posts Collection of post to associate with viewer
     * @return A new collection associated to viewer
     */
    public List<PostEntity> replacePosts(Long viewerId, List<PostEntity> posts) {
        LOGGER.log(Level.INFO, "Trying to replace posts related to viewer with id = {0}", viewerId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId);
        List<PostEntity> postList = postPersistence.findAll();
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
        LOGGER.log(Level.INFO, "Ended trying to replace posts related to viewer with id = {0}", viewerId);
        return viewerEntity.getPostsToView();
    }

    /**
     * Unlinks an post from a viewer
     *
     * @param viewerId Id from viewer
     * @param postId Id from post
     */
    public void removePost(Long viewerId, Long postId) {
        LOGGER.log(Level.INFO, "Trying to delete an post from viewer with id = {0}", viewerId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId);
        PostEntity postEntity = postPersistence.find(postId);
        postEntity.getViewers().remove(viewerEntity);
        LOGGER.log(Level.INFO, "Finished removing an post from viewer with id = {0}", viewerId);
    }
}
