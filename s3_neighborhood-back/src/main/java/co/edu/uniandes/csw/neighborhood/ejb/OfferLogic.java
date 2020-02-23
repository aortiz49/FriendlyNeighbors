/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.OfferEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.OfferPersistence;
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
public class OfferLogic {
    
    private static final Logger LOGGER = Logger.getLogger(OfferLogic.class.getName());
    
    @Inject
    private OfferPersistence persistence;
    
    public OfferEntity createOffer(OfferEntity offerEntity)throws BusinessLogicException {
        
        // The discount factor must be greater than 0
        if(offerEntity.getDiscountFactor() <= 0 ){
            throw new BusinessLogicException("The discount factor must be greater than 0");
        }
        // The discount factor must be less than 100
        if(offerEntity.getDiscountFactor() > 100 ){
            throw new BusinessLogicException("The discount factor must be less than 100");
        }
        // The start day can not be null
        if(offerEntity.getStartDate() == null){
            throw new BusinessLogicException("An start day has to be specified");
        }
        // The duration can not be null
        if(offerEntity.getDuration() == null){
            throw new BusinessLogicException("A duration has to be specified");
        }
        
        // An offer's start date cannot be in the past.
        
        persistence.create(offerEntity);
        LOGGER.log(Level.INFO, "Creation process for offer eneded");
        
        return offerEntity;
    }
    
    public void deleteOffer(Long id) {
        
        LOGGER.log(Level.INFO, "Starting deleting process for offer with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for offer with id = {0}", id);
    }


    public List<OfferEntity> getOffers() {
        
        LOGGER.log(Level.INFO, "Starting querying process for all offers");
        List<OfferEntity> offers = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all offers");
        return offers;
    }

    public OfferEntity getOffer(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for offer with id ", id);
        OfferEntity offer = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for offer with id", id);
        return offer;
    }
    
    public OfferEntity updateOffer(OfferEntity offerEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for offer with id ", offerEntity.getId());
     
        // The discount factor must be greater than 0
        if(offerEntity.getDiscountFactor() <= 0 ){
            throw new BusinessLogicException("The discount factor must be greater than 0");
        }
        // The discount factor must be less than 100
        if(offerEntity.getDiscountFactor() > 100 ){
            throw new BusinessLogicException("The discount factor must be less than 100");
        }
        // The start day can not be null
        if(offerEntity.getStartDate() == null){
            throw new BusinessLogicException("An start day has to be specified");
        }
        // The duration can not be null
        if(offerEntity.getDuration() == null){
            throw new BusinessLogicException("A duration has to be specified");
        }
        
        // An offer's start date cannot be in the past.
        
        OfferEntity modified = persistence.update(offerEntity);
        LOGGER.log(Level.INFO, "Ended update process for offer with id ", offerEntity.getId());
        return modified;
    }
}
