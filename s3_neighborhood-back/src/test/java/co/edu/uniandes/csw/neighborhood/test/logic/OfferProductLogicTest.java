/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.OfferProductLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ProductLogic;
import co.edu.uniandes.csw.neighborhood.entities.OfferEntity;
import co.edu.uniandes.csw.neighborhood.entities.ProductEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.OfferPersistence;
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
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class OfferProductLogicTest {
    
    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private OfferProductLogic offerProductLogic;

    @Inject
    private ProductLogic productLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private OfferEntity offer = new OfferEntity();
    private List<ProductEntity> data = new ArrayList<>();
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(OfferEntity.class.getPackage())
                .addPackage(ProductEntity.class.getPackage())
                .addPackage(OfferProductLogic.class.getPackage())
                .addPackage(OfferPersistence.class.getPackage())
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
    
    /**f
     * Clears tables involved in tests
     */
    private void clearData() {
        em.createQuery("delete from OfferEntity").executeUpdate();
        em.createQuery("delete from ProductEntity").executeUpdate();
    }
    
    /**
     * Inserts initial data for correct test operation
     */
    private void insertData() {
        offer = factory.manufacturePojo(OfferEntity.class);
        offer.setId(1L);
        offer.setProducts(new ArrayList<>());
        em.persist(offer);

        for (int i = 0; i < 3; i++) {
            ProductEntity entity = factory.manufacturePojo(ProductEntity.class);
            entity.setOffers(new ArrayList<>());
            entity.getOffers().add(offer);
            em.persist(entity);
            data.add(entity);
            offer.getProducts().add(entity);
        }
    }
    
    @Test
    public void addProductTest() throws BusinessLogicException {
        ProductEntity newProduct = factory.manufacturePojo(ProductEntity.class);
        newProduct.setPrice(500.0);
        newProduct.setMaxSaleQuantity(20);
        productLogic.createProduct(newProduct);
        ProductEntity productEntity = offerProductLogic.associateProductToOffer(newProduct.getId(), offer.getId());
        Assert.assertNotNull(productEntity);

        Assert.assertEquals(productEntity.getId(), newProduct.getId());
        Assert.assertEquals(productEntity.getDescription(), newProduct.getDescription());


        ProductEntity lastProduct = offerProductLogic.getProduct(productEntity.getId(), offer.getId() );

        Assert.assertEquals(lastProduct.getId(), newProduct.getId());
    }
    
    @Test
    public void getProductsTest() {
        List<ProductEntity> productEntities = offerProductLogic.getProducts(offer.getId());

        Assert.assertEquals(data.size(), productEntities.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertTrue(productEntities.contains(data.get(0)));
        }
    }
    
    @Test
    public void getProductTest() throws BusinessLogicException {        
        ProductEntity productEntity = data.get(0);
        ProductEntity product = offerProductLogic.getProduct(productEntity.getId(), offer.getId());
        Assert.assertNotNull(product);

        Assert.assertEquals(productEntity.getId(), product.getId());
        Assert.assertEquals(productEntity.getDescription(), product.getDescription());
    }
   
}
