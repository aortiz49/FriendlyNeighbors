/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
public class PostGroupLogic {

    private static final Logger LOGGER = Logger.getLogger(PostLogic.class.getName());

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private GroupPersistence groupPersistence;

    /**
     * Associates post with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @param postId id from post
     * @return associated post
     */
    public PostEntity associatePostToGroup(Long groupId, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between post with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        PostEntity postEntity = postPersistence.find(postId, neighId);

        postEntity.setGroup(groupEntity);

        LOGGER.log(Level.INFO, "Association created between post with id {0} and group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
        return postPersistence.find(postId, neighId);
    }

    /**
     * Gets a collection of post entities associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @return collection of post entities associated with group
     */
    public List<PostEntity> getPosts(Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all posts belonging to group with id = {0} from neighborhood {1}", new Object[]{groupId, neighId});
        return groupPersistence.find(groupId, neighId).getPosts();
    }

    /**
     * Gets post associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group
     * @param postId id from associated entity
     * @return associated post
     * @throws BusinessLogicException If post is not associated
     */
    public PostEntity getPost(Long groupId, Long postId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about post with id {0} from group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
        List<PostEntity> posts = groupPersistence.find(groupId, neighId).getPosts();
        PostEntity postPosts = postPersistence.find(postId, neighId);
        int index = posts.indexOf(postPosts);
        LOGGER.log(Level.INFO, "Finish query about post with id {0} from group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
        if (index >= 0) {
            return posts.get(index);
        }
        throw new BusinessLogicException("There is no association between post and group");
    }



    /**
     * Unlinks post from group
     *
     * @param neighId parent neighborhood
     * @param groupId Id from group
     * @param postId Id from post
     */
    public void removePost(Long groupId, Long postId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between post with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
        PostEntity postEntity = postPersistence.find(postId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
         postEntity.setGroup(null);
        LOGGER.log(Level.INFO, "Association deleted between post with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{postId, groupId, neighId});
    }

}
