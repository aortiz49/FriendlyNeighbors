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

import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Persistence class for Location.
 *
 * @author v.cardonac1
 * @version 2.0 (This version filters the location by neighborhood)
 */
@Stateless
public class LocationPersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the neighborhood persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationPersistence.class.getName());

    /**
     * The entity manager that will access the Location table.
     */
    @PersistenceContext(unitName = "NeighborhoodPU")
    protected EntityManager em;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Method to persist the entity in the database.
     *
     * @param pLocationEntity location object to be created in the database.
     * @return returns the entity created with id given by the database.
     */
    public LocationEntity create(LocationEntity pLocationEntity) {
        LOGGER.log(Level.INFO, "Creating a new location");

        // persists the location in the database
        em.persist(pLocationEntity);

        LOGGER.log(Level.INFO, "Location created");
        return pLocationEntity;
    }

    /**
     * Returns all location from the database.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the location
     *
     * @return a list with ll locations in a neighborhood.
     */
    public List<LocationEntity> findAll(Long pNeighborhoodId) {
        LOGGER.log(Level.INFO, "Consulting all locations");

        // Create a typed location entity query to find all locationes 
        // in the database. 
        TypedQuery query = em.createQuery(
                "Select e From LocationEntity e where e.neighborhood.id = :pNeighborhoodId",
                LocationEntity.class);

        query = query.setParameter("pNeighborhoodId", pNeighborhoodId);

        return query.getResultList();
    }

    /**
     * Returns a location associated to the id given by the parameter.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pLocationId the location's id
     *
     * @return the found location
     */
    public LocationEntity find(Long pNeighborhoodId, Long pLocationId) {

        LOGGER.log(Level.INFO, "Querying for location with id {0} belonging to neighborhood  {1}",
                new Object[]{pLocationId, pNeighborhoodId});

        LocationEntity foundLocation = em.find(LocationEntity.class, pLocationId);

        if (foundLocation != null) {
            if (foundLocation.getNeighborhood() == null
                    || !foundLocation.getNeighborhood().getId().equals(pNeighborhoodId)) {
                throw new RuntimeException("Location " + pLocationId + " does not belong to "
                        + "neighborhood " + pNeighborhoodId);
            }
        }

        return foundLocation;
    }

    /**
     * Finds a location by name.
     *
     * @param pName the name of the location to find
     *
     * @return null if the location doesn't exist. If the location exists, return the first one
     */
    public LocationEntity findByName(String pName) {
        LOGGER.log(Level.INFO, "Consulting location by name ", pName);

        // creates a query to search for books with the name given by the parameter. 
        // ":pName" is a placeholder that must be replaced
        TypedQuery query = em.createQuery("Select e From LocationEntity e where e.name = :pName", LocationEntity.class);

        // the "pName" placeholder is replaced with the name of the parameter
        query = query.setParameter("pName", pName);

        // invokes the query and returns a list of results
        List<LocationEntity> sameName = query.getResultList();
        LocationEntity result;

        if (sameName == null || sameName.isEmpty()) {
            result = null;
        } else {
            result = sameName.get(0);
        }

        LOGGER.log(Level.INFO, "Exiting consultation of location by name ", pName);
        return result;
    }

    /**
     * Updates a location.
     *
     * @param pLocationEntity the location with the modifications. For example, the name could have
     * changed. In that case, we must use this update method.
     * @param pNeighborhoodId the id from parent neighborhood.
     *
     * @return the location with the updated changes
     */
    public LocationEntity update(Long pNeighborhoodId, LocationEntity pLocationEntity) {
        LOGGER.log(Level.INFO, "Updating location with id = {0}",
                pLocationEntity.getId());

        find(pNeighborhoodId,pLocationEntity.getId());
        return em.merge(pLocationEntity);
    }

    /**
     * Deletes a location.
     * <p>
     *
     * Deletes the location with the associated Id.
     *
     * @param pLocationId the id of the location to be deleted
     * @param pNeighborhoodId the id from parent neighborhood.
     */
    public void delete(Long pNeighborhoodId, Long  pLocationId) {
        LOGGER.log(Level.INFO, "Deleting location with id = {0}", pLocationId);
        LocationEntity location = find(pNeighborhoodId, pLocationId);

        em.remove(location);
        LOGGER.log(Level.INFO, "Exiting the deletion of location with id = {0}", pLocationId);
    }
}
