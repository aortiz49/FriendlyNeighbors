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

import co.edu.uniandes.csw.neighborhood.ejb.BusinessResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
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
 * Tests the BusinessResidentProfileLogic.
 *
 * @author aortiz49
 */
@RunWith(Arquillian.class)
public class BusinessResidentProfileLogicTest {
//===================================================
// Attributes
//===================================================

    /**
     * Factory that creates entity POJOs.
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * Dependency injection for business/residentProfile logic.
     */
    @Inject
    private BusinessResidentProfileLogic businessResidentProfileLogic;

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
     * List of residentProfiles to be used in the tests.
     */
    private List<ResidentProfileEntity> testPeeps = new ArrayList<>();

    /**
     * List of businesses to be used in the tests.
     */
    private List<BusinessEntity> testJoints = new ArrayList<>();

    /**
     * Neighborhood to be used in the tests.
     */
    private NeighborhoodEntity neighborhood;

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
        em.createQuery("delete from BusinessEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();

    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        // creates 3 random residentProfiles
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity res = factory.manufacturePojo(ResidentProfileEntity.class);

            res.setNeighborhood(neighborhood);

            em.persist(res);
            testPeeps.add(res);
        }

        // creates 3 random businesses
        for (int i = 0; i < 3; i++) {
            BusinessEntity buss = factory.manufacturePojo(BusinessEntity.class);

            buss.setNeighborhood(neighborhood);
            em.persist(buss);
            testJoints.add(buss);
        }

        // associates businesses to a residentProfile
        testJoints.get(0).setOwner(testPeeps.get(0));
        testJoints.get(2).setOwner(testPeeps.get(0));

    }
//===================================================
// Tests
//===================================================

    /**
     * Tests the consultation of all business entities associated with residentProfile.
     */
    @Test
    public void getBusinessesTest() {
        List<BusinessEntity> list = businessResidentProfileLogic.
                getBusinesses(neighborhood.getId(), testPeeps.get(0).getId());

        // checks that there are two businesses associated to the residentProfile
        Assert.assertEquals(2, list.size());
        // checks that the name of the associated residentProfile matches
        Assert.assertEquals(list.get(0).getOwner().getName(), testPeeps.get(0).getName());
    }

    /**
     * Tests the consultation of a business entity associated with residentProfile.
     *
     * @throws BusinessLogicException if the business is not found
     */
    @Test
    public void getBusinessTest() throws BusinessLogicException {

        // gets the first business from the list
        BusinessEntity business = testJoints.get(0);

        // gets the first residentProfile from the list
        ResidentProfileEntity residentProfile = testPeeps.get(0);

        // get the business from the residentProfile
        BusinessEntity response = businessResidentProfileLogic.getBusiness(
                neighborhood.getId(), residentProfile.getId(), business.getId());

        Assert.assertEquals(business.getId(), response.getId());

    }

    /**
     * Tests the removal of a business from the residentProfile.
     *
     * @throws BusinessLogicException if cannot delete the business
     */
    @Test
    public void removeBusinessTest() throws BusinessLogicException {
        // gets the first residentProfile from the list. 
        // (Uses em.find because the persisted residentProfile contains the added businesses)
        ResidentProfileEntity residentProfile = em.find(ResidentProfileEntity.class, testPeeps.get(0).getId());

        // get the first associated business
        BusinessEntity business = testJoints.get(0);

        // gets the list of businesses in the residentProfile
        List<BusinessEntity> list = em.find(ResidentProfileEntity.class, residentProfile.getId()).getBusinesses();

        businessResidentProfileLogic.removeBusiness(neighborhood.getId(), residentProfile.getId(), business.getId());
        Assert.assertEquals(1, list.size());

    }
}
