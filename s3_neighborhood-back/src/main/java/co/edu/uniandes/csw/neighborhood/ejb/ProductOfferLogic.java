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
public class ProductOfferLogic {
    private static final Logger LOGGER = Logger.getLogger(ProductOfferLogic.class.getName());
    
    @Inject
    private ProductPersistence productPersistence;
    
    @Inject
    private OfferPersistence offerPersistence;
    
    public OfferEntity associateOfferToProduct(Long productId, Long offerId){
        LOGGER.log(Level.INFO, "Trying to associate offer with product with id = {0}", productId);
        ProductEntity productEntity = productPersistence.find(productId);
        OfferEntity offerEntity = offerPersistence.find(offerId);
        offerEntity.getProducts().add(productEntity);
                
        LOGGER.log(Level.INFO, "Offer is associated with product with id = {0}", productId);
        return offerPersistence.find(offerId);
    }
    
    public List<OfferEntity> getOffers(Long productId){
        LOGGER.log(Level.INFO, "Gets all offers belonging to product with id = {0}", productId);
        return productPersistence.find(productId).getOffers();
    }
    
    public OfferEntity getOffer(Long productId, Long offerId) throws BusinessLogicException{
        LOGGER.log(Level.INFO, "Finding offer with id = {0} from product with = " + productId, offerId);
        List<OfferEntity> offers = productPersistence.find(productId).getOffers();
        OfferEntity offerEntity = offerPersistence.find(offerId);
        int index = offers.indexOf(offerEntity);
        LOGGER.log(Level.INFO, "Finish query about offer with id = {0} from product with = " + productId, offerId);
        if(index >= 0){
            return offers.get(index);
        }
        throw new BusinessLogicException("There is no association between product and offer");
    } 
    
    public List<OfferEntity> replaceOffers(Long productId, List<OfferEntity> offers) {
        LOGGER.log(Level.INFO, "Trying to replace offers related to product con id = {0}", productId);
          ProductEntity productEntity = productPersistence.find(productId);
        List<OfferEntity> offerList = offerPersistence.findAll();
        for (OfferEntity offer : offerList) {
            if (offers.contains(offer)) {
                if (!offer.getProducts().contains(productEntity)) {
                    offer.getProducts().add(productEntity);
                }
            } else {
                offer.getProducts().remove(productEntity);
            }
        }
        productEntity.setOffers(offers);
        LOGGER.log(Level.INFO, "Ended trying to replace offers related to product con id = {0}", productId);
        return productEntity.getOffers();
    }
    
    public void removeOffer(Long offerId, Long productId) {
        LOGGER.log(Level.INFO, "Trying to delete a offer from product con id = {0}", productId);
        ProductEntity productEntity = productPersistence.find(productId);
        OfferEntity offerEntity = offerPersistence.find(offerId);
        offerEntity.getProducts().remove(productEntity);
        LOGGER.log(Level.INFO, "Finished removing an offer from product con id = {0}", offerId);
    }
}
