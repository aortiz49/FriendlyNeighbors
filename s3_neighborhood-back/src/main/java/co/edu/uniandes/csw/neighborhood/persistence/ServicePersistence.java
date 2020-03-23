package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * This class handles persistence for ServiceEntity. The connection is set by
 * Entity Manager from javax.persistence to the SQL DB.
 *
 * @author aortiz49
 */
@Stateless
public class ServicePersistence {

    private static final Logger LOGGER = Logger.getLogger(ServicePersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    /**
     * Creates a service within DB
     *
     * @param serviceEntity service object to be created in DB
     * @return returns the created entity with id given by DB.
     */
    public ServiceEntity create(ServiceEntity serviceEntity) {
        LOGGER.log(Level.INFO, "Creating a new service ");

        em.persist(serviceEntity);
        LOGGER.log(Level.INFO, "Service created");
        return serviceEntity;
    }

    /**
     * Returns all services from DB belonging to a neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @return a list with ll services found in DB belonging to a neighborhood.
     */
    public List<ServiceEntity> findAll(Long neighID) {

        LOGGER.log(Level.INFO, "Querying for all services from neighborhood ", neighID);

        TypedQuery query = em.createQuery("Select e From ServiceEntity e where e.author.neighborhood.id = :neighID", ServiceEntity.class);

        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }

    /**
     * Looks for a service with the id and neighborhood id given by argument
     *
     * @param serviceId: id from service to be found.
     * @param neighborhood_id: id from parent neighborhood.
     * @return a service.
     */
    public ServiceEntity find(Long serviceId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Querying for service with id {0}" + serviceId + " belonging to " + neighborhood_id);

        ServiceEntity e = em.find(ServiceEntity.class, serviceId);

        if (e != null) {
            if (e.getAuthor() == null || e.getAuthor().getNeighborhood() == null || e.getAuthor().getNeighborhood().getId() != neighborhood_id) {
                throw new RuntimeException("Service " + serviceId + " does not belong to neighborhood " + neighborhood_id);
            }
        }

        return e;
    }

    /**
     * Updates a service with the modified service given by argument belonging
     * to a neighborhood.
     *
     * @param serviceEntity: the modified service.
     * @param neighborhood_id: id from parent neighborhood.
     * @return the updated service
     */
    public ServiceEntity update(ServiceEntity serviceEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating service with id={0}", serviceEntity.getId());

        find(serviceEntity.getId(), neighborhood_id);

        return em.merge(serviceEntity);
    }

    /**
     * Deletes from DB a service with the id given by argument belonging to a
     * neighborhood.
     *
     * @param neighborhood_id: id from parent neighborhood.
     * @param serviceId: id from service to be deleted.
     */
    public void delete(Long serviceId, Long neighborhood_id) {

        LOGGER.log(Level.INFO, "Deleting service wit id={0}", serviceId);
        ServiceEntity e = find(serviceId, neighborhood_id);

        em.remove(e);
    }

}
