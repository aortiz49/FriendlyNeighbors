package co.edu.uniandes.csw.neighborhood.ejb;

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

import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 * Pruebas de logica de Neighborhoods
 *
 * @neighborhood ISIS2603
 */
@RunWith(Arquillian.class)
public class NeighborhoodLogicTest2 {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private NeighborhoodLogic neighborhoodLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<NeighborhoodEntity> data = new ArrayList<>();

    /**
     * @return Devuelve el jar que Arquillian va a desplegar en Payara embebido. El jar contiene las
     * clases, el descriptor de la base de datos y el archivo beans.xml para resolver la inyección
     * de dependencias.
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
     * Configuración inicial de la prueba.
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
     * Limpia las tablas que están implicadas en la prueba.
     */
    private void clearData() {
        em.createQuery("delete from ResidentProfileEntity").executeUpdate();
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            NeighborhoodEntity entity = factory.manufacturePojo(NeighborhoodEntity.class);
            em.persist(entity);
            entity.setResidents(new ArrayList<>());
            data.add(entity);
        }
        
        NeighborhoodEntity neighborhood = data.get(2);
        ResidentProfileEntity entity = factory.manufacturePojo(ResidentProfileEntity.class);
        entity.setNeighborhood(neighborhood);
        em.persist(entity);
        neighborhood.getResidents().add(entity);

    }

    /**
     * Prueba para crear un Neighborhood.
     */
    @Test
    public void createNeighborhoodTest() throws BusinessLogicException {
        NeighborhoodEntity newEntity = factory.manufacturePojo(NeighborhoodEntity.class);
        NeighborhoodEntity result = neighborhoodLogic.createNeighborhood(newEntity);
        Assert.assertNotNull(result);
        NeighborhoodEntity entity = em.find(NeighborhoodEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Prueba para consultar la lista de Neighborhoods.
     */
    @Test
    public void getNeighborhoodsTest() {
        List<NeighborhoodEntity> list = neighborhoodLogic.getNeighborhoods();
        Assert.assertEquals(data.size(), list.size());
        for (NeighborhoodEntity entity : list) {
            boolean found = false;
            for (NeighborhoodEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Neighborhood.
     */
    @Test
    public void getNeighborhoodTest() {
        NeighborhoodEntity entity = data.get(0);
        NeighborhoodEntity resultEntity = neighborhoodLogic.getNeighborhood(entity.getName());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
    }

    /**
     * Prueba para actualizar un Neighborhood.
     */
    @Test
    public void updateNeighborhoodTest() throws BusinessLogicException {
        NeighborhoodEntity entity = data.get(0);
        NeighborhoodEntity pojoEntity = factory.manufacturePojo(NeighborhoodEntity.class);

        pojoEntity.setId(entity.getId());

        neighborhoodLogic.updateNeighborhood(pojoEntity.getId(), pojoEntity);

        NeighborhoodEntity resp = em.find(NeighborhoodEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getName(), resp.getName());
    }

    /**
     * Prueba para eliminar un Neighborhood
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test
    public void deleteNeighborhoodTest() throws BusinessLogicException {
        NeighborhoodEntity entity = data.get(0);
        neighborhoodLogic.deleteNeighborhood(entity.getId());
        NeighborhoodEntity deleted = em.find(NeighborhoodEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para eliminar un Neighborhood asociado a un libro
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteNeighborhoodConLibroTest() throws BusinessLogicException {
        neighborhoodLogic.deleteNeighborhood(data.get(2).getId());
    }

}
