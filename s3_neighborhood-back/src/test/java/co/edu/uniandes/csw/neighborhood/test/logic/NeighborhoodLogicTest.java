/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
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
 * Runs tests for NeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class NeighborhoodLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Creates NeighborhoodEntity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Injects NeighborhoodLogic objects.
     */
    @Inject
    private NeighborhoodLogic neighborhoodLogic;

    /**
     * Entity manager to communicate with the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * The UserTransaction used to directly manipulate data in the database.
     */
    @Inject
    UserTransaction utx;

    /**
     * An array containing the set of data used for the tests.
     */
    private List<NeighborhoodEntity> data = new ArrayList<>();

//===================================================
// Tests
//===================================================
    // configurations to deploy the test
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NeighborhoodEntity.class.getPackage())
                .addPackage(NeighborhoodLogic.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
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
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserts the initial data for the tests.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            NeighborhoodEntity neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
            em.persist(neighborhood);
            data.add(neighborhood);
        }
    }

    /**
     * Test to create a Neighborhood.
     *
     * @throws BusinessLogicException if the business wasn't created properly
     */
    @Test
    public void createNeighborhoodTest() {

        try {
            // generate a random neighborhood
            NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);

            // create the neighborhood and persist it in the database
            NeighborhoodEntity result = neighborhoodLogic.createBusiness(newEntity);
            Assert.assertNotNull(result);

            // find the neighborhood in the database
            NeighborhoodEntity entity = em.find(NeighborhoodEntity.class, result.getId());
            Assert.assertEquals(newEntity.getId(), entity.getId());
            Assert.assertEquals(newEntity.getName(), entity.getName());

            // create a neighborhood with a null name
            NeighborhoodEntity failedEntity = factory.manufacturePojo(NeighborhoodEntity.class);
            failedEntity.setName(null);

            // created neighborhood should fail and throw the exception
            neighborhoodLogic.createBusiness(failedEntity);
        } catch (BusinessLogicException e) {
            System.out.println(e.getMessage());
        }

    }

}
