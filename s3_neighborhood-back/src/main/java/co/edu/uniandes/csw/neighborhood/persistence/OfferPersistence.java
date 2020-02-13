/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.OfferEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
/**
 *
 * @author Carlos Figueredo
 */
@Stateless
public class OfferPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(DashboardPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU" )
    protected EntityManager em;
    
    /**
     * Creates an offer within DB
     *
     * @param offerEntity offer object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public OfferEntity create(OfferEntity offerEntity){
        
        LOGGER.log(Level.INFO, "Creating a new Offer");

        em.persist(offerEntity);
        LOGGER.log(Level.INFO, "Offer created");
        return offerEntity;
    }
    
    /**
     * Returns all logins from DB.
     *
     * @return a list with all logins found in DB.
     */
    public List<OfferEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all Offers");
        
        TypedQuery query = em.createQuery("select u from OfferEntity u", OfferEntity.class);
       
        return query.getResultList();
    }
    /**
     * Looks for an offer with the id given by argument
     *
     * @param offerId: id from offer to be found.
     * @return an offer.
     */
     public OfferEntity find(Long offerId) {
        LOGGER.log(Level.INFO, "Querying for offer with id={0}", offerId);
       
        
        return em.find(OfferEntity.class, offerId);
    }
     
    /**
     * Updates a offer with the modified offer given by argument.
     *
     * @param offerEntity: the modified offer. Por
     * @return the updated offer
     */
    public OfferEntity update(OfferEntity offerEntity) {
        LOGGER.log(Level.INFO, "Updating offer with id={0}", offerEntity.getId());
        return em.merge(offerEntity);
    }
    
    /**
     * Deletes from DB a offer with the id given by argument
     *
     * @param offerId: id from offer to be deleted.
     */
    public void delete(Long offerId) {

        LOGGER.log(Level.INFO, "Deleting dashboard wit id={0}", offerId);
        OfferEntity offerEntity = em.find(OfferEntity.class, offerId);
        em.remove(offerEntity);
    }
}
