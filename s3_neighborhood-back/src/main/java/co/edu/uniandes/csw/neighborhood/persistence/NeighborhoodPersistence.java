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

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Neighborhood. It connects via the Entity Manager in
 * javax.persistance with  SQL database.
 *
 * @author aortiz49
 */
@Stateless
public class NeighborhoodPersistence {
    //===================================================
    // Attributes
    //===================================================

    /**
     * Logger to log messages for the neighborhood persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(NeighborhoodPersistence.class.getName());

    /**
     * The entity manager that will access the Neighborhood table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a neighborhood in the database.
     *
     * @param pNeighborhoodEntity neighborhood object to be created in the databse
     * @return the created neighborhood with id given by the databse
     */
    public NeighborhoodEntity create(NeighborhoodEntity pNeighborhoodEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new neighborhood");

        // makes the entity instance managed and persistent
        em.persist(pNeighborhoodEntity);
        LOGGER.log(Level.INFO, "Neighborhood created");

        return pNeighborhoodEntity;
    }

    /**
     * Returns all neighborhoods in the database.
     *
     * @return a list containing every neighborhood in the database. select u from
     * NeighborhoodEntity u" is akin to a "SELECT * from NeighborhoodEntity" in SQL.
     */
    public List<NeighborhoodEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all neighborhoods");

        // Create a typed neighborhood entity query to find all neighborhoods 
        // in the database. 
        TypedQuery<NeighborhoodEntity> query
                = em.createQuery("select u from NeighborhoodEntity u", NeighborhoodEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a neighborhood with the id given by the parameter.
     *
     * @param pNeighborhoodId the id corresponding to the neighborhood
     * @return the found neighborhood
     */
    public NeighborhoodEntity find(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Consulting neighborhood with id={0}", pNeighborhoodId);

        return em.find(NeighborhoodEntity.class, pNeighborhoodId);
    }

    /**
     * Finds a neighborhood by name.
     *
     * @param pName the name of the neighborhood to search for
     * @return null if the neighborhood doesn't exist. If the neighborhood exists, return the first
     * one
     */
    public NeighborhoodEntity findByName(String pName) {
        LOGGER.log(Level.INFO, "Consulting neighborhood by name ", pName);

        // creates a query to search for neighborhoods with the name given by the parameter. ":pName" is a placeholder that must be replaced
        TypedQuery query = em.createQuery("Select e From NeighborhoodEntity e where e.name = :pName", NeighborhoodEntity.class);

        // the "pName" placeholder is replaced with the name of the parameter
        query = query.setParameter("pName", pName);

        // invokes the query and returns a list of results
        List<NeighborhoodEntity> sameName = query.getResultList();
        NeighborhoodEntity result;

        if (sameName == null || sameName.isEmpty()) {
            result = null;
        } else {
            result = sameName.get(0);
        }
        LOGGER.log(Level.INFO, "Exiting consultation of neighborhood by name ", pName);
        return result;
    }

    /**
     * Updates a neighborhood.
     *
     * @param pNeighborhoodEntity the neighborhood with the modifications. For example, the name
     * could have changed. In that case, we must use this update method.
     * @return the neighborhood with the updated changes
     */
    public NeighborhoodEntity update(NeighborhoodEntity pNeighborhoodEntity) {
        LOGGER.log(Level.INFO, "Updating neighborhood with id = {0}", pNeighborhoodEntity.getId());
        return em.merge(pNeighborhoodEntity);
    }

    /**
     * Deletes a neighborhood.
     * <p>
     * <p>
     * Deletes the neighborhood with the associated Id.
     *
     * @param pNeighborhoodId the id of the neighborhood to be deleted
     */
    public void delete(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Deleting neighborhood with id = {0}", pNeighborhoodId);
        NeighborhoodEntity reviewEntity = em.find(NeighborhoodEntity.class, pNeighborhoodId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO, "Exiting the deletion of neighborhood with id = {0}",
                pNeighborhoodId);
    }

}
