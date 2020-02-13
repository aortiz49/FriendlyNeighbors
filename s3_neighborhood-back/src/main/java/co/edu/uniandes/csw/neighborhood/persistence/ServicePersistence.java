/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aortiz49
 */
@Stateless
public class ServicePersistence {

    private static final Logger LOGGER = Logger.getLogger(ServicePersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Method to persist the entity in the database.
     *
     * @param serviceEntity service object to be created in the database.
     * @return returns the entity created with an id given by the database.
     */
    public ServiceEntity create(ServiceEntity serviceEntity) {
        LOGGER.log(Level.INFO, "Creating a new service");
        em.persist(serviceEntity);
        LOGGER.log(Level.INFO, "Service created");
        return serviceEntity;
    }

    /**
     * Returns all services from the database.
     *
     * @return a list with all the services found in the database, "select u
     * from ServiceEntity u" is like a "select from ServiceEntity;" -
     * "SELECT * FROM table_name" en SQL.
     */
    public List<ServiceEntity> findAll() {
        LOGGER.log(Level.INFO, "Consulting all services");
        Query q = em.createQuery("select u from ServiceEntity u");
        return q.getResultList();
    }

    /**
     * Searches for service with the id that is sent as an
     * argument
     *
     * @param serviceId: id corresponding to the service sought
     * @return a service.
     */
    public ServiceEntity find(Long serviceId) {
        LOGGER.log(Level.INFO, "Consulting notification with the id = {0}", 
                serviceId);
        return em.find(ServiceEntity.class, serviceId);

    }

    /**
     * Updates a service.
     *
     * @param serviceEntity: the service that comes with the new changes.
     * @return a service with the changes applied
     */
    public ServiceEntity update(ServiceEntity serviceEntity) {
        LOGGER.log(Level.INFO, "Updating service with the id = {0}", serviceEntity.getId());
        return em.merge(serviceEntity);
    }

    /**
     *
     * Deletes a service from the database receiving the id of the
     * notification as an argument
     *
     * @param serviceId: id corresponding to the service to delete.
     */
    public void delete(Long serviceId) {
        LOGGER.log(Level.INFO, "Deleting notification with id = {0}", 
                serviceId);
        ServiceEntity entity = em.find(ServiceEntity.class, serviceId);
        em.remove(entity);
    }
}
