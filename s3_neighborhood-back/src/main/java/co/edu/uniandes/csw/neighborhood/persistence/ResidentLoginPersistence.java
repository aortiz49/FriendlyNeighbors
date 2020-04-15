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
import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for ResidentLoginEntity. The connection is set by Entity Manager
 * from javax.persistence to the SQL DB.
 *
 * @author aortiz49
 */
@Stateless
public class ResidentLoginPersistence {
//===================================================
// Attributes
//===================================================

    private static final Logger LOGGER = Logger.getLogger(ResidentLoginPersistence.class.getName());

    @PersistenceContext(unitName = "NeighborhoodPU")
    protected EntityManager em;
//===================================================
// CRUD methods
//===================================================

    /**
     * Creates a residentLogin within DB
     *
     * @param residentLoginEntity residentLogin object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public ResidentLoginEntity create(ResidentLoginEntity residentLoginEntity) {
        LOGGER.log(Level.INFO, "Creating a new residentLogin ");

        em.persist(residentLoginEntity);
        LOGGER.log(Level.INFO, "Service created");
        return residentLoginEntity;
    }

    /**
     * Returns all residentLogins from DB belonging to a neighborhood.
     *
     * @param pNeighborhoodId: id from parent neighborhood.
     * @return a list with ll residentLogins found in DB belonging to a neighborhood.
     */
    public List<ResidentLoginEntity> findAll(Long pNeighborhoodId) {

        LOGGER.log(Level.INFO, "Querying for all residentLogins from neighborhood ", pNeighborhoodId);

        TypedQuery query = em.createQuery("Select e From ResidentLoginEntity e where e.resident.neighborhood.id = :pNeighborhoodId", ResidentLoginEntity.class);

        query = query.setParameter("pNeighborhoodId", pNeighborhoodId);

        return query.getResultList();
    }

    /**
     * Looks for a residentLogin with the id and neighborhood id given by argument
     *
     * @param pResidentLoginId: id from residentLogin to be found.
     * @param pNeighborhoodId: id from parent neighborhood.
     * @return a residentLogin.
     */
    public ResidentLoginEntity find(Long pNeighborhoodId, Long pResidentLoginId) {
        LOGGER.log(Level.INFO, "Querying for residentLogin with id '{'0'}'{0} belonging to {1}", new Object[]{pResidentLoginId, pNeighborhoodId});

        ResidentLoginEntity login = em.find(ResidentLoginEntity.class, pResidentLoginId);
        
        if (login != null) {
            if (login.getNeighborhood() == null
                    || !login.getNeighborhood().getId().equals(pNeighborhoodId)) {
                throw new RuntimeException("Login " + pResidentLoginId + " does not belong to "
                        + "neighborhood " + pNeighborhoodId);
            }
        }


        return login;
    }

    /**
     * Updates a residentLogin with the modified residentLogin given by argument belonging to a
     * neighborhood.
     *
     * @param residentLoginEntity: the modified residentLogin.
     * @param pNeighborhoodId: id from parent neighborhood.
     * @return the updated residentLogin
     */
    public ResidentLoginEntity update(Long pNeighborhoodId, ResidentLoginEntity residentLoginEntity) {
        LOGGER.log(Level.INFO, "Updating residentLogin with id={0}", residentLoginEntity.getId());

        find(residentLoginEntity.getId(), pNeighborhoodId);

        return em.merge(residentLoginEntity);
    }

    /**
     * Deletes from DB a residentLogin with the id given by argument belonging to a neighborhood.
     *
     * @param pNeighborhoodId: id from parent neighborhood.
     * @param pResidentLoginId: id from residentLogin to be deleted.
     */
    public void delete(Long pNeighborhoodId, Long pResidentLoginId) {

        LOGGER.log(Level.INFO, "Deleting residentLogin wit id={0}", pResidentLoginId);
        ResidentLoginEntity e = find(pNeighborhoodId, pResidentLoginId);

        em.remove(e);
    }

}
