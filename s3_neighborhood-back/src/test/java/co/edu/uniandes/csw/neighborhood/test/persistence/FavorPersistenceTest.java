/*
MIT License

Copyright (c) 2019 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.test.persistence;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.FavorPersistence;
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
 * Persistence test for Favor
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class FavorPersistenceTest {

    @Inject
    private FavorPersistence favorPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    NeighborhoodEntity neighborhood;

    ResidentProfileEntity resident;

    private List<FavorEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(FavorEntity.class.getPackage())
                .addPackage(FavorPersistence.class.getPackage())
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
            em.joinTransaction();
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
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {

        PodamFactory factory = new PodamFactoryImpl();

        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        em.persist(neighborhood);

        resident = factory.manufacturePojo(ResidentProfileEntity.class);
        
        resident.setNeighborhood(neighborhood);
        em.persist(resident);
        

        for (int i = 0; i < 3; i++) {
            FavorEntity entity = factory.manufacturePojo(FavorEntity.class);
            entity.setAuthor(resident);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating a Favor.
     */
    @Test
    public void createFavorTest() {
        PodamFactory factory = new PodamFactoryImpl();
        FavorEntity newEntity = factory.manufacturePojo(FavorEntity.class);
        FavorEntity result = favorPersistence.create(newEntity);

        Assert.assertNotNull(result);

        FavorEntity entity = em.find(FavorEntity.class, result.getId());

        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());

    }

    /**
     * Test for retrieving all residents from DB.
     */
    @Test
    public void findAllTest() {

        List<FavorEntity> list = favorPersistence.findAll(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (FavorEntity ent : list) {
            boolean found = false;
            for (FavorEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for a query about a favor.
     */
    @Test
    public void getResidentTest() {
        FavorEntity entity = data.get(0);
        FavorEntity newEntity = favorPersistence.find(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getDatePosted(), newEntity.getDatePosted());
        Assert.assertEquals(entity.getTitle(), newEntity.getTitle());
    }

    /**
     * Test for a query about a favor not belonging to a neighborhood.
     */
    @Test(expected = RuntimeException.class)
    public void getResidentTestNotBelonging() {
        FavorEntity entity = data.get(0);
        favorPersistence.find(entity.getId(), new Long(10000));
    }

    /**
     * Test for updating a favor.
     */
    @Test
    public void updateResidentTest() {
        FavorEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        FavorEntity newEntity = factory.manufacturePojo(FavorEntity.class);

        newEntity.setId(entity.getId());
        newEntity.setAuthor(resident);

        favorPersistence.update(newEntity, neighborhood.getId());

        FavorEntity resp = favorPersistence.find(entity.getId(), neighborhood.getId());

        Assert.assertEquals(newEntity.getTitle(), resp.getTitle());
    }

    /**
     * Test for deleting a favor.
     */
    @Test
    public void deleteResidentTest() {
        FavorEntity entity = data.get(0);
        favorPersistence.delete(entity.getId(), neighborhood.getId());
        FavorEntity deleted = em.find(FavorEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }


}
