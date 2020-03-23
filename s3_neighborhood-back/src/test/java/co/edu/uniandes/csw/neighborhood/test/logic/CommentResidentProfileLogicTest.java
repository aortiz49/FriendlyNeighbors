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
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.CommentResidentProfileLogic;
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
public class CommentResidentProfileLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private CommentResidentProfileLogic residentCommentLogic;

    @Inject
    private CommentLogic commentLogic;

    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    @Inject
    private PostPersistence postPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private NeighborhoodEntity neighborhood;

    private PostEntity post;

    private List<ResidentProfileEntity> residentData = new ArrayList<ResidentProfileEntity>();

    private List<CommentEntity> commentsData = new ArrayList();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
                .addPackage(ResidentProfileLogic.class.getPackage())
                .addPackage(ResidentProfilePersistence.class.getPackage())
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() throws BusinessLogicException {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            entity.setNeighborhood(neighborhood);
            em.persist(entity);
            residentData.add(entity);
        }

        post = factory.manufacturePojo(PostEntity.class);
        post.setAuthor(residentData.get(1));
        postPersistence.create(post);

        for (int i = 0; i < 3; i++) {
            CommentEntity comment = factory.manufacturePojo(CommentEntity.class);

            if (i == 0) {
                commentLogic.createComment(comment, post.getId(), residentData.get(0).getId(), neighborhood.getId());
            } else {
                commentLogic.createComment(comment, post.getId(), residentData.get(i).getId(), neighborhood.getId());
            }

            commentsData.add(comment);
        }

    }

    /**
     * Test for getting  collection of comment entities associated with 
     * resident
     */
    @Test
    public void getCommentsTest() {
        List<CommentEntity> list = residentCommentLogic.getComments(residentData.get(0).getId(), neighborhood.getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting  comment entity associated with  resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getCommentTest() throws BusinessLogicException {
        ResidentProfileEntity entity = residentData.get(0);
        CommentEntity commentEntity = commentsData.get(0);
        CommentEntity response = residentCommentLogic.getComment(entity.getId(), commentEntity.getId(), neighborhood.getId());

        Assert.assertEquals(commentEntity.getId(), response.getId());
    }

    /**
     * Test for getting  comment from non-author user
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedCommentTest() throws BusinessLogicException {
        ResidentProfileEntity entity = residentData.get(0);
        CommentEntity commentEntity = commentsData.get(1);
        residentCommentLogic.getComment(entity.getId(), commentEntity.getId(), neighborhood.getId());
    }

    /**
     * Test for replacing comments associated with  resident
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceCommentsTest() throws BusinessLogicException {
        List<CommentEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CommentEntity entity = factory.manufacturePojo(CommentEntity.class);

        commentLogic.createComment(entity, post.getId(), residentData.get(0).getId(), neighborhood.getId());

            newCollection.add(entity);
        }
        residentCommentLogic.replaceComments(residentData.get(0).getId(), newCollection, neighborhood.getId());
        List<CommentEntity> comments = residentCommentLogic.getComments(residentData.get(0).getId(), neighborhood.getId());
        for (CommentEntity newE : newCollection) {
            Assert.assertTrue(comments.contains(newE));
        }
    }

    /**
     * Test for removing  comment from resident
     *
     */
    @Test
    public void removeCommentTest() throws BusinessLogicException {

        residentCommentLogic.removeComment(residentData.get(0).getId(), commentsData.get(0).getId(), neighborhood.getId());

        Assert.assertTrue(residentCommentLogic.getComments(residentData.get(0).getId(), neighborhood.getId()).isEmpty());
    }

}
