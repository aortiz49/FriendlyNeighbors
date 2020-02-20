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

import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 * Persistence test for ResidentProfile
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class ResidentProfilePersistenceTest {

    @Inject
    private ResidentProfilePersistence residentPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<ResidentProfileEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ResidentProfileEntity.class.getPackage())
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
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating a ResidentProfile.
     */
    @Test
    public void createResidentProfileTest() {
        PodamFactory factory = new PodamFactoryImpl();
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);
        ResidentProfileEntity result = residentPersistence.create(newEntity);

        Assert.assertNotNull(result);

        ResidentProfileEntity entity = em.find(ResidentProfileEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Test for retrieving all residents from DB.
     */
    @Test
    public void findAllTest() {
        List<ResidentProfileEntity> list = residentPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (ResidentProfileEntity ent : list) {
            boolean found = false;
            for (ResidentProfileEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for a query about a resident.
     */
    @Test
    public void getResidentTest() {
        ResidentProfileEntity entity = data.get(0);
        ResidentProfileEntity newEntity = residentPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getNickname(), newEntity.getNickname());
        Assert.assertEquals(entity.getEmail(), newEntity.getEmail());
        Assert.assertEquals(entity.getPhoneNumber(), newEntity.getPhoneNumber());
        Assert.assertEquals(entity.getPreferences(), newEntity.getPreferences());
        Assert.assertEquals(entity.getProofOfResidence(), newEntity.getProofOfResidence());

    }

    /**
     * Test for updating a resident.
     */
    @Test
    public void updateResidentTest() {
        ResidentProfileEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        ResidentProfileEntity newEntity = factory.manufacturePojo(ResidentProfileEntity.class);

        newEntity.setId(entity.getId());

        residentPersistence.update(newEntity);

        ResidentProfileEntity resp = em.find(ResidentProfileEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }

    /**
     * Test for deleting a resident.
     */
    @Test
    public void deleteResidentTest() {
        ResidentProfileEntity entity = data.get(0);
        residentPersistence.delete(entity.getId());
        ResidentProfileEntity deleted = em.find(ResidentProfileEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
    
        @Test
    public void getResidentByEmailTest() {
        
        ResidentProfileEntity entity = data.get(0);
        ResidentProfileEntity newEntity = residentPersistence.findByEmail(entity.getEmail());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getNickname(), newEntity.getNickname());
        Assert.assertEquals(entity.getEmail(), newEntity.getEmail());
        Assert.assertEquals(entity.getPhoneNumber(), newEntity.getPhoneNumber());
        Assert.assertEquals(entity.getPreferences(), newEntity.getPreferences());
        Assert.assertEquals(entity.getProofOfResidence(), newEntity.getProofOfResidence());

    }


}
