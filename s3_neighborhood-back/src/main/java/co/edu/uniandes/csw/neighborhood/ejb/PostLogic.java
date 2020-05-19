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

//===================================================
// Imports
//===================================================
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that implements the connection with the post persistence for the Post
 * entity.
 *
 * @author albayona
 */
@Stateless
public class PostLogic {

//===================================================
// Attributes
//===================================================
    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(PostLogic.class.getName());

    /**
     * The injected post persistence object.
     */
    @Inject
    private PostPersistence persistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    /**
     * Creates a post
     *
     * @param postEntity post entity to be created
     * @param residentId author
     * @param neighId parent neighborhood
     * @return created entity
     * @throws BusinessLogicException if business rules are not met
     */
    public PostEntity createPost(PostEntity postEntity, Long residentId, Long neighId) throws BusinessLogicException {

        ResidentProfileEntity r = residentPersistence.find(residentId, neighId);

        LOGGER.log(Level.INFO, "Creation process for post has started");

        //must have a title
        if (postEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (postEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a date
        if (postEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A publish date has to be specified");
        }

        postEntity.setAuthor(r);

        persistence.create(postEntity);
        LOGGER.log(Level.INFO, "Creation process for post eneded");

        return postEntity;
    }

    /**
     * Deletes a post by ID from neighborhood
     *
     * @param neighID parent neighborhood
     * @param id of post to be deleted
     */
    public void deletePost(Long id, Long neighID) {

        LOGGER.log(Level.INFO, "Starting deleting process for post with id = {0}", id);
        persistence.delete(id, neighID);
        LOGGER.log(Level.INFO, "Ended deleting process for post with id = {0}", id);
    }

    /**
     * Get all post entities from neighborhood
     *
     * @param neighID parent neighborhood
     * @return all of post entities
     */
    public List<PostEntity> getPosts(Long neighID) {

        LOGGER.log(Level.INFO, "Starting querying process for all posts");
        List<PostEntity> residents = persistence.findAll(neighID);
        LOGGER.log(Level.INFO, "Ended querying process for all posts");
        return residents;
    }

    /**
     * Gets a post by id from neighborhood
     *
     * @param neighID parent neighborhood
     * @param id from entity post
     * @return entity post found
     */
    public PostEntity getPost(Long id, Long neighID) {
        LOGGER.log(Level.INFO, "Starting querying process for post with id {0}", id);
        PostEntity resident = persistence.find(id, neighID);
        LOGGER.log(Level.INFO, "Ended querying process for  post with id {0}", id);
        return resident;
    }

    /**
     * Updates a post from neighborhood
     *
     * @param neighID parent neighborhood
     * @param postEntity to be updated
     * @return the entity with the updated post
     */
    public PostEntity updatePost(PostEntity postEntity, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for post with id {0}", postEntity.getId());

        //must have a title
        if (postEntity.getTitle() == null) {
            throw new BusinessLogicException("A title has to be specified");
        }

        //must have a description
        if (postEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }

        //must have a date
        if (postEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A publish date has to be specified");
        }

        PostEntity original = persistence.find(postEntity.getId(), neighID);
        postEntity.setAuthor(original.getAuthor());

        PostEntity modified = persistence.update(postEntity, neighID);
        LOGGER.log(Level.INFO, "Ended update process for post with id {0}", postEntity.getId());
        return modified;
    }

    public List<ResidentProfileEntity> getPotentialViewers(Long postID, Long neighID) {

        List<ResidentProfileEntity> s1 = residentPersistence.findAll(neighID);
        List<ResidentProfileEntity> s2 = getPost(postID, neighID).getViewers();
        s1.removeAll(s2);

        return s1;

    }

}
