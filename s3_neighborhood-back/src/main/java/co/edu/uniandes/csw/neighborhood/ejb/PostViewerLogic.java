/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 *
 * @author albayona
 */
@Stateless
public class PostViewerLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfileLogic.class.getName());

    @Inject
    private ResidentProfilePersistence viewerPersistence;

    @Inject
    private PostPersistence postPersistence;

    /**
     * Associates viewer with post
     *
     * @param neighId parent neighborhood
     * @param postId id from post entity
     * @param viewerId id from viewer
     * @return associated viewer
     */
    public ResidentProfileEntity associateViewerToPost(Long postId, Long viewerId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between viewer with id {0} and  post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
        PostEntity postEntity = postPersistence.find(postId, neighId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId, neighId);

        postEntity.getViewers().add(viewerEntity);

        LOGGER.log(Level.INFO, "Association created between viewer with id {0} and post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
        return viewerPersistence.find(viewerId, neighId);
    }

    /**
     * Gets a collection of viewer entities associated with post
     *
     * @param neighId parent neighborhood
     * @param postId id from post entity
     * @return collection of viewer entities associated with post
     */
    public List<ResidentProfileEntity> getViewers(Long postId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all viewers belonging to post with id = {0} from neighborhood {1}", new Object[]{postId, neighId});
        return postPersistence.find(postId, neighId).getViewers();
    }

    /**
     * Gets viewer associated with post
     *
     * @param neighId parent neighborhood
     * @param postId id from post
     * @param viewerId id from associated entity
     * @return associated viewer
     * @throws BusinessLogicException If viewer is not associated
     */
    public ResidentProfileEntity getViewer(Long postId, Long viewerId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about viewer with id {0} from post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
        List<ResidentProfileEntity> viewers = postPersistence.find(postId, neighId).getViewers();
        ResidentProfileEntity viewerResidentProfiles = viewerPersistence.find(viewerId, neighId);
        int index = viewers.indexOf(viewerResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about viewer with id {0} from post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
        if (index >= 0) {
            return viewers.get(index);
        }
        throw new BusinessLogicException("There is no association between viewer and post");
    }

    /**
     * Replaces viewers associated with post
     *
     * @param neighId parent neighborhood
     * @param postId id from post
     * @param viewers collection of viewer to associate with post
     * @return A new collection associated to post
     */
    public List<ResidentProfileEntity> replaceViewers(Long postId, List<ResidentProfileEntity> viewers, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace viewers related to post with id {0} from neighborhood {1}", new Object[]{postId, neighId});
        PostEntity postEntity = postPersistence.find(postId, neighId);
        List<ResidentProfileEntity> viewerList = viewerPersistence.findAll(neighId);
        for (ResidentProfileEntity viewer : viewerList) {
            if (viewers.contains(viewer)) {
                if (!viewer.getPostsToView().contains(postEntity)) {
                    viewer.getPostsToView().add(postEntity);
                }
            } else {
                viewer.getPostsToView().remove(postEntity);
            }
        }
        postEntity.setViewers(viewers);
        LOGGER.log(Level.INFO, "Ended trying to replace viewers related to post with id {0} from neighborhood {1}", new Object[]{postId, neighId});
        return postEntity.getViewers();
    }

    /**
     * Unlinks viewer from post
     *
     * @param neighId parent neighborhood
     * @param postId Id from post
     * @param viewerId Id from viewer
     */
    public void removeViewer(Long postId, Long viewerId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between viewer with id {0} and  post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId, neighId);
        PostEntity postEntity = postPersistence.find(postId, neighId);
        postEntity.getViewers().remove(viewerEntity);
        LOGGER.log(Level.INFO, "Association deleted between viewer with id {0} and  post with id {1}, from neighbothood {2}", new Object[]{viewerId, postId, neighId});
    }

    public List<ResidentProfileEntity> getPotentialViewers(Long postID, Long neighID) {

        List<ResidentProfileEntity> s1 = viewerPersistence.findAll(neighID);
        List<ResidentProfileEntity> s2 = postPersistence.find(postID, neighID).getViewers();
        s1.removeAll(s2);

        return s1;

    }

}
