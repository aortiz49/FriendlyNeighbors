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
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements the connection for the relations between neighborhood and business.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessNeighborhoodLogic {
//==================================================
// Attributes
//===================================================

    /**
     * Logger that outputs to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessNeighborhoodLogic.class.getName());

    /**
     * Dependency injection for neighborhood persistence.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    /**
     * Dependency injection for business persistence.
     */
    @Inject
    private BusinessPersistence businessPersistence;

//===================================================
// Methods
//===================================================
    /**
     * Associates a Business to a Neighborhood.
     *
     * @param pBusinessId business id
     * @param pNeighborhoodId neighborhood id
     * @return the business instance that was associated to the neighborhood
     * @throws BusinessLogicException when the neighborhood or business don't exist
     */
    public BusinessEntity addBusinessToNeighborhood(Long pBusinessId, Long pNeighborhoodId) throws BusinessLogicException {

        // creates the logger
        LOGGER.log(Level.INFO, "Start association between business and neighborhood with id = {0}", pBusinessId);

        // finds existing neighborhood
        NeighborhoodEntity neighborhoodEntity = neighborhoodPersistence.find(pNeighborhoodId);

        // neighborhood must exist
        if (neighborhoodEntity == null) {
            throw new BusinessLogicException("The neighborhood must exist.");
        }

        // finds existing business
        BusinessEntity businessEntity = businessPersistence.find(pBusinessId);

        // business must exist
        if (businessEntity == null) {
            throw new BusinessLogicException("The business must exist.");
        }

        // set neighborhood of thr business
        businessEntity.setNeighborhood(neighborhoodEntity);

        // add the business to the neighborhood
        neighborhoodEntity.getBusinesses().add(businessEntity);

        LOGGER.log(Level.INFO, "End association between business and neighborhood with id = {0}", pBusinessId);
        return businessEntity;
    }

    /**
     * Gets a collection of business entities associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @return collection of business entities associated with a neighborhood
     */
    public List<BusinessEntity> getBusinesses(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Gets all businesses belonging to neighborhood with id = {0}", pNeighborhoodId);

        // returns the list of all businesses
        return neighborhoodPersistence.find(pNeighborhoodId).getBusinesses();
    }

    /**
     * Gets a service entity associated with a resident
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pBusinessId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If event is not associated
     */
    public BusinessEntity getBusiness(Long pNeighborhoodId, Long pBusinessId) throws BusinessLogicException {

        // logs start
        LOGGER.log(Level.INFO, "Finding business with id = {0} from neighborhood with = " + pBusinessId, pNeighborhoodId);

        // gets all the businesses in a neighborhood
        List<BusinessEntity> businesses = neighborhoodPersistence.find(pNeighborhoodId).getBusinesses();

        // the busines that was found
        int index = businesses.indexOf(businessPersistence.find(pBusinessId));

        // logs end
        LOGGER.log(Level.INFO, "Finish business query with id = {0} from neighborhood with = " + pBusinessId, pNeighborhoodId);

        // if the index doesn't exist
        if (index == -1) {
            throw new BusinessLogicException("Business is not associated with the neighborhood");
        }

        return businesses.get(index);
    }

    /**
     * Replaces businesses associated with a neighborhood
     *
     * @param pNeighborhoodId the neighborhood id
     * @param pNewBusinessesList Collection of service to associate with resident
     * @return A new collection associated to resident
     */
    public List<BusinessEntity> replaceBusinesses(Long pNeighborhoodId, List<BusinessEntity> pNewBusinessesList) {

        //logs start 
        LOGGER.log(Level.INFO, "Start replacing businesses related to neighborhood with id = {0}", pNeighborhoodId);

        // finds the neighborhood
        NeighborhoodEntity neighborhood = neighborhoodPersistence.find(pNeighborhoodId);

        // finds all the businesses
        List<BusinessEntity> currentBusinessesList = businessPersistence.findAll();

        // for all businesses in the database, check if a business in the new list already exists. 
        // if this business exists, change the neighborhood it is associated with
        for (int i = 0; i < currentBusinessesList.size(); i++) {
            BusinessEntity current = currentBusinessesList.get(i);
            if (pNewBusinessesList.contains(current)) {
                current.setNeighborhood(neighborhood);
            } // if the current business in the list has the desired neighborhood as its neighborhood,
            // set this neighborhood to null since it is not in the list of businesses we want the 
            // neighborhood to have
            else if (current.getNeighborhood().equals(neighborhood)) {
                current.setNeighborhood(null);
            }
        }

        // logs end
        LOGGER.log(Level.INFO, "End replacing businesses related to neighborhood with id = {0}", pNeighborhoodId);

        return pNewBusinessesList;
    }

    /**
     * Removes a business from a neighborhood.
     *
     * @param pNeighborhoodId Id from resident
     * @param pBusinessId Id from service
     */
    public void removeBusiness(Long pNeighborhoodId, Long pBusinessId) {
        LOGGER.log(Level.INFO, "Start removing a business from neighborhood with id = {0}", pBusinessId);

        // removes the business from the database
        businessPersistence.delete(pBusinessId);

        LOGGER.log(Level.INFO, "Finished removing a business from neighborhood with id = {0}", pBusinessId);
    }
}
