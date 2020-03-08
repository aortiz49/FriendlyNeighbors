/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.ejb;

//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection with the ServicePersistence for the
 * Service entity.
 *
 * @author aortiz49
 */
@Stateless
public class ServiceLogic {

//===================================================
// Attributes
//===================================================
    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(ServiceEntity.class.getName());

    /**
     * The injected Service persistence object.
     */
    @Inject
    private ServicePersistence ServicePersistence;

    /**
     * The injected neighborhood persistence object.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists a new Service
     *
     * @param pServiceEntity the entity of type Service of the new Service to
     * be persisted.
     * @return the Service entity after it is persisted
     * @throws BusinessLogicException if the new Service violates the Service
     * rules
     */
    public ServiceEntity createService(ServiceEntity pServiceEntity) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin creating a Service");

        // verify Service rules for creating a new Service
        verifyServiceCreationRules(pServiceEntity);

        // create the Service
        ServiceEntity createdEntity = ServicePersistence.create(pServiceEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End creating a businss");
        return createdEntity;
    }

    /**
     * Returns all the Services in the database.
     *
     * @return list of Services
     */
    public List<ServiceEntity> getServices() {
        LOGGER.log(Level.INFO, "Begin consulting all Services");
        List<ServiceEntity> Services = ServicePersistence.findAll();
        LOGGER.log(Level.INFO, "End consulting all Services");
        return Services;
    }

    /**
     * Finds a Service by ID.
     *
     * @return the found Service, null if not found
     */
    public ServiceEntity getService(Long pId) {
        LOGGER.log(Level.INFO, "Begin search for Service with Id = {0}", pId);
        ServiceEntity entity = ServicePersistence.find(pId);
        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The Service with Id = {0} doesn't exist", pId);
        }
        LOGGER.log(Level.INFO, "End search for bsiness with Id = {0}", pId);
        return entity;
    }

    /**
     * Finds a Service by name.
     *
     * @return the found Service, null if not found
     */
    public ServiceEntity getServiceByName(String pName) {
        LOGGER.log(Level.INFO, "Begin search for Service with name = {0}", pName);
        ServiceEntity ServiceEntity = ServicePersistence.findByName(pName);
        if (ServiceEntity == null) {
            LOGGER.log(Level.SEVERE, "The Service with name = {0} doesn't exist", pName);
        }
        LOGGER.log(Level.INFO, "End search for Service with name = {0}", pName);
        return ServiceEntity;
    }

    /**
     * Update a Service with a given Id.
     *
     * @param pServiceId the Id of the Service to update
     * @param pService the new Service
     * @return the Service entity after the update
     * @throws BusinessLogicException if the new Service violates the Service
     * rules
     */
    public ServiceEntity updateService(Long pId, ServiceEntity pService) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for Service with id = {0}", pId);

        // update neighborhood
        ServiceEntity newEntity = ServicePersistence.update(pService);
        LOGGER.log(Level.INFO, "End the update process for Service with id = {0}", pService.getName());
        return newEntity;
    }

    /**
     * Deletes a Service by ID.
     *
     * @param ServiceId the ID of the book to be deleted
     *
     */
    public void deleteService(Long ServiceId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the delete process for Service with id = {0}", ServiceId);
        ServicePersistence.delete(ServiceId);
        LOGGER.log(Level.INFO, "End the delete process for Service with id = {0}", ServiceId);
    }

    /**
     * Verifies that the the Service is valid.
     *
     * @param pServiceEntity Service to verify
     * @return true if the Service is valid. False otherwise
     * @throws BusinessLogicException if the Service doesn't satisfy the
     * Service rules
     */
    private boolean verifyServiceCreationRules(ServiceEntity pServiceEntity) throws BusinessLogicException {
        boolean valid = true;

        // the neighborhood the potential Service belongs to 
        NeighborhoodEntity ServiceNeighborhood = pServiceEntity.getNeighborhood();

        // 1. The Service must have a neighborhood
        if (ServiceNeighborhood == null) {
            throw new BusinessLogicException("The Service must have a neighborhood!");
        } // 2. The neighborhood to which the Service will be added to must already exist
        else if (neighborhoodPersistence.find(ServiceNeighborhood.getId()) == null) {
            throw new BusinessLogicException("The Service's neighborhood doesn't exist!");
        } // 3. No two Services can have the same name
        else if (ServicePersistence.findByName(pServiceEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood already has a Service with that name!");
        } // 4. The address of the Service cannot be null
        else if (pServiceEntity.getAddress() == null) {
            throw new BusinessLogicException("The Service address cannot be null!");
        } // 5. The name of the Service cannot be null
        else if (pServiceEntity.getName() == null) {
            throw new BusinessLogicException("The Service name cannot be null!");
        }

        return valid;

    }
}
