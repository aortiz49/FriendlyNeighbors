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

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a resident within DB
     *
     * @param residentEntity resident object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public ResidentProfileEntity create(ResidentProfileEntity residentEntity) {
        LOGGER.log(Level.INFO, "Creating a new resident");

        em.persist(residentEntity);
        LOGGER.log(Level.INFO, "Resident created");
        return residentEntity;
    }

    /**
     * Returns all residents from DB.
     *
     * @return a list with all residents found in DB.
     */
    public List<ResidentProfileEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all residents");

        TypedQuery query = em.createQuery("select u from ResidentProfileEntity u", ResidentProfileEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a resident with the id given by argument
     *
     * @param residentId: id from resident to be found.
     * @return a resident.
     */
    public ResidentProfileEntity find(Long residentId) {
        LOGGER.log(Level.INFO, "Querying for resident with id={0}", residentId);

        return em.find(ResidentProfileEntity.class, residentId);
    }

    /**
     * Updates a resident with the modified resident given by argument.
     *
     * @param residentEntity: the modified resident. Por
     * @return the updated resident
     */
    public ResidentProfileEntity update(ResidentProfileEntity residentEntity) {
        LOGGER.log(Level.INFO, "Updating resident with id={0}", residentEntity.getId());
        return em.merge(residentEntity);
    }

    /**
     * Deletes from DB a resident with the id given by argument
     *
     * @param residentId: id from resident to be deleted.
     */
    public void delete(Long residentId) {

        LOGGER.log(Level.INFO, "Deleting resident wit id={0}", residentId);
        ResidentProfileEntity residentEntity = em.find(ResidentProfileEntity.class, residentId);
        em.remove(residentEntity);
    }
    
    

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
