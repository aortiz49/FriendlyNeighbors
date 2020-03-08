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
     * Associates a viewer with an post
     *
     * @param postId ID from post entity
     * @param viewerId ID from viewer
     * @return associated viewer
     */
    public ResidentProfileEntity associateResidentProfileToPost(Long postId, Long viewerId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Trying to add viewer to post with id = {0}", postId);
        PostEntity postEntity = postPersistence.find(postId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId);

        if (viewerEntity.getNeighborhood().getId() != postEntity.getAuthor().getNeighborhood().getId()) {
            throw new BusinessLogicException("Viewer and post must belong to the same neighborhood");
        }
        postEntity.getViewers().add(viewerEntity);

        LOGGER.log(Level.INFO, "Resident is associated with post with id = {0}", postId);
        return viewerPersistence.find(viewerId);
    }

    /**
     * Gets a collection of viewer entities associated with an post
     *
     * @param postId ID from post entity
     * @return collection of viewer entities associated with an post
     */
    public List<ResidentProfileEntity> getResidentProfiles(Long postId) {
        LOGGER.log(Level.INFO, "Gets all viewers belonging to post with id = {0}", postId);
        return postPersistence.find(postId).getViewers();
    }

    /**
     * Gets an viewer associated with an post
     *
     * @param postId Id from post
     * @param viewerId Id from associated entity
     * @return associated viewer
     * @throws BusinessLogicException If viewer is not associated
     */
    public ResidentProfileEntity getResidentProfile(Long postId, Long viewerId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding viewer with id = {0} from post with = " + postId, viewerId);
        List<ResidentProfileEntity> viewers = postPersistence.find(postId).getViewers();
        ResidentProfileEntity viewerResidentProfiles = viewerPersistence.find(viewerId);
        int index = viewers.indexOf(viewerResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about viewer with id = {0} from post with = " + postId, viewerId);
        if (index >= 0) {
            return viewers.get(index);
        }
        throw new BusinessLogicException("There is no association between viewer and post");
    }

    /**
     * Replaces viewers associated with a post
     *
     * @param postId Id from post
     * @param viewers Collection of viewer to associate with post
     * @return A new collection associated to post
     */
    public List<ResidentProfileEntity> replaceResidentProfiles(Long postId, List<ResidentProfileEntity> viewers) {
        LOGGER.log(Level.INFO, "Trying to replace viewers related to post with id = {0}", postId);
        PostEntity postEntity = postPersistence.find(postId);
        List<ResidentProfileEntity> viewerList = viewerPersistence.findAll();
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
        LOGGER.log(Level.INFO, "Ended trying to replace viewers related to post with id = {0}", postId);
        return postEntity.getViewers();
    }

    /**
     * Unlinks an viewer from a post
     *
     * @param postId Id from post
     * @param viewerId Id from viewer
     */
    public void removeResidentProfile(Long postId, Long viewerId) {
        LOGGER.log(Level.INFO, "Trying to delete an viewer from post with id = {0}", postId);
        ResidentProfileEntity viewerEntity = viewerPersistence.find(viewerId);
        PostEntity postEntity = postPersistence.find(postId);
        postEntity.getViewers().remove(viewerEntity);
        LOGGER.log(Level.INFO, "Finished removing an viewer from post with id = {0}", postId);
    }

}
