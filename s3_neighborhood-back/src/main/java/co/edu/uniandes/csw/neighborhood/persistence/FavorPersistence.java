/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class FavorPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(FavorPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;
    
    /**
     * Method to persist the entity in the database.
     *
     * @param favorEntity favor object to be created in the database.
     * @return returns the entity created with an id given by the database.
     */
    public FavorEntity create(FavorEntity favorEntity) {
        LOGGER.log(Level.INFO, "Creating a new favor");
        em.persist(favorEntity);
        LOGGER.log(Level.INFO, "Favor created");
        return favorEntity;
    }
    
    /**
     * Returns all favors from the database.
     *
     * @return a list with all the favors found in the database, "select u from FavorEntity u" is like a "select 
     * from FavorEntity;" - "SELECT * FROM table_name" en SQL.
     */
    public List<FavorEntity> findAll() {
        LOGGER.log(Level.INFO, "Consulting all favors");
        TypedQuery q = em.createQuery("select u from FavorEntity u", FavorEntity.class);
        return q.getResultList();
    }
    
    /**
     * Search if there is any favor with the id that is sent as an argument
     *
     * @param favorId: id corresponding to the favor sought
     * @return a favor.
     */
    public FavorEntity find(Long favorId) {
        LOGGER.log(Level.INFO, "Consulting favor with the id = {0}", favorId);
        return em.find(FavorEntity.class, favorId);
        
    }
    
    /**
     * update a favor.
     *
     * @param favorEntity: the favor that comes with the new changes.
     * @return a favor with the changes applied
     */
    public FavorEntity update(FavorEntity favorEntity) {
        LOGGER.log(Level.INFO, "Updating favor with the id = {0}", favorEntity.getId());
        return em.merge(favorEntity);
    }

    /**
     *
     * Delete a favor from the database receiving the id of the favor as an argument
     *
     * @param favorId: id corresponding to the favor to delete.
     */
    public void delete(Long favorId) {
        LOGGER.log(Level.INFO, "Deleting favor with id = {0}", favorId);
        FavorEntity entity = em.find(FavorEntity.class, favorId);
        em.remove(entity);
    }
}