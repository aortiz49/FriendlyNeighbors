/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.LoginEntity;
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
public class LoginPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(LoginPersistence.class.getName());

    @PersistenceContext(unitName = "FriendlyNeighborsPU" )
    protected EntityManager em;
    
     /**
     * Creates a login within DB
     *
     * @param le login object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public LoginEntity create(LoginEntity le){
        
        LOGGER.log(Level.INFO, "Creating a new Login");

        em.persist(le);
        LOGGER.log(Level.INFO, "Login created");
        return le;
    }
    
    /**
     * Returns all logins from DB.
     *
     * @return a list with all logins found in DB.
     */
    public List<LoginEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all logins");
        
        TypedQuery query = em.createQuery("select u from LoginEntity u", LoginEntity.class);
       
        return query.getResultList();
    }
    /**
     * Looks for a login with the id given by argument
     *
     * @param loginId: id from login to be found.
     * @return a login.
     */
     public LoginEntity find(Long loginId) {
        LOGGER.log(Level.INFO, "Querying for login with id={0}", loginId);
       
        
        return em.find(LoginEntity.class, loginId);
    }
     
    /**
     * Updates a login with the modified login given by argument.
     *
     * @param le: the modified login. Por
     * @return the updated login
     */
    public LoginEntity update(LoginEntity le) {
        LOGGER.log(Level.INFO, "Updating login with id={0}", le.getId());
        return em.merge(le);
    }
    
    /**
     * Deletes from DB a login with the id given by argument
     *
     * @param loginId: id from login to be deleted.
     */
    public void delete(Long loginId) {

        LOGGER.log(Level.INFO, "Deleting login wit id={0}", loginId);
        LoginEntity loginEntity = em.find(LoginEntity.class, loginId);
        em.remove(loginEntity);
    }
}
