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

import co.edu.uniandes.csw.neighborhood.ejb.BusinessNeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.lang.reflect.InvocationTargetException;
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
 * Tests the BusinessNeighborhoodLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class BusinessNeighborhoodLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for business/neighborhood logic.
     */
    @Inject
    private BusinessNeighborhoodLogic businessNeighborhoodLogic;

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
     * List of neighborhoods to be used in the tests.
     */
    private List<NeighborhoodEntity> testHoods = new ArrayList<NeighborhoodEntity>();

    /**
     * List of businesses to be used in the tests.
     */
    private List<BusinessEntity> testJoints = new ArrayList<BusinessEntity>();
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
        em.createQuery("delete from BusinessEntity").executeUpdate();
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        // creates 3 random neighborhoods
        for (int i = 0; i < 3; i++) {
            NeighborhoodEntity neigh = factory.manufacturePojo(NeighborhoodEntity.class);
            em.persist(neigh);
            testHoods.add(neigh);
        }

        // creates 3 random businesses
        for (int i = 0; i < 3; i++) {
            BusinessEntity buss = factory.manufacturePojo(BusinessEntity.class);
            em.persist(buss);
            testJoints.add(buss);
        }

        // associates businesses to a neighborhood
        testJoints.get(0).setNeighborhood(testHoods.get(0));
        testJoints.get(2).setNeighborhood(testHoods.get(0));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the association of a business with a neighborhood.
     *
     * @throws BusinessLogicException if the association fails
     */
    @Test
    public void addBusinessToNeighborhoodTest() throws BusinessLogicException {
        // gets the second random neighborhood from the list
        NeighborhoodEntity neighborhood = testHoods.get(0);

        // gets the second random business from the list, since the first has an associated 
        // business already
        BusinessEntity business = testJoints.get(1);

        // add the business to the neighborhood
        BusinessEntity response = businessNeighborhoodLogic.addBusinessToNeighborhood(
                business.getId(), neighborhood.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(business.getId(), response.getId());
    }

    /**
     * Tests the consultation of all business entities associated with a neighborhood.
     */
    @Test
    public void getBusinessesTest() {
        List<BusinessEntity> list = businessNeighborhoodLogic.getBusinesses(testHoods.get(0).getId());

        // checks that there are two businesses associated to the neighborhood
        Assert.assertEquals(2, list.size());

        // checks that the name of the associated neighborhood matches
        Assert.assertEquals(list.get(0).getNeighborhood().getName(), testHoods.get(0).getName());
    }

    /**
     * Tests the consultation of a business entity associated with a neighborhood.
     *
     * @throws BusinessLogicException if the business is not found
     */
    @Test
    public void getBusinessTest() throws BusinessLogicException {

        // gets the first business from the list
        BusinessEntity business = testJoints.get(0);

        // gets the first neighborhood from the list
        NeighborhoodEntity neighborhood = testHoods.get(0);

        // get the business from the neighborhood
        BusinessEntity response = businessNeighborhoodLogic.getBusiness(neighborhood.getId(), business.getId());

        Assert.assertEquals(business.getId(), response.getId());

    }

    /**
     * Tests the removal of a business from the neighborhood. 
     */
    @Test
    public void removeBusinessTest() {
        // gets the first neighborhood from the list. 
        // (Uses em.find because the persisted neighborhood contains the added businesses)
        NeighborhoodEntity neighborhood = em.find(NeighborhoodEntity.class,testHoods.get(0).getId());
        
        // get the first associated business
        BusinessEntity business = testJoints.get(0);
        
        // gets the list of businesses in the neighborhood
        List<BusinessEntity> list = em.find(NeighborhoodEntity.class, neighborhood.getId()).getBusinesses();
        
        businessNeighborhoodLogic.removeBusiness(neighborhood.getId(), business.getId());
        Assert.assertEquals(1, list.size());

    }

}
