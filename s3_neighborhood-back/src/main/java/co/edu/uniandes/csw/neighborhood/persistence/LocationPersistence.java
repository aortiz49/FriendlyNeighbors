/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class LocationPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(LocationPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;
    
    /**
     * Method to persist the entity in the database.
     *
     * @param locationEntity location object to be created in the database.
     * @return returns the entity created with id given by the database.
     */
    public LocationEntity create(LocationEntity locationEntity) {
        LOGGER.log(Level.INFO, "Creating a new location");
        em.persist(locationEntity);
        LOGGER.log(Level.INFO, "Location created");
        return locationEntity;
    }
    
    /**
     * Returns all location from the database.
     *
     * @return a list with ll the locations found in the database, "select u from LocationEntity u" is like a "select 
     * from LocationEntity;" - "SELECT * FROM table_name" en SQL.
     */
    public List<LocationEntity> findAll() {
        LOGGER.log(Level.INFO, "Consulting all locations");
        Query q = em.createQuery("select u from LocationEntity u");
        return q.getResultList();
    }
    
    /**
     * Search if there is any location with the id that is sent as an argument
     *
     * @param locationId: id corresponding to the location sought
     * @return a location.
     */
    public LocationEntity find(Long locationId) {
        LOGGER.log(Level.INFO, "Consulting location with the id = {0}", locationId);
        return em.find(LocationEntity.class, locationId);
        
    }
    
    /**
     * update a location.
     *
     * @param locationEntity: the location that comes with the new changes.
     * @return a location with the changes applied
     */
    public LocationEntity update(LocationEntity locationEntity) {
        LOGGER.log(Level.INFO, "Updating location with the id = {0}", locationEntity.getId());
        return em.merge(locationEntity);
    }

    /**
     *
     * Delete a location from the database receiving the id of the location as an argument
     *
     * @param locationId: id corresponding to the location to delete.
     */
    public void delete(Long locationId) {
        LOGGER.log(Level.INFO, "Deleting location with id = {0}", locationId);
        LocationEntity entity = em.find(LocationEntity.class, locationId);
        em.remove(entity);
    }
}