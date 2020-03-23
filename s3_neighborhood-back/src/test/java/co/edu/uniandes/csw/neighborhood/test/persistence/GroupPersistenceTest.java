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
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
 * Persistence test for Group
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class GroupPersistenceTest {

    @Inject
    private GroupPersistence groupPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;
    
    NeighborhoodEntity neighborhood;

    private List<GroupEntity> data = new ArrayList<>();

    /**
     * @return Returns jar which Arquillian will deploy embedded in Payara. jar
     * contains classes, DB descriptor and beans.xml file for dependencies
     * injector resolution.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(GroupEntity.class.getPackage())
                .addPackage(GroupPersistence.class.getPackage())
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
        em.createQuery("delete from GroupEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        
        PodamFactory factory = new PodamFactoryImpl();
        
         neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
         em.persist(neighborhood);
         
        for (int i = 0; i < 3; i++) {
            GroupEntity entity = factory.manufacturePojo(GroupEntity.class);
            entity.setNeighborhood(neighborhood);
            
            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Test for creating a Group.
     */
    @Test
    public void createGroupTest() {
        PodamFactory factory = new PodamFactoryImpl();
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);
        GroupEntity result = groupPersistence.create(newEntity);

        Assert.assertNotNull(result);

        GroupEntity entity = em.find(GroupEntity.class, result.getId());

         Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getDateCreated(), entity.getDateCreated());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());
        Assert.assertEquals(newEntity.getEvents(), entity.getEvents());
        Assert.assertEquals(newEntity.getMembers(), entity.getMembers());
        Assert.assertEquals(newEntity.getPosts(), entity.getPosts());
        Assert.assertEquals(newEntity.getNeighborhood(), entity.getNeighborhood());

    }

    /**
     * Test for retrieving all groups from DB.
     */
    @Test
    public void findAllTest() {
        

        List<GroupEntity> list = groupPersistence.findAll(neighborhood.getId());
        Assert.assertEquals(data.size(), list.size());
        for (GroupEntity ent : list) {
            boolean found = false;
            for (GroupEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Test for a query about a group.
     */
    @Test
    public void getResidentTest() {
        GroupEntity entity = data.get(0);
        GroupEntity newEntity = groupPersistence.find(entity.getId(), neighborhood.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getDateCreated(), entity.getDateCreated());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());
        Assert.assertEquals(newEntity.getEvents(), entity.getEvents());
        Assert.assertEquals(newEntity.getMembers(), entity.getMembers());
        Assert.assertEquals(newEntity.getPosts(), entity.getPosts());
        Assert.assertEquals(newEntity.getNeighborhood(), entity.getNeighborhood());
    }
    
     /**
     * Test for a query about a group not belonging to a neighborhood.
     */
    @Test(expected = RuntimeException.class)
    public void getResidentTestNotBelonging() {
         GroupEntity entity = data.get(0);
        groupPersistence.find(entity.getId(), new Long(10000));
    }
    /**
     * Test for updating a group.
     */
    @Test
    public void updateResidentTest() {
        GroupEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        GroupEntity newEntity = factory.manufacturePojo(GroupEntity.class);

        newEntity.setId(entity.getId());
        newEntity.setNeighborhood(neighborhood);
        
        groupPersistence.update(newEntity, neighborhood.getId());

        GroupEntity resp = groupPersistence.find(entity.getId(), neighborhood.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }

    /**
     * Test for deleting a group.
     */
    @Test
    public void deleteResidentTest() {
        GroupEntity entity = data.get(0);
        groupPersistence.delete(entity.getId(), neighborhood.getId());
        GroupEntity deleted = em.find(GroupEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
   


}
