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
import javax.persistence.Query;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class NotificationPersistence {
    private static final Logger LOGGER = Logger.getLogger(NotificationPersistence.class.getName());

    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;
    
    /**
     * Method to persist the entity in the database.
     *
     * @param notificationEntity notification object to be created in the database.
     * @return returns the entity created with an id given by the database.
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
     * @return a list with all the notifications found in the database, "select u from NotificationEntity u" is like a "select 
     * from NotificationEntity;" - "SELECT * FROM table_name" en SQL.
     */
    public List<NotificationEntity> findAll() {
        LOGGER.log(Level.INFO, "Consulting all notifications");
        Query q = em.createQuery("select u from NotificationEntity u");
        return q.getResultList();
    }
    
    /**
     * Search if there is any notification with the id that is sent as an argument
     *
     * @param notificationId: id corresponding to the notification sought
     * @return a notification.
     */
    public NotificationEntity find(Long notificationId) {
        LOGGER.log(Level.INFO, "Consulting notification with the id = {0}", notificationId);
        return em.find(NotificationEntity.class, notificationId);
        
    }
    
    /**
     * update a notification.
     *
     * @param notificationEntity: the notification that comes with the new changes.
     * @return a notification with the changes applied
     */
    public NotificationEntity update(NotificationEntity notificationEntity) {
        LOGGER.log(Level.INFO, "Updating notification with the id = {0}", notificationEntity.getId());
        return em.merge(notificationEntity);
    }

    /**
     *
     * Delete a notification from the database receiving the id of the notification as an argument
     *
     * @param notificationId: id corresponding to the notification to delete.
     */
    public void delete(Long notificationId) {
        LOGGER.log(Level.INFO, "Deleting notification with id = {0}", notificationId);
        NotificationEntity entity = em.find(NotificationEntity.class, notificationId);
        em.remove(entity);
    }
}
