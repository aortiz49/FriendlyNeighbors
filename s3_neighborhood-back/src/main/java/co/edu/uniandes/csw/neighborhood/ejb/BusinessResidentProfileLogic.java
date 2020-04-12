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
package co.edu.uniandes.csw.neighborhood.ejb;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between ResidentProfile and Business.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessResidentProfileLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessResidentProfileLogic.class.getName());

    /**
     * Dependency injection for business persistence.
     */
    @Inject
    private BusinessPersistence businessPersistence;

    /**
     * Dependency injection for residentProfile persistence.
     */
    @Inject
    private ResidentProfilePersistence residentProfilePersistence;

    /**
     * Dependency injection for neighborhood persistence.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Gets a collection of business entities associated with residentProfile
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pResidentProfileId the residentProfile id
     * @return collection of business entities associated with residentProfile
     */
    public List<BusinessEntity> getBusinesses(Long pNeighborhoodId, Long pResidentProfileId) {
        LOGGER.log(Level.INFO, "Gets all businesses belonging to residentProfile with id = {0}", pResidentProfileId);

        // returns the list of all businesses
        return residentProfilePersistence.find(pResidentProfileId, pNeighborhoodId).getBusinesses();
    }

    /**
     * Gets a service entity associated with resident
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessId the id of associated business
     *
     * @return the desired business entity
     * @throws BusinessLogicException if the business doesn't exist
     */
    public BusinessEntity getBusiness(Long pNeighborhoodId,
            Long pOwnerId, Long pBusinessId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding business with id {0} associated to "
                + "resident with id {1}, from neighbothood {2}",
                new Object[]{pBusinessId, pOwnerId, pNeighborhoodId});
        // gets all the businesses in a residentProfile
        List<BusinessEntity> businesses = residentProfilePersistence.find(
                pOwnerId, pNeighborhoodId).getBusinesses();

        // the busines that was found
        int index = businesses.indexOf(businessPersistence.find(pNeighborhoodId, pBusinessId));

        // logs end
        LOGGER.log(Level.INFO, "Finished searching for business with id {0} associated to "
                + "resident with id {1}, from neighbothood {2}",
                new Object[]{pBusinessId, pOwnerId, pNeighborhoodId});

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Business is not associated with the residentProfile");
        }

        return businesses.get(index);
    }

    //////   
    // TODO: Update
    /////
    /**
     * Replaces businesses associated with resident
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessList the list of associated businesses
     *
     * @return A new business collection associated to a resident
     */
    public List<BusinessEntity> replaceBusinesses(Long pNeighborhoodId, Long pOwnerId,
            List<BusinessEntity> pBusinessList) {
        LOGGER.log(Level.INFO, "Replacing businesses owned by resident with id {0} from "
                + "neighborhood {1}", new Object[]{pOwnerId, pNeighborhoodId});

        // the business owner
        ResidentProfileEntity owner = residentProfilePersistence.find(pOwnerId, pNeighborhoodId);

        // the neighborhood the business is in
        NeighborhoodEntity neighborhood = neighborhoodPersistence.find(pNeighborhoodId);

        // list of all business in the neighborhood
        List<BusinessEntity> businessList = businessPersistence.findAll(pNeighborhoodId);

        // for every business in the neighborhood..
        for (BusinessEntity business : businessList) {

            // if the neighborhood contains the business in the new list, set its new owner 
            if (pBusinessList.contains(business)) {
                business.setOwner(owner);
                business.setNeighborhood(neighborhood);
            } // if the neighborhood doesn't contain the business, set it's owner to null
            else if (business.getOwner() != null && business.getOwner().equals(owner)) {
                business.setOwner(null);
            }
        }

        LOGGER.log(Level.INFO, "Replaced businesses related to resident id {0} from "
                + "neighborhood {1}", new Object[]{pOwnerId, pNeighborhoodId});

        return pBusinessList;
    }

    /**
     * Removes a business from resident.
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessId the id of the associated businesses
     * @throws BusinessLogicException if the business to delete doesn't exist
     */
    public void removeBusiness(Long pNeighborhoodId, Long pOwnerId, Long pBusinessId)
            throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Deleting business with id {0} associated to resident with id {1}, "
                + "from neighborhood {2}", new Object[]{pBusinessId, pOwnerId, pNeighborhoodId});

        Long toDelete = getBusiness(pNeighborhoodId, pOwnerId, pBusinessId).getId();

        businessPersistence.delete(pNeighborhoodId, toDelete);

        LOGGER.log(Level.INFO, "Deleted post with id {0} associated to resident with id {1}, "
                + "from neighborhood {2}", new Object[]{pBusinessId, pOwnerId, pNeighborhoodId});
    }
}
