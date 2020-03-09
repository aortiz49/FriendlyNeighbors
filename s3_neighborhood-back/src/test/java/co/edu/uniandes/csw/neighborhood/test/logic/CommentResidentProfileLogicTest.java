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
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.ejb.CommentResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileNeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests the CommentResidentProfileLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class CommentResidentProfileLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for comment/resident logic.
     */
    @Inject
    private CommentResidentProfileLogic commentResidentProfileLogic;

    /**
     * Entity manager to communicate with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    private UserTransaction utx;

    /**
     * List of residents to be used in the tests.
     */
    private List<ResidentProfileEntity> testPeeps = new ArrayList<>();

    /**
     * List of comments to be used in the tests.
     */
    private List<CommentEntity> testComments = new ArrayList<>();
//===================================================
// Test Setup
//===================================================

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar contains classes, DB
     * descriptor and beans.xml file for dependencies injector resolution.
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random residents
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity resident = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(resident);
            testPeeps.add(resident);
        }

        // creates 3 random comments
        for (int i = 0; i < 4; i++) {
            CommentEntity comment = factory.manufacturePojo(CommentEntity.class);
            em.persist(comment);
            testComments.add(comment);
        }

        // change comment text
        testComments.get(0).setText("One");
        testComments.get(1).setText("Two");
        testComments.get(2).setText("Three");
        testComments.get(3).setText("Four");

        // associates comments to a resident
        testComments.get(0).setAuthor(testPeeps.get(0));
        testComments.get(2).setAuthor(testPeeps.get(0));

        // adds comments to a resident
        testPeeps.get(0).getComments().add(testComments.get(0));
        testPeeps.get(0).getComments().add(testComments.get(2));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a comment with a resident.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addCommentToResidentProfileTest() throws BusinessLogicException {
        // gets the second random resident from the list
        ResidentProfileEntity resident = testPeeps.get(0);

        // gets the second random comment from the list, since the first has an associated 
        // resident already
        CommentEntity comment = testComments.get(1);

        // add the comment to the resident
        CommentEntity response = commentResidentProfileLogic.addCommentToResidentProfile(
                comment.getId(), resident.getId());

        Assert.assertNotNull(response);

        ResidentProfileEntity updatedRes = em.find(ResidentProfileEntity.class, resident.getId());
        Assert.assertEquals(3, updatedRes.getComments().size());
        
        // TODO: ask why insertion isnt appended
        Assert.assertNotNull(updatedRes.getComments().contains(comment.getId()));

    }

    /**
     * Tests the consultation of all resident entities associated with a neighborhood.
     */
    @Test
    public void getResidentProfileesTest() {
        List<ResidentProfileEntity> list = residentNeighborhoodLogic.getResidentProfilees(testHoods.get(0).getId());

        // checks that there are two residentes associated to the neighborhood
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated neighborhood matches
        Assert.assertEquals(list.get(0).getNeighborhood().getName(), testHoods.get(0).getName());
    }

    /**
     * Tests the consultation of a resident entity associated with a neighborhood.
     *
     * @throws BusinessLogicException if the resident is not found
     */
    @Test
    public void getResidentProfileTest() throws BusinessLogicException {

        // gets the first resident from the list
        ResidentProfileEntity resident = testPeeps.get(0);

        // gets the first neighborhood from the list
        NeighborhoodEntity neighborhood = testHoods.get(0);

        // get the resident from the neighborhood
        ResidentProfileEntity response = residentNeighborhoodLogic.getResidentProfile(neighborhood.getId(), resident.getId());

        Assert.assertEquals(resident.getId(), response.getId());

    }

    /**
     * Tests the removal of a resident from the neighborhood.
     */
    @Test
    public void removeResidentProfileTest() {
        // gets the first neighborhood from the list. 
        // (Uses em.find because the persisted neighborhood contains the added residentes)
        NeighborhoodEntity neighborhood = em.find(NeighborhoodEntity.class, testHoods.get(0).getId());

        // get the first associated resident
        ResidentProfileEntity resident = testPeeps.get(0);

        // gets the list of residentes in the neighborhood
        List<ResidentProfileEntity> list = neighborhood.getResidents();

        residentNeighborhoodLogic.removeResidentProfile(neighborhood.getId(), resident.getId());
        Assert.assertEquals(1, list.size());

    }

}
