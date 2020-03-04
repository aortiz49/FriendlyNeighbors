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
import co.edu.uniandes.csw.neighborhood.ejb.ServiceResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
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
public class ServiceResidentProfileLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private ResidentProfileLogic residentLogic;
    @Inject
    private ServiceResidentProfileLogic residentServiceLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<ResidentProfileEntity> data = new ArrayList<ResidentProfileEntity>();

    private List<ServiceEntity> servicesData = new ArrayList<ServiceEntity>();

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
        em.createQuery("delete from ServiceEntity").executeUpdate();
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
    }

    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            ServiceEntity services = factory.manufacturePojo(ServiceEntity.class);
            em.persist(services);
            servicesData.add(services);
        }
        for (int i = 0; i < 3; i++) {
            ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
            em.persist(entity);
            data.add(entity);
            if (i == 0) {
                servicesData.get(i).setAuthor(entity);
            }
        }
    }

    /**
     * Test to associate a service with a resident
     *
     *
     * @throws BusinessLogicException
     */
    @Test
    public void addServicesTest() {
        ResidentProfileEntity entity = data.get(0);
        ServiceEntity serviceEntity = servicesData.get(1);
        ServiceEntity response = residentServiceLogic.associateServiceToResident(serviceEntity.getId(), entity.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(serviceEntity.getId(), response.getId());
    }

    /**
     * Test for getting a collection of service entities associated with a resident
     */
    @Test
    public void getServicesTest() {
        List<ServiceEntity> list = residentServiceLogic.getServices(data.get(0).getId());

        Assert.assertEquals(1, list.size());
    }

    /**
     * Test for getting a service entity associated with a resident
     *
     * @throws BusinessLogicException
     */
    @Test
    public void getServiceTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        ServiceEntity serviceEntity = servicesData.get(0);
        ServiceEntity response = residentServiceLogic.getService(entity.getId(), serviceEntity.getId());

        Assert.assertEquals(serviceEntity.getId(), response.getId());
        Assert.assertEquals(serviceEntity.getDescription(), response.getDescription());

    }

    /**
     * Test for getting a service from a non-author user
     *
     * @throws BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void getNonRealatedServiceTest() throws BusinessLogicException {
        ResidentProfileEntity entity = data.get(0);
        ServiceEntity serviceEntity = servicesData.get(1);
        residentServiceLogic.getService(entity.getId(), serviceEntity.getId());
    }

}
