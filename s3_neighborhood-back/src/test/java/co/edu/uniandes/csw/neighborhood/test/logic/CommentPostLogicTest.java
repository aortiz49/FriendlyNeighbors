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
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
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
public class CommentPostLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ResidentProfilePersistence residentPersistence;
    @Inject
    private CommentPostLogic postCommentLogic;
    
    @Inject
    private CommentLogic commentLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<PostEntity> data = new ArrayList<PostEntity>();

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
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            CommentEntity comments = factory.manufacturePojo(CommentEntity.class);
            em.persist(comments);
            commentsData.add(comments);
        }
        
        ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
        residentPersistence.create(resident);
        
      
        for (int i = 0; i < 3; i++) {
            PostEntity entity = factory.manufacturePojo(PostEntity.class);
            entity.setAuthor(resident);
            em.persist(entity);
            data.add(entity);
            if (i == 0) {
                commentsData.get(i).setPost(entity);
            }
        }
    }

    /**
     * Test to associate a comment with a resident 
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addCommentsTest() throws BusinessLogicException {
        PostEntity entity = data.get(0);
        CommentEntity commentEntity = commentsData.get(1);

        
        CommentEntity response = postCommentLogic.associateCommentToPost(commentEntity.getId(), entity.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(commentEntity.getId(), response.getId());
    }

    /**
     * Test for getting a collection of comment entities associated with a resident
     */
    @Test
    public void getCommentsTest() {
        List<CommentEntity> list = postCommentLogic.getComments(data.get(0).getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting a comment entity associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getCommentTest() throws BusinessLogicException {
        PostEntity entity = data.get(0);
        CommentEntity commentEntity = commentsData.get(0);
        CommentEntity response = postCommentLogic.getComment(entity.getId(), commentEntity.getId());

        Assert.assertEquals(commentEntity.getId(), response.getId());
        Assert.assertEquals(commentEntity.getText(), response.getText());

    }

    /**
     * Test for getting a comment from a non-author user 
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedCommentTest() throws BusinessLogicException {
        PostEntity entity = data.get(0);
        CommentEntity commentEntity = commentsData.get(1);
        postCommentLogic.getComment(entity.getId(), commentEntity.getId());
    }
    
    
        /**
     * Test for replacing comments associated with a post
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceCommentsTest() throws BusinessLogicException {
        List<CommentEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CommentEntity entity = factory.manufacturePojo(CommentEntity.class);
            entity.setPost(data.get(0));

            commentLogic.createComment(entity);

            newCollection.add(entity);
        }
        postCommentLogic.replaceComments(data.get(0).getId(), newCollection);
        List<CommentEntity> comments = postCommentLogic.getComments(data.get(0).getId());
        for (CommentEntity newE : newCollection) {
            Assert.assertTrue(comments.contains(newE));
        }
    }

    /**
     * Test for removing an comment from post
     *
     */
    @Test
    public void removeCommentTest() throws BusinessLogicException {
   
            postCommentLogic.removeComment(data.get(0).getId(), commentsData.get(0).getId());

        Assert.assertTrue(postCommentLogic.getComments(data.get(0).getId()).isEmpty());
    }
    
    



}