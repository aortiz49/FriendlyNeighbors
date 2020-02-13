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

import co.edu.uniandes.csw.neighborhood.entities.BusinessOwnerProfileEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the BusinessOwnerProfile. It connects
 * via the Entity Manager in javax.persistance with a SQL database.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessOwnerProfilePersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the business owner profile persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            BusinessOwnerProfilePersistence.class.getName());

    /**
     * The entity manager that will access the business owner profile table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a business owner profile in the database.
     *
     * @param pBusinessOwnerProfileEntity business owner profile object to be
     * created in the databse
     * @return the created business owner profile with an id given by the
     * databse
     */
    public BusinessOwnerProfileEntity create(
            BusinessOwnerProfileEntity pBusinessOwnerProfileEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new business owner profile");

        // makes the entity instance managed and persistent
        em.persist(pBusinessOwnerProfileEntity);
        LOGGER.log(Level.INFO, "Business owner profile created");

        return pBusinessOwnerProfileEntity;
    }

    /**
     * Returns all business owner profiles in the database.
     *
     * @return a list containing every business in the database. select u from
     * BusinessOwnerProfileEntity u" is akin to a "SELECT * from
     * BusinessOwnerProfileEntity" in SQL.
     */
    public List<BusinessOwnerProfileEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all business owner profiles");

        // Create a typed businessOwnerProfileEntity query to find all business 
        // owner profiles in the database. 
        TypedQuery<BusinessOwnerProfileEntity> query = em.createQuery(
                "select u from BusinessOwnerProfileEntity u", 
                BusinessOwnerProfileEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a business owner profile with the id given by the parameter.
     *
     * @param pBusinessOwnerProfileId the id corresponding to the business owner
     * profile
     * @return the found business owner profile
     */
    public BusinessOwnerProfileEntity find(Long pBusinessOwnerProfileId) {
        LOGGER.log(Level.INFO, "Consulting business owner profile with id={0}",
                pBusinessOwnerProfileId);

        return em.find(BusinessOwnerProfileEntity.class, 
                pBusinessOwnerProfileId);
    }

    /**
     * Updates a business owner profile.
     *
     * @param pBusinessOwnerProfileEntity the business owner profile with the
     * modifications. For example, the name could have changed. In that case, we
     * must use this update method.
     * @return the business owner profile with the updated changes
     */
    public BusinessOwnerProfileEntity update(BusinessOwnerProfileEntity 
            pBusinessOwnerProfileEntity) {
        LOGGER.log(Level.INFO, "Updating business owner profile with id = {0}",
                pBusinessOwnerProfileEntity.getId());
        return em.merge(pBusinessOwnerProfileEntity);
    }

    /**
     * Deletes a business owner profile.
     * <p>
     *
     * Deletes the business owner profile with the associated Id.
     *
     * @param pBusinessOwnerProfileId the id of the business owner profile to be
     * deleted
     */
    public void delete(Long pBusinessOwnerProfileId) {
        LOGGER.log(Level.INFO, "Deleting business owner profile with id = {0}",
                pBusinessOwnerProfileId);
        BusinessOwnerProfileEntity reviewEntity = em.find(
                BusinessOwnerProfileEntity.class, pBusinessOwnerProfileId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO,
                "Exiting the deletion of business owner profile with id = {0}",
                pBusinessOwnerProfileId);
    }

}
