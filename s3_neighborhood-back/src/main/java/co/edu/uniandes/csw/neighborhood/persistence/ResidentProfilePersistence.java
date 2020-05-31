package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for ResidentProfileEntity. The connection is
 * stablished by Entity Manager from javax.persistance to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class ResidentProfilePersistence {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfilePersistence.class.getName());

    @PersistenceContext(unitName = "NeighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a resident within DB
     *
     * @param residentEntity resident object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public ResidentProfileEntity create(ResidentProfileEntity residentEntity) {
        LOGGER.log(Level.INFO, "Creating a new resident ");

        em.persist(residentEntity);
        LOGGER.log(Level.INFO, "Resident created");
        return residentEntity;
    }

    /**
     * Returns all residents from DB belonging to a neighborhood.
     *
     * @param neighID: id from parent neighborhood.
     * @return a list with ll residents found in DB belonging to a
     * neighborhood.
     */
    public List<ResidentProfileEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all residents from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From ResidentProfileEntity e where e.neighborhood.id = :neighID", ResidentProfileEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a resident with the id and neighborhood id given by argument
     *
     * @param residentId: id from resident to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a resident.
     */
    public ResidentProfileEntity find(Long residentId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for resident with id {0} belonging to neighborhood  {1}", new Object[]{residentId, neighborhood_id});

        ResidentProfileEntity e = em.find(ResidentProfileEntity.class, residentId);

        if (e != null) {
            if (e.getNeighborhood() == null || !e.getNeighborhood().getId().equals(neighborhood_id)) {
                throw new RuntimeException("Resident " + residentId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a resident with the modified resident given by argument belonging
     * to a neighborhood.
     *
     * @param residentEntity: the modified resident.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated resident
     */
    public ResidentProfileEntity update(ResidentProfileEntity residentEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Deleting resident with id {0} belonging to neighborhood  {1}", new Object[]{residentEntity.getId(), neighborhood_id});

        find(residentEntity.getId(), neighborhood_id);
        
        

        return em.merge(residentEntity);
    }

    /**
     * Deletes from DB a resident with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param residentId: id from resident to be deleted.
     */
    public void delete(Long residentId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting resident with id {0} belonging to neighborhood  {1}", new Object[]{residentId, neighborhood_id});
        ResidentProfileEntity e = find(residentId, neighborhood_id);

        em.remove(e);
    }

    /**
     * Returns a resident given its email
     *
     * @param email email from resident
     * @return resident with wanted email
     */
    public ResidentProfileEntity findByEmail(String email) {

        LOGGER.log(Level.INFO, "Querying for resident with email ", email);

        TypedQuery query = em.createQuery("Select e From ResidentProfileEntity e where e.email = :email", ResidentProfileEntity.class);

        query = query.setParameter("email", email);

        List<ResidentProfileEntity> sameEmail = query.getResultList();
        ResidentProfileEntity result;
        if (sameEmail == null) {
            result = null;
        } else if (sameEmail.isEmpty()) {
            result = null;
        } else {
            result = sameEmail.get(0);
        }

        return result;
    }
}
