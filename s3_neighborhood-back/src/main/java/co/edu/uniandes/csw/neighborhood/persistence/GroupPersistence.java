package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for GroupEntity. The connection is stablished
 * by Entity Manager from javax.persistance to the SQL DB.
 *
 * @author albayona
 */
@Stateless
public class GroupPersistence {

    private static final Logger LOGGER = Logger.getLogger(GroupPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU" )
    protected EntityManager em;

    /**
     * Creates a group within DB
     *
     * @param groupEntity group object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public GroupEntity create(GroupEntity groupEntity) {
        LOGGER.log(Level.INFO, "Creating a new group");

        em.persist(groupEntity);
        LOGGER.log(Level.INFO, "group created");
        return groupEntity;
    }

    
    /**
     * Returns all groups from DB.
     *
     * @return a list with all groups found in DB.
     */
    public List<GroupEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all groups");
        
        TypedQuery query = em.createQuery("select u from GroupEntity u", GroupEntity.class);
       
        return query.getResultList();
    }
    
        /**
     * Looks for a group with the id given by argument
     *
     * @param groupId: id from group to be found.
     * @return a group.
     */
    public GroupEntity find(Long groupId) {
        LOGGER.log(Level.INFO, "Querying for group with id={0}", groupId);
       
        
        return em.find(GroupEntity.class, groupId);
    }


        /**
     * Updates a group with the modified group given by argument.
     *
     * @param groupEntity: the modified group. 
     * @return the updated group
     */
    public GroupEntity update(GroupEntity groupEntity) {
        LOGGER.log(Level.INFO, "Updating group with id={0}", groupEntity.getId());
        return em.merge(groupEntity);
    }
    
        /**
     * Deletes from DB a group with the id given by argument
     *
     * @param groupId: id from group to be deleted.
     */
    public void delete(Long groupId) {

        LOGGER.log(Level.INFO, "Deleting group with id={0}", groupId);
        GroupEntity groupEntity = em.find(GroupEntity.class, groupId);
        em.remove(groupEntity);
    }
}
