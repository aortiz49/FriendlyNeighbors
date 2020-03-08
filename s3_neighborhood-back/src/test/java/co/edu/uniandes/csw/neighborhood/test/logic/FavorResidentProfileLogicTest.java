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

import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.FavorResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
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
public class FavorResidentProfileLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private FavorResidentProfileLogic residentFavorLogic;
    
    @Inject
    private FavorLogic favorLogic;

    @PersistenceContext
    private EntityManager em;
    

    @Inject
    private UserTransaction utx;

    private List<ResidentProfileEntity> data = new ArrayList<ResidentProfileEntity>();

    private List<FavorEntity> favorsData = new ArrayList();

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
        em.createQuery("delete from FavorEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            FavorEntity favors = factory.manufacturePojo(FavorEntity.class);
            em.persist(favors);
            favorsData.add(favors);
        }
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(entity);
            data.add(entity);
            if (i == 0) {
                favorsData.get(i).setAuthor(entity);
            }
        }
    }

    /**
     * Test to associate a favor with a resident
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addFavorsTest() {
        ResidentProfileEntity entity = data.get(0);
        FavorEntity favorEntity = favorsData.get(1);
        FavorEntity response = residentFavorLogic.associateFavorToResident(favorEntity.getId(), entity.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(favorEntity.getId(), response.getId());
    }

    /**
     * Test for getting a collection of favor entities associated with a
     * resident
     */
    @Test
    public void getFavorsTest() {
        List<FavorEntity> list = residentFavorLogic.getFavors(data.get(0).getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting a favor entity associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getFavorTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        FavorEntity favorEntity = favorsData.get(0);
        FavorEntity response = residentFavorLogic.getFavor(entity.getId(), favorEntity.getId());

        Assert.assertEquals(favorEntity.getId(), response.getId());
        Assert.assertEquals(favorEntity.getDescription(), response.getDescription());

    }

    /**
     * Test for getting a favor from a non-author user
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedFavorTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        FavorEntity favorEntity = favorsData.get(1);
        residentFavorLogic.getFavor(entity.getId(), favorEntity.getId());
    }
    
        /**
     * Test for replacing favors associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test

    public void replaceFavorsTest() throws BusinessLogicException {
        List<FavorEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);
            entity.setAuthor(data.get(0));

            favorLogic.createFavor(entity);

            newCollection.add(entity);
        }
        residentFavorLogic.replaceFavors(data.get(0).getId(), newCollection);
        List<FavorEntity> favors = residentFavorLogic.getFavors(data.get(0).getId());
        for (FavorEntity newE : newCollection) {
            Assert.assertTrue(favors.contains(newE));
        }
    }

    /**
     * Test for removing an favor from resident
     *
     */
    @Test
    public void removeFavorTest() throws BusinessLogicException {
   
            residentFavorLogic.removeFavor(data.get(0).getId(), favorsData.get(0).getId());

        Assert.assertTrue(residentFavorLogic.getFavors(data.get(0).getId()).isEmpty());
    }

}
