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

import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfilePostLogic;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
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
public class ResidentProfilePostLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ResidentProfileLogic residentLogic;
    @Inject
    private ResidentProfilePostLogic residentPostLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<ResidentProfileEntity> data = new ArrayList<ResidentProfileEntity>();

    private List<PostEntity> postsData = new ArrayList();

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
        em.createQuery("delete from PostEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

        /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PostEntity posts = factory.manufacturePojo(PostEntity.class);
            em.persist(posts);
            postsData.add(posts);
        }
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(entity);
            data.add(entity);
            if (i == 0) {
                postsData.get(i).setAuthor(entity);
            }
        }
    }

    /**
     * Test to associate a post with a resident 
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addPostsTest() {
        ResidentProfileEntity entity = data.get(0);
        PostEntity postEntity = postsData.get(1);
        PostEntity response = residentPostLogic.associatePostToResident(postEntity.getId(), entity.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(postEntity.getId(), response.getId());
    }

    /**
     * Test for getting a collection of post entities associated with a resident
     */
    @Test
    public void getPostsTest() {
        List<PostEntity> list = residentPostLogic.getPosts(data.get(0).getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting a post entity associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getPostTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        PostEntity postEntity = postsData.get(0);
        PostEntity response = residentPostLogic.getPost(entity.getId(), postEntity.getId());

        Assert.assertEquals(postEntity.getId(), response.getId());
        Assert.assertEquals(postEntity.getDescription(), response.getDescription());

    }

    /**
     * Test for getting a post from a non-author user 
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedPostTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        PostEntity postEntity = postsData.get(1);
        residentPostLogic.getPost(entity.getId(), postEntity.getId());
    }

}
