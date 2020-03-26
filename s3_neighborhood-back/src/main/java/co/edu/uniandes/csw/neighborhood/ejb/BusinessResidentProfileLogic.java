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
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
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
     * Dependency injection for residentProfile persistence.
     */
    @Inject
    private ResidentProfilePersistence residentProfilePersistence;

    /**
     * Dependency injection for business persistence.
     */
    @Inject
    private BusinessPersistence businessPersistence;

//===================================================
// Methods
//===================================================
//    
//    /**
//     * Gets a collection of business entities associated with  residentProfile
//     *
//     * @param pResidentProfileId the residentProfile id
//     * @return collection of business entities associated with  residentProfile
//     */
//    public List<BusinessEntity> getBusinesses(Long pResidentProfileId) {
//        LOGGER.log(Level.INFO, "Gets all businesses belonging to residentProfile with id = {0}", pResidentProfileId);
//
//        // returns the list of all businesses
//        return residentProfilePersistence.find(pResidentProfileId).getBusinesses();
//    }

    /**
     * Gets a service entity associated with  resident
     * 
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessId the id of associated business
     * 
     * @return the desired business entity
     * @throws BusinessLogicException if the business doesn't exist
     */
    public BusinessEntity getBusiness(Long pNeighborhoodId, 
            Long pResidentId, Long pBusinessId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding business with id {0} associated to "
                + "resident with id {1}, from neighbothood {2}", 
                new Object[]{pBusinessId, pResidentId, pNeighborhoodId});

        // gets all the businesses in a residentProfile
        List<BusinessEntity> businesses = residentProfilePersistence.find(
                pResidentId,pNeighborhoodId).getBusinesses();

        // the busines that was found
        int index = businesses.indexOf(businessPersistence.find(pNeighborhoodId,pBusinessId));

        // logs end
        LOGGER.log(Level.INFO, "Finished searching for business with id {0} associated to "
                + "resident with id {1}, from neighbothood {2}", 
                new Object[]{pBusinessId, pResidentId, pNeighborhoodId});

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Business is not associated with the residentProfile");
        }

        return businesses.get(index);
    }

//    /**
//     * Replaces businesses associated with  residentProfile
//     *
//     * @param pResidentProfileId the residentProfile id
//     * @param pNewBusinessesList Collection of service to associate with resident
//     * @return A new collection associated to resident
//     */
//    public List<BusinessEntity> replaceBusinesses(Long pResidentProfileId, List<BusinessEntity> pNewBusinessesList) {
//
//        //logs start 
//        LOGGER.log(Level.INFO, "Start replacing businesses related to residentProfile with id = {0}", pResidentProfileId);
//
//        // finds the residentProfile
//        ResidentProfileEntity residentProfile = residentProfilePersistence.find(pResidentProfileId);
//
//        // finds all the businesses
//        List<BusinessEntity> currentBusinessesList = businessPersistence.findAll();
//
//        // for all businesses in the database, check if a business in the new list already exists. 
//        // if this business exists, change the residentProfile it is associated with
//        for (int i = 0; i < currentBusinessesList.size(); i++) {
//            BusinessEntity current = currentBusinessesList.get(i);
//            if (pNewBusinessesList.contains(current)) {
//                current.setOwner(residentProfile);
//            } // if the current business in the list has the desired residentProfile as its residentProfile,
//            // set this residentProfile to null since it is not in the list of businesses we want the 
//            // residentProfile to have
//            else if (current.getOwner().equals(residentProfile)) {
//                current.setOwner(null);
//            }
//        }
//
//        // logs end
//        LOGGER.log(Level.INFO, "End replacing businesses related to residentProfile with id = {0}", pResidentProfileId);
//
//        return pNewBusinessesList;
//    }
//
//    /**
//     * Removes a business from residentProfile.
//     *
//     * @param pResidentProfileId Id from resident
//     * @param pBusinessId Id from service
//     */
//    public void removeBusiness(Long pResidentProfileId, Long pBusinessId) {
//       LOGGER.log(Level.INFO, "Start removing business from residentProfile with id = {0}", pBusinessId);
//
//        // desired residentProfile
//        ResidentProfileEntity residentProfile = residentProfilePersistence.find(pResidentProfileId);
//
//        // business to delete
//        BusinessEntity business = businessPersistence.find(pBusinessId);
//
//        // business to remove from residentProfile   
//        residentProfile.getBusinesses().remove(business);
//        
//        // group to remove from event
//        business.setOwner(null);
//
//        LOGGER.log(Level.INFO, "Finished removing  event from group con id = {0}", pBusinessId);
//    }
}
