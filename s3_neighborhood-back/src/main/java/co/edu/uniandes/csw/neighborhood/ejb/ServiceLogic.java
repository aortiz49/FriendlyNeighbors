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
     * @param residentId author
     * @param neighId parent neighborhood
     * @return the Service entity after it is persisted
     * @throws BusinessLogicException if the new Service violates the Service
     * rules
     */
    public ServiceEntity createService(ServiceEntity pServiceEntity, Long residentId, Long neighId) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin creating a Service");

        ResidentProfileEntity r = residentPersistence.find(residentId, neighId);

        // 1. The service must have a resident
        if (r == null) {
            throw new BusinessLogicException("The service must have a resident!");
        }

        // verify Service rules for creating a new Service
        verifyServiceCreationRules(pServiceEntity, r.getNeighborhood().getId());

        // create the Service
        ServiceEntity createdEntity = ServicePersistence.create(pServiceEntity);

        pServiceEntity.setAuthor(r);
        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End creating a businss");
        return createdEntity;
    }

    /**
     * Returns all the Services in the database.
     *
     * @return list of Services
     */
    public List<ServiceEntity> getServices(Long neighID) {
        LOGGER.log(Level.INFO, "Begin consulting all Services");
        List<ServiceEntity> Services = ServicePersistence.findAll(neighID);
        LOGGER.log(Level.INFO, "End consulting all Services");
        return Services;
    }

    /**
     * Finds a Service by ID.
     *
     * @param pId
     * @return the found Service, null if not found
     */
    public ServiceEntity getService(Long pId, Long neighID) {
        LOGGER.log(Level.INFO, "Begin search for Service with Id = {0}", pId);
        ServiceEntity entity = ServicePersistence.find(pId, neighID);
        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The Service with Id = {0} doesn't exist", pId);
        }
        LOGGER.log(Level.INFO, "End search for bsiness with Id = {0}", pId);
        return entity;
    }

    /**
     * Update a Service with  given Id.
     *
     * @param pId the id of the service
     * @param pService the new Service
     * @return the Service entity after the update
     * @throws BusinessLogicException if the new Service violates the Service
     * rules
     */
    public ServiceEntity updateService(ServiceEntity pService, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for Service with id = {0}", pService.getId());

        // update neighborhood
        ServiceEntity newEntity = ServicePersistence.update(pService, neighID);
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
    public void deleteService(Long pServiceId, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the delete process for Service with id = {0}", pServiceId);

        if (ServicePersistence.find(pServiceId, neighID) == null) {
            throw new BusinessLogicException("Service doesn't exist.");
        }

        ServicePersistence.delete(pServiceId, neighID);
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
    private boolean verifyServiceCreationRules(ServiceEntity pServiceEntity, Long neighID) throws BusinessLogicException {
        boolean valid = true;

        // the resident the potential service belongs to 
        ResidentProfileEntity serviceResident = pServiceEntity.getAuthor();

         if (pServiceEntity.getDescription() == null) {
            throw new BusinessLogicException("The Service address cannot be null!");
        } else if (pServiceEntity.getDescription().length() > 250) {
            throw new BusinessLogicException("The service cannot have more than 250 characters");

        }

        return valid;

    }
}
