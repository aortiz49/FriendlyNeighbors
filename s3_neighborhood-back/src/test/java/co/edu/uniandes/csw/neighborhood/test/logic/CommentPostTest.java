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
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.CommentLogic;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
import co.edu.uniandes.csw.neighborhood.ejb.CommentPostLogic;
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.PostPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * @author albayona
 */
@RunWith(Arquillian.class)
public class CommentPostTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private CommentPostLogic postCommentLogic;

    @Inject
    private CommentLogic commentLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private NeighborhoodEntity neighborhood;

    private ResidentProfileEntity resident;

    private PostEntity post;

    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    private List<PostEntity> postData = new ArrayList<PostEntity>();

    private List<CommentEntity> commentsData = new ArrayList();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(PostEntity.class.getPackage())
                .addPackage(PostLogic.class.getPackage())
                .addPackage(PostPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Initial test configuration.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Clears tables involved in tests
     */
    private void clearData() {
        em.createQuery("delete from CommentEntity").executeUpdate();
        em.createQuery("delete from PostEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() throws BusinessLogicException {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhoodPersistence.create(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        resident.setNeighborhood(neighborhood);

        residentPersistence.create(resident);

        post = factory.manufacturePojo(PostEntity.class);
        post.setAuthor(resident);

        postPersistence.create(post);

        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);

            entity.setAuthor(resident);

            em.persist(entity);
            postData.add(entity);
        }

        for (int i = 0; i < 3; i++) {
            CommentEntity comment = factory.manufacturePojo(CommentEntity.class);

            if (i == 0) {
                commentLogic.createComment(comment, postData.get(0).getId(), resident.getId(),  neighborhood.getId());
            } else {
                commentLogic.createComment(comment, postData.get(i).getId(), resident.getId(), neighborhood.getId());
            }

            commentsData.add(comment);
        }

    }

    /**
     * Test for getting  collection of comment entities associated with  post
     */
    @Test
    public void getCommentsTest() {
        List<CommentEntity> list = postCommentLogic.getComments(postData.get(0).getId(), neighborhood.getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting  comment entity associated with  post
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getCommentTest() throws BusinessLogicException {
        PostEntity entity = postData.get(0);
        CommentEntity commentEntity = commentsData.get(0);
        CommentEntity response = postCommentLogic.getComment(entity.getId(), commentEntity.getId(), neighborhood.getId());

        Assert.assertEquals(commentEntity.getId(), response.getId());
        Assert.assertEquals(commentEntity.getAuthor(), response.getAuthor());

    }

    /**
     * Test for getting  comment from non-author user
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedCommentTest() throws BusinessLogicException {
        PostEntity entity = postData.get(0);
        CommentEntity commentEntity = commentsData.get(1);
        postCommentLogic.getComment(entity.getId(), commentEntity.getId(), neighborhood.getId());
    }

    /**
     * Test for replacing comments associated with  post
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceCommentsTest() throws BusinessLogicException {
        List<CommentEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CommentEntity entity = factory.manufacturePojo(CommentEntity.class);

            commentLogic.createComment(entity, postData.get(0).getId(), resident.getId(), neighborhood.getId());

            newCollection.add(entity);
        }
        postCommentLogic.replaceComments(postData.get(0).getId(), newCollection, neighborhood.getId());
        List<CommentEntity> comments = postCommentLogic.getComments(postData.get(0).getId(), neighborhood.getId());
        for (CommentEntity newE : newCollection) {
            Assert.assertTrue(comments.contains(newE));
        }
    }

    /**
     * Test for removing  comment from post
     *
     */
    @Test
    public void removeCommentTest() throws BusinessLogicException {

        postCommentLogic.removeComment(postData.get(0).getId(), commentsData.get(0).getId(), neighborhood.getId());

        Assert.assertTrue(postCommentLogic.getComments(postData.get(0).getId(), neighborhood.getId()).isEmpty());
    }

}
