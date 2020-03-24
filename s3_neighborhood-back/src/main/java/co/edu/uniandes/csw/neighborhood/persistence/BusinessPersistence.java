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
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Business. It connects via the Entity Manager in
 * javax.persistance with SQL database.
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
     * The entity manager that will access the business table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a business in the database.
     *
     * @param pBusinessEntity business object to be created in the database
     *
     * @return the created business with id given by the database
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
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return a list containing every business in the database. "select u from BusinessUntity u" is
     * akin to: "SELECT * from BusinessEntity" in SQL.
     */
    public List<BusinessEntity> findAll(Long pNeighborhoodId) {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all businesses");

        // Create a typed business entity query to find all businesses 
        // in the database. 
        TypedQuery query = em.createQuery(
                "Select e From BusinessEntity e where e.owner.neighborhood.id = :pNeighborhoodId",
                BusinessEntity.class);

        query = query.setParameter("pNeighborhoodId", pNeighborhoodId);

        return query.getResultList();
    }

    /**
     * Looks for a business with the id given by the parameter.
     *
     * @param pBusinessId the id corresponding to the business
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return the found business
     */
    public BusinessEntity find(Long pBusinessId, Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Querying for business with id {0} belonging to neighborhood  {1}",
                new Object[]{pBusinessId, pNeighborhoodId});

        BusinessEntity foundbusiness = em.find(BusinessEntity.class, pBusinessId);
        if (!foundbusiness.getNeighborhood().getId().equals(pNeighborhoodId)) {
            throw new RuntimeException("Business " + pBusinessId + " does not belong to neighborhood " + pNeighborhoodId);
        }

        return foundbusiness;
    }

    /**
     * Finds a business by name.
     *
     * @param pName the name of the business to
     * @return null if the business doesn't exist. If the business exists, return the first one
     */
    public BusinessEntity findByName(String pName) {
        LOGGER.log(Level.INFO, "Consulting business by name ", pName);

        // creates a query to search for books with the name given by the parameter. ":pName" is a placeholder that must be replaced
        TypedQuery query = em.createQuery("Select e From BusinessEntity e where e.name = :pName", BusinessEntity.class);

        // the "pName" placeholder is replaced with the name of the parameter
        query = query.setParameter("pName", pName);

        // invokes the query and returns a list of results
        List<BusinessEntity> sameName = query.getResultList();
        BusinessEntity result;

        if (sameName == null || sameName.isEmpty()) {
            result = null;
        } else {
            result = sameName.get(0);
        }

        LOGGER.log(Level.INFO, "Exiting consultation of business by name ", pName);
        return result;
    }

    /**
     * Updates a business.
     *
     * @param pBusinessEntity the business with the modifications. For example, the name could have
     * changed. In that case, we must use this update method.
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return the business with the updated changes
     */
    public BusinessEntity update(BusinessEntity pBusinessEntity, Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Updating business with id = {0}",
                pBusinessEntity.getId());

        find(pBusinessEntity.getId(), pNeighborhoodId);
        return em.merge(pBusinessEntity);
    }

    /**
     * Deletes a business.
     * <p>
     *
     * Deletes the business with the associated Id.
     *
     * @param pBusinessId the id of the business to be deleted
     * @param pNeighborhoodId the id from parent neighborhood.
     */
    public void delete(Long pBusinessId, Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Deleting business with id = {0}", pBusinessId);
        BusinessEntity business = find(pBusinessId, pNeighborhoodId);

        em.remove(business);
        LOGGER.log(Level.INFO, "Exiting the deletion of business with id = {0}", pBusinessId);
    }

}
