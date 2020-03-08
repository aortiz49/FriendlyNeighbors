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
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection with the businessPersistence for the
 * Business entity.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessLogic {

//===================================================
// Attributes
//===================================================
    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessEntity.class.getName());

    /**
     * The injected business persistence object.
     */
    @Inject
    private BusinessPersistence businessPersistence;

    /**
     * The injected neighborhood persistence object.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists a new business
     *
     * @param pBusinessEntity the entity of type Business of the new business to
     * be persisted.
     * @return the business entity after it is persisted
     * @throws BusinessLogicException if the new business violates the business
     * rules
     */
    public BusinessEntity createBusiness(BusinessEntity pBusinessEntity) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin creating a business");

        // verify business rules for creating a new business
        verifyBusinessCreationRules(pBusinessEntity);

        // create the business
        BusinessEntity createdEntity = businessPersistence.create(pBusinessEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End creating a businss");
        return createdEntity;
    }

    /**
     * Returns all the businesses in the database.
     *
     * @return list of businesses
     */
    public List<BusinessEntity> getBusinesses() {
        LOGGER.log(Level.INFO, "Begin consulting all businesses");
        List<BusinessEntity> businesses = businessPersistence.findAll();
        LOGGER.log(Level.INFO, "End consulting all businesses");
        return businesses;
    }

    /**
     * Finds a business by ID.
     *
     * @return the found business, null if not found
     */
    public BusinessEntity getBusiness(Long pId) {
        LOGGER.log(Level.INFO, "Begin search for business with Id = {0}", pId);
        BusinessEntity entity = businessPersistence.find(pId);
        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The business with Id = {0} doesn't exist", pId);
        }
        LOGGER.log(Level.INFO, "End search for bsiness with Id = {0}", pId);
        return entity;
    }

    /**
     * Finds a business by name.
     *
     * @return the found business, null if not found
     */
    public BusinessEntity getBusinessByName(String pName) {
        LOGGER.log(Level.INFO, "Begin search for business with name = {0}", pName);
        BusinessEntity businessEntity = businessPersistence.findByName(pName);
        if (businessEntity == null) {
            LOGGER.log(Level.SEVERE, "The business with name = {0} doesn't exist", pName);
        }
        LOGGER.log(Level.INFO, "End search for business with name = {0}", pName);
        return businessEntity;
    }

    /**
     * Update a business with a given Id.
     *
     * @param pBusinessId the Id of the business to update
     * @param pBusiness the new business
     * @return the business entity after the update
     * @throws BusinessLogicException if the new business violates the business
     * rules
     */
    public BusinessEntity updateBusiness(Long pId, BusinessEntity pBusiness) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for business with id = {0}", pId);

        // update neighborhood
        BusinessEntity newEntity = businessPersistence.update(pBusiness);
        LOGGER.log(Level.INFO, "End the update process for business with id = {0}", pBusiness.getName());
        return newEntity;
    }

    /**
     * Deletes a business by ID.
     *
     * @param businessId the ID of the book to be deleted
     *
     */
    public void deleteBusiness(Long businessId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the delete process for business with id = {0}", businessId);
        businessPersistence.delete(businessId);
        LOGGER.log(Level.INFO, "End the delete process for business with id = {0}", businessId);
    }

    /**
     * Verifies that the the business is valid.
     *
     * @param pBusinessEntity business to verify
     * @return true if the business is valid. False otherwise
     * @throws BusinessLogicException if the business doesn't satisfy the
     * business rules
     */
    private boolean verifyBusinessCreationRules(BusinessEntity pBusinessEntity) throws BusinessLogicException {
        boolean valid = true;

        // the neighborhood the potential business belongs to 
        NeighborhoodEntity businessNeighborhood = pBusinessEntity.getNeighborhood();

        // 1. The business must have a neighborhood
        if (businessNeighborhood == null) {
            throw new BusinessLogicException("The business must have a neighborhood!");
        } // 2. The neighborhood to which the business will be added to must already exist
        else if (neighborhoodPersistence.find(businessNeighborhood.getId()) == null) {
            throw new BusinessLogicException("The business's neighborhood doesn't exist!");
        } // 3. No two businesses can have the same name
        else if (businessPersistence.findByName(pBusinessEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood already has a business with that name!");
        } // 4. The address of the business cannot be null
        else if (pBusinessEntity.getAddress() == null) {
            throw new BusinessLogicException("The business address cannot be null!");
        } // 5. The name of the business cannot be null
        else if (pBusinessEntity.getName() == null) {
            throw new BusinessLogicException("The business name cannot be null!");
        }

        return valid;

    }
}
