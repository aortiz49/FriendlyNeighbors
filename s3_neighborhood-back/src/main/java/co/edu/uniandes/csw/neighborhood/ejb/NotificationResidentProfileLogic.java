/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

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
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NotificationPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author albayona
 */
@Stateless
public class NotificationResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(NotificationResidentProfileLogic.class.getName());

    @Inject
    private NotificationPersistence notificationPersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;


    /**
     * /**
     * Gets a collection of notifications entities associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId ID from resident entity
     * @return collection of notification entities associated with  resident
     */
    public List<NotificationEntity> getNotifications(Long residentId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all notifications belonging to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return residentPersistence.find(residentId, neighId).getNotifications();
    }

    /**
     * Gets a notification entity associated with  resident
     *
     * @param residentId Id from resident
     * @param neighId ID from parent neighborhood
     * @param notificationId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If notification is not associated
     */
    public NotificationEntity getNotification(Long residentId, Long notificationId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding notification with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{notificationId, residentId, neighId});
        List<NotificationEntity> notifications = residentPersistence.find(residentId, neighId).getNotifications();
        NotificationEntity NotificationEntity = notificationPersistence.find(notificationId, neighId);
        int index = notifications.indexOf(NotificationEntity);
        LOGGER.log(Level.INFO, "Found notification with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{notificationId, residentId, neighId});
        if (index >= 0) {
            return notifications.get(index);
        }
        throw new BusinessLogicException("Notification is not associated with resident");
    }

    /**
     * Replaces notifications associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId Id from resident
     * @param notifications Collection of notification to associate with resident
     * @return A new collection associated to resident
     */
    public List<NotificationEntity> replaceNotifications(Long residentId, List<NotificationEntity> notifications, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace notifications related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        ResidentProfileEntity resident = residentPersistence.find(residentId, neighId);
        List<NotificationEntity> notificationList = notificationPersistence.findAll(neighId);
        for (NotificationEntity notification : notificationList) {
            if (notifications.contains(notification)) {
                notification.setAuthor(resident);
            } else if (notification.getAuthor() != null && notification.getAuthor().equals(resident)) {
                notification.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced notifications related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return notifications;
    }

    /**
     * Removes a notification from resident. Notification is no longer in DB
     *
     * @param residentID Id from resident
     * @param neighId ID from parent neighborhood
     * @param notificationId Id from notification
     */
    public void removeNotification(Long residentID, Long notificationId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting notification with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{notificationId, residentID, neighId});

        notificationPersistence.delete(getNotification(residentID, notificationId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted notification with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{notificationId, residentID, neighId});
    }
}
