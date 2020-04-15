/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.persistence;

import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class NotificationPersistence {
    private static final Logger LOGGER = Logger.getLogger(NotificationPersistence.class.getName());

    @PersistenceContext(unitName = "NeighborhoodPU")
    protected EntityManager em;
    
    /**
     * Method to persist the entity in the database.
     *
     * @param notificationEntity notification object to be created in the database.
     * @return returns the entity created with id given by the database.
     */
    public NotificationEntity create(NotificationEntity notificationEntity) {
        LOGGER.log(Level.INFO, "Creating a new notification");
        em.persist(notificationEntity);
        LOGGER.log(Level.INFO, "Notification created");
        return notificationEntity;
    }
    
    /**
     * Returns all notifications from the database.
     *
     * @param neighID
     * @return a list with ll the notifications found in the database, "select u from NotificationEntity u" is like a "select 
     * from NotificationEntity;" - "SELECT * FROM table_name" en SQL.
     */
    public List<NotificationEntity> findAll(Long neighID) {
        LOGGER.log(Level.INFO, "Querying for all posts from neighborhood ", neighID);
        TypedQuery query = em.createQuery("Select e From NotificationEntity e where e.author.neighborhood.id = :neighID", NotificationEntity.class);
        query = query.setParameter("neighID", neighID);

        return query.getResultList();
    }
    
    /**
     * Search if there is any notification with the id that is sent as an argument
     *
     * @param notificationId: id corresponding to the notification sought
     * @return a notification.
     */
    public NotificationEntity find(Long notificationId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Consulting notification with the id = {0}", notificationId);
        NotificationEntity e = em.find(NotificationEntity.class, notificationId);
        if (e != null) {
            if (e.getAuthor() == null || e.getAuthor().getNeighborhood() == null || !e.getAuthor().getNeighborhood().getId().equals(neighborhood_id)) {
                throw new RuntimeException("Notification " + notificationId + " does not belong to neighborhood " + neighborhood_id);
            }
        }
        return e;
    }
    
    /**
     * update a notification.
     *
     * @param notificationEntity: the notification that comes with the new changes.
     * @return a notification with the changes applied
     */
    public NotificationEntity update(NotificationEntity notificationEntity, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Updating notification with the id = {0}", notificationEntity.getId());
        find(notificationEntity.getId(), neighborhood_id);

        return em.merge(notificationEntity);
    }

    /**
     *
     * Delete a notification from the database receiving the id of the notification as an argument
     *
     * @param notificationId: id corresponding to the notification to delete.
     */
    public void delete(Long notificationId, Long neighborhood_id) {
        LOGGER.log(Level.INFO, "Deleting notification with id = {0}", notificationId);
        NotificationEntity e = find(notificationId, neighborhood_id);

        em.remove(e);
    }
}
