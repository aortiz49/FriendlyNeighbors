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

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Business. It connects via the
 * Entity Manager in javax.persistance with a SQL database.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessPersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the business persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            BusinessPersistence.class.getName());

    /**
     * The entity manager that will access the Business table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a business in the database.
     *
     * @param pBusinessEntity business object to be created in the
     * databse
     * @return the created business with an id given by the databse
     */
    public BusinessEntity create(BusinessEntity pBusinessEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new business");

        // makes the entity instance managed and persistent
        em.persist(pBusinessEntity);
        LOGGER.log(Level.INFO, "Business created");

        return pBusinessEntity;
    }

    /**
     * Returns all businesses in the database.
     *
     * @return a list containing every business in the database. select u
     * from BusinessEntity u" is akin to a "SELECT * from
     * BusinessEntity" in SQL.
     */
    public List<BusinessEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all neighborhoods");

        // Create a typed businessEntity query to find all businesses 
        // in the database. 
        TypedQuery<BusinessEntity> query = em.createQuery(
                "select u from BusinessEntity u", BusinessEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a business with the id given by the parameter.
     *
     * @param pBusinessId the id corresponding to the business
     * @return the found business
     */
    public BusinessEntity find(Long pBusinessId) {
        LOGGER.log(Level.INFO, "Consulting business with id={0}",
                pBusinessId);

        return em.find(BusinessEntity.class, pBusinessId);
    }

    /**
     * Updates a business.
     *
     * @param pBusinessEntity the business with the modifications. For
     * example, the name could have changed. In that case, we must use this
     * update method.
     * @return the business with the updated changes
     */
    public BusinessEntity update(BusinessEntity pBusinessEntity) {
        LOGGER.log(Level.INFO, "Updating business with id = {0}",
                pBusinessEntity.getId());
        return em.merge(pBusinessEntity);
    }

    /**
     * Deletes a business.
     * <p>
     *
     * Deletes the business with the associated Id.
     *
     * @param pBusinessId the id of the business to be deleted
     */
    public void delete(Long pBusinessId) {
        LOGGER.log(Level.INFO, "Deleting business with id = {0}",
                pBusinessId);
        BusinessEntity reviewEntity = em.find(BusinessEntity.class,
                pBusinessId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO,
                "Exiting the deletion of business with id = {0}",
                pBusinessId);
    }

}
