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
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection with the businessPersistence for the Business entity.
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

    /**
     * The injected resident profile persistence object.
     */
    @Inject
    private ResidentProfilePersistence residentProfilePersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists a new business
     *
     * @param pBusinessEntity the entity of type Business of the new business to be persisted.
     * @param pNeighborhoodId the id of the neighborhood containing the business
     *
     * @return the business entity after it is persisted
     * @throws BusinessLogicException if the new business violates the business rules
     */
    public BusinessEntity createBusiness(BusinessEntity pBusinessEntity, Long pNeighborhoodId) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin creating a business");
        
        System.out.println("############ DEBUG "+ pBusinessEntity.getOwner().getName());

        // 1. No two businesses can have the same name
        if (businessPersistence.findByName(pBusinessEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood already has a business with that name!");
        }
        
        // verify business rules for creating a new business
        verifyBusinessCreationRules(pBusinessEntity);

        // set the business's neighborhood 
        pBusinessEntity.setNeighborhood(neighborhoodPersistence.find(pNeighborhoodId));
        
        // set the business's owner
        pBusinessEntity.setOwner(residentProfilePersistence.find(pBusinessEntity.getOwner().getId(), pNeighborhoodId));

        // create the business
        BusinessEntity createdEntity = businessPersistence.create(pBusinessEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End creating a business");
        return createdEntity;
    }

    /**
     * Returns all the businesses in the database.
     *
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return list of businesses
     */
    public List<BusinessEntity> getBusinesses(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Begin consulting all businesses");
        List<BusinessEntity> businesses = businessPersistence.findAll(pNeighborhoodId);
        LOGGER.log(Level.INFO, "End consulting all businesses");
        return businesses;
    }

    /**
     * Finds a business by ID.
     *
     * @param pBusinessId the id corresponding to the business
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return the found business, null if not found
     */
    public BusinessEntity getBusiness(Long pBusinessId, Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Begin search for business with Id = {0}", pBusinessId);
        BusinessEntity entity = businessPersistence.find(pBusinessId, pNeighborhoodId);

        if (entity == null) {
            LOGGER.log(Level.SEVERE, "The business with Id = {0} doesn't exist", pBusinessId);
        }
        LOGGER.log(Level.INFO, "End search for bsiness with Id = {0}", pBusinessId);
        return entity;
    }

    /**
     * Finds a business by name.
     *
     * @param pName the name of the business to find
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
     * Update a business with given Id.
     *
     * @param pBusinessEntity the new business
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return the business entity after the update
     * @throws BusinessLogicException if the new business violates the business rules
     */
    public BusinessEntity updateBusiness(BusinessEntity pBusinessEntity, Long pNeighborhoodId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Begin the update process for business with id = {0}", pBusinessEntity.getId());

        // find original business
        BusinessEntity original = businessPersistence.find(pBusinessEntity.getId(), pNeighborhoodId);

        // No two businesses can have the same name
        if (!original.getName().equals(pBusinessEntity.getName())) {

            if (businessPersistence.findByName(pBusinessEntity.getName()) != null) {
                throw new BusinessLogicException("The neighborhood already has a business with that name!");
            }
        }
        
        // verify that the new business meets the rules
        verifyBusinessCreationRules(pBusinessEntity);

        // set the business's neighborhood 
        pBusinessEntity.setNeighborhood(neighborhoodPersistence.find(pNeighborhoodId));
        
        // update business
        BusinessEntity newEntity = businessPersistence.update(pBusinessEntity, pNeighborhoodId);

        LOGGER.log(Level.INFO, "End the update process for business with id = {0}", pBusinessEntity.getName());
        return newEntity;

    }

    /**
     * Deletes a business by ID.
     *
     * @param pBusinessId the ID of the business to be deleted
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     */
    public void deleteBusiness(Long pBusinessId, Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Begin the delete process for business with id = {0}", pBusinessId);
        businessPersistence.delete(pBusinessId, pNeighborhoodId);
        LOGGER.log(Level.INFO, "End the delete process for business with id = {0}", pBusinessId);
    }

    /**
     * Verifies that the the business is valid.
     *
     * @param pBusinessEntity business to verify
     *
     * @return true if the business is valid. False otherwise
     * @throws BusinessLogicException if the business doesn't satisfy the business rules
     */
    private boolean verifyBusinessCreationRules(BusinessEntity pBusinessEntity) throws BusinessLogicException {
        boolean valid = true;

        // 2. The address of the business cannot be null
        if (pBusinessEntity.getAddress() == null) {
            throw new BusinessLogicException("The business address cannot be null!");
        }

        // 3. The name of the business cannot be null
        if (pBusinessEntity.getName() == null) {
            throw new BusinessLogicException("The business name cannot be null!");
        }

        // 4. The business must have an owner
        if (pBusinessEntity.getOwner() == null) {
            throw new BusinessLogicException("The business must have an owner!");
        }

        ResidentProfileEntity businessOwner = pBusinessEntity.getOwner();

        // 5. The business owner must exist
        if (residentProfilePersistence.find(businessOwner.getId(),
                pBusinessEntity.getNeighborhood().getId()) == null) {
            throw new BusinessLogicException("The business owner must exist!");
        }

        String openingTime = pBusinessEntity.getOpeningTime();
        String closingTime = pBusinessEntity.getClosingTime();

        // 6. Opening time must not be null
        if (openingTime == null) {
            throw new BusinessLogicException("The business opening time cannot be null!");
        }

        // 7. Closing time must not be null
        if (closingTime == null) {
            throw new BusinessLogicException("The business closing time cannot be null!");
        }

        // date format for the opening and closing times
        DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");

        // placeholder dates represening the times
        Date open = null;
        Date close = null;

        // 8.Opening and closing times must be legal hh:mm aa format
        try {
            open = timeFormat.parse(openingTime);
            close = timeFormat.parse(closingTime);
        } catch (ParseException e) {
            throw new BusinessLogicException("The business times have an illegal format!");
        }

        // 9. Opening time must come before the closing time
        if (open.compareTo(close) > 0) {
            throw new BusinessLogicException("The business opening time must come before the closing time!");
        }

        return valid;
    }
}
