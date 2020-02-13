/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.persistence;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Service. It connects via the
 * Entity Manager in javax.persistance with a SQL database.
 *
 * @author aortiz49
 */
@Stateless
public class ServicePersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the service persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            BusinessPersistence.class.getName());

    /**
     * The entity manager that will access the service table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a service in the database.
     *
     * @param pserviceEntity business object to be created in the
     * databse
     * @return the created service with an id given by the databse
     */
    public ServiceEntity create(ServiceEntity pServiceEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new service");

        // makes the entity instance managed and persistent
        em.persist(pServiceEntity);
        LOGGER.log(Level.INFO, "Service created");

        return pServiceEntity;
    }

    /**
     * Returns all services in the database.
     *
     * @return a list containing every service in the database. select u
     * from ServiceEntity u" is akin to a "SELECT * from
     * ServiceEntity" in SQL.
     */
    public List<ServiceEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all services");

        // Create a typed ServiceEntity query to find all services 
        // in the database. 
        TypedQuery<ServiceEntity> query = em.createQuery(
                "select u from ServiceEntity u", ServiceEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a service with the id given by the parameter.
     *
     * @param pserviceId the id corresponding to the service
     * @return the found service
     */
    public ServiceEntity find(Long pServiceId) {
        LOGGER.log(Level.INFO, "Consulting service with id={0}",
                pServiceId);

        return em.find(ServiceEntity.class, pServiceId);
    }

    /**
     * Updates a business.
     *
     * @param pServiceEntity the service with the modifications. For
     * example, the name could have changed. In that case, we must use this
     * update method.
     * @return the service with the updated changes
     */
    public ServiceEntity update(ServiceEntity pServiceEntity) {
        LOGGER.log(Level.INFO, "Updating service with id = {0}",
                pServiceEntity.getId());
        return em.merge(pServiceEntity);
    }

    /**
     * Deletes a service.
     * <p>
     *
     * Deletes the service with the associated Id.
     *
     * @param pServiceId the id of the service to be deleted
     */
    public void delete(Long pServiceId) {
        LOGGER.log(Level.INFO, "Deleting service with id = {0}",
                pServiceId);
        ServiceEntity reviewEntity = em.find(ServiceEntity.class,
                pServiceId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO,
                "Exiting the deletion of pServiceId with id = {0}",
                pServiceId);
    }

}
