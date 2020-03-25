/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NotificationPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class NotificationLogic {

    private static final Logger LOGGER = Logger.getLogger(NotificationLogic.class.getName());

    @Inject
    private NotificationPersistence persistence;
    @Inject
    private ResidentProfilePersistence residentPersistence;

    public NotificationEntity createNotification(NotificationEntity notificationEntity, Long residentId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for notification has started");
        ResidentProfileEntity r = residentPersistence.find(residentId, neighId);
        // Date posted can not be null
        if (notificationEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }
        // Priority can not be null
        if (notificationEntity.getPriority() == null) {
            throw new BusinessLogicException("A priority has to be specified");
        }
        // Header can not be null
        if (notificationEntity.getHeader() == null) {
            throw new BusinessLogicException("A header has to be specified");
        }
        // Description can not be null
        if (notificationEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }
        // Description length must be less than 250 characters
        if (notificationEntity.getDescription().length() > 250) {
            throw new BusinessLogicException("Description length must be less than 250 characters");
        }
        notificationEntity.setAuthor(r);
        persistence.create(notificationEntity);
        LOGGER.log(Level.INFO, "Creation process for notification eneded");

        return notificationEntity;
    }

    public void deleteNotification(Long id, Long neighID) {

        LOGGER.log(Level.INFO, "Starting deleting process for notification with id = {0}", id);
        persistence.delete(id, neighID);
        LOGGER.log(Level.INFO, "Ended deleting process for notification with id = {0}", id);
    }

    public List<NotificationEntity> getNotifications(Long neighID) {

        LOGGER.log(Level.INFO, "Starting querying process for all notifications");
        List<NotificationEntity> notifications = persistence.findAll(neighID);
        LOGGER.log(Level.INFO, "Ended querying process for all notifications");
        return notifications;
    }

    public NotificationEntity getNotification(Long id, Long neighID) {
        LOGGER.log(Level.INFO, "Starting querying process for notification with id {0}", id);
        NotificationEntity notification = persistence.find(id, neighID);
        LOGGER.log(Level.INFO, "Ended querying process for  notification with id {0}", id);
        return notification;
    }

    public NotificationEntity updateNotification(NotificationEntity notificationEntity, Long neighID) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for notification with id {0}", notificationEntity.getId());

        // Date posted can not be null
        if (notificationEntity.getPublishDate() == null) {
            throw new BusinessLogicException("A date has to be specified");
        }
        // Priority can not be null
        if (notificationEntity.getPriority() == null) {
            throw new BusinessLogicException("A priority has to be specified");
        }
        // Header can not be null
        if (notificationEntity.getHeader() == null) {
            throw new BusinessLogicException("A header has to be specified");
        }
        // Description can not be null
        if (notificationEntity.getDescription() == null) {
            throw new BusinessLogicException("A description has to be specified");
        }
        // Description length must be less than 250 characters
        if (notificationEntity.getDescription().length() > 250) {
            throw new BusinessLogicException("Description length must be less than 250 characters");
        }
        NotificationEntity original = persistence.find(notificationEntity.getId(), neighID);
        notificationEntity.setAuthor(original.getAuthor());
        NotificationEntity modified = persistence.update(notificationEntity, neighID);
        LOGGER.log(Level.INFO, "Ended update process for notification with id {0}", notificationEntity.getId());
        return modified;
    }
}
