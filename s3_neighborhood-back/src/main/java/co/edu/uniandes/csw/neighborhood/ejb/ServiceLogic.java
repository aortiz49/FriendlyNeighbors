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
import co.edu.uniandes.csw.neighborhood.entities.*;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.*;
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
     * The injected resident persistence object.
     */
    @Inject
    private ResidentProfilePersistence residentPersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists the new Service
     *
     * @param pServiceEntity the entity of type Service of the new Service to be
     * persisted.
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
     * @param pId
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
     * @param pName
     * @return the found Service, null if not found
     */
    public ServiceEntity getServiceByName(String pName) {
        LOGGER.log(Level.INFO, "Begin search for Service with name = {0}", pName);
        ServiceEntity ServiceEntity = ServicePersistence.findByTitle(pName);
        if (ServiceEntity == null) {
            LOGGER.log(Level.SEVERE, "The Service with name = {0} doesn't exist", pName);
        }
        LOGGER.log(Level.INFO, "End search for Service with name = {0}", pName);
        return ServiceEntity;
    }

    /**
     * Update a Service with a given Id.
     *
     * @param pId the id of the service
     * @param pService the new Service
     * @return the Service entity after the update
     * @throws BusinessLogicException if the new Service violates the Service
     * rules
     */
    public ServiceEntity updateService(Long pId, ServiceEntity pService) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for Service with id = {0}", pId);

        // update neighborhood
        ServiceEntity newEntity = ServicePersistence.update(pService);
        LOGGER.log(Level.INFO, "End the update process for Service with id = {0}", pService.getTitle());

        return newEntity;
    }

    /**
     * Deletes a Service by ID.
     *
     * @param pServiceId the ID of the book to be deleted
     * @throws BusinessLogicException if the deletion failed
     *
     */
    public void deleteService(Long pServiceId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the delete process for Service with id = {0}", pServiceId);

        if (ServicePersistence.find(pServiceId) == null) {
            throw new BusinessLogicException("Service doesn't exist.");                   
        }
            
        ServicePersistence.delete(pServiceId);
        LOGGER.log(Level.INFO, "End the delete process for Service with id = {0}", pServiceId);
    }

    /**
     * Verifies that the the Service is valid.
     *
     * @param pServiceEntity Service to verify
     * @return true if the Service is valid. False otherwise
     * @throws BusinessLogicException if the Service doesn't satisfy the Service
     * rules
     */
    private boolean verifyServiceCreationRules(ServiceEntity pServiceEntity) throws BusinessLogicException {
        boolean valid = true;

        // the resident the potential service belongs to 
        ResidentProfileEntity serviceResident = pServiceEntity.getAuthor();

        // 1. The service must have a resident
        if (serviceResident == null) {
            throw new BusinessLogicException("The service must have a resident!");
        } // 2. The resident to which the service will be added to must already exist
        else if (residentPersistence.find(serviceResident.getId()) == null) {
            throw new BusinessLogicException("The Service's neighborhood doesn't exist!");
        }// 3. The description of the service cannot be null
        else if (pServiceEntity.getDescription() == null) {
            throw new BusinessLogicException("The Service address cannot be null!");
        }

        return valid;

    }
}
