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

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a group within DB
     *
     * @param groupEntity group object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public GroupEntity create(GroupEntity groupEntity) {
        LOGGER.log(Level.INFO, "Creating a new group ");

        em.persist(groupEntity);
        LOGGER.log(Level.INFO, "Group created");
        return groupEntity;
    }

    /**
     * Returns all groups from DB belonging to a neighborhood.
     *
     * @param neighID: id from parent neighborhood.
     * @return a list with ll groups found in DB belonging to a neighborhood.
     */
    public List<GroupEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all groups from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From GroupEntity e where e.neighborhood.id = :neighID", GroupEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a group with the id and neighborhood id given by argument
     *
     * @param groupId: id from group to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a group.
     */
    public GroupEntity find(Long groupId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for group with id {0} belonging to neighborhood  {1}", new Object[]{groupId, neighborhood_id});

        GroupEntity e = em.find(GroupEntity.class, groupId);

        if (e != null) {
            if (e == null || e.getNeighborhood() == null || e.getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Group " + groupId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a group with the modified group given by argument belonging to a
     * neighborhood.
     *
     * @param groupEntity: the modified group.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated group
     */
    public GroupEntity update(GroupEntity groupEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Deleting group with id {0} belonging to neighborhood  {1}", new Object[]{groupEntity.getId(), neighborhood_id});

        find(groupEntity.getId(), neighborhood_id);

        return em.merge(groupEntity);
    }

    /**
     * Deletes from DB a group with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param groupId: id from group to be deleted.
     */
    public void delete(Long groupId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting group with id {0} belonging to neighborhood  {1}", new Object[]{groupId, neighborhood_id});
        GroupEntity e = find(groupId, neighborhood_id);

        em.remove(e);
    }

}
