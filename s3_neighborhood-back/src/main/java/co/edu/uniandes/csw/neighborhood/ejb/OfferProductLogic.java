/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.OfferEntity;
import co.edu.uniandes.csw.neighborhood.entities.ProductEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.OfferPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ProductPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class OfferProductLogic {
    private static final Logger LOGGER = Logger.getLogger(OfferProductLogic.class.getName());
    
    @Inject
    private ProductPersistence productPersistence;
    
    @Inject
    private OfferPersistence offerPersistence;
    
    public ProductEntity associateProductToOffer(Long productId, Long offerId){
        LOGGER.log(Level.INFO, "Trying to associate product with offer with id = {0}", offerId);
        ProductEntity productEntity = productPersistence.find(productId);
        OfferEntity offerEntity = offerPersistence.find(offerId);
        offerEntity.getProducts().add(productEntity);
        
        LOGGER.log(Level.INFO, "Product is associated with offer with id = {0}", offerId);
        return productPersistence.find(productId);
    }
    
    public List<ProductEntity> getProducts(Long offerId){
        LOGGER.log(Level.INFO, "Gets all products belonging to offer with id = {0}", offerId);
        return offerPersistence.find(offerId).getProducts();
    }
    
    public ProductEntity getProduct(Long productId, Long offerId) throws BusinessLogicException{
        LOGGER.log(Level.INFO, "Finding product with id = {0} from offer with = " + offerId, productId);
        List<ProductEntity> products = offerPersistence.find(offerId).getProducts();
        ProductEntity productEntity = productPersistence.find(productId);
        int index = products.indexOf(productEntity);
        LOGGER.log(Level.INFO, "Finish query about product with id = {0} from offer with = " + offerId, productId);
        if(index >= 0){
            return products.get(index);
        }
        throw new BusinessLogicException("There is no association between offer and product");
    }   
    
    public List<ProductEntity> replaceProducts(Long offerId, List<ProductEntity> products) {
        LOGGER.log(Level.INFO, "Trying to replace products related to offer con id = {0}", offerId);
          OfferEntity offerEntity = offerPersistence.find(offerId);
        List<ProductEntity> productList = productPersistence.findAll();
        for (ProductEntity product : productList) {
            if (products.contains(product)) {
                if (!product.getOffers().contains(offerEntity)) {
                    product.getOffers().add(offerEntity);
                }
            } else {
                product.getOffers().remove(offerEntity);
            }
        }
        offerEntity.setProducts(products);
        LOGGER.log(Level.INFO, "Ended trying to replace products related to offer con id = {0}", offerId);
        return offerEntity.getProducts();
    }
    
    public void removeProduct(Long offerId, Long productId) {
        LOGGER.log(Level.INFO, "Trying to delete a product from offer con id = {0}", offerId);
        OfferEntity offerEntity = offerPersistence.find(offerId);
        ProductEntity productEntity = productPersistence.find(productId);
        productEntity.getOffers().remove(offerEntity);
        LOGGER.log(Level.INFO, "Finished removing an product from offer con id = {0}", offerId);
    }
}
