///*
//MIT License
//
//Copyright (c) 2020 Universidad de los Andes - ISIS2603
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all
//copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//SOFTWARE.
// */
//package co.edu.uniandes.csw.neighborhood.ejb;
////===================================================
//// Imports
////===================================================
//
//import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
//import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
//import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
//import co.edu.uniandes.csw.neighborhood.persistence.NotificationPersistence;
//import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Class that implements the connection for the relations between ResidentProfile and Notification.
// *
// * @author aortiz49
// */
//@Stateless
//public class NotificationResidentProfileLogic {
////==================================================
//// Attributes
////===================================================
//
//    /**
//     * Logger that outputs to the console.
//     */
//    private static final Logger LOGGER = Logger.getLogger(NotificationResidentProfileLogic.class.getName());
//
//    /**
//     * Dependency injection for residentProfile persistence.
//     */
//    @Inject
//    private ResidentProfilePersistence residentProfilePersistence;
//
//    /**
//     * Dependency injection for notification persistence.
//     */
//    @Inject
//    private NotificationPersistence notificationPersistence;
//
////===================================================
//// Methods
////===================================================
//    /**
//     * Associates a Notification to a ResidentProfile.
//     *
//     * @param pNotificationId notification id
//     * @param pResidentProfileId residentProfile id
//     * @return the notification instance that was associated to the residentProfile
//     * @throws BusinessLogicException when the residentProfile or notification don't exist
//     */
//    public NotificationEntity addNotificationToResidentProfile(Long pNotificationId, Long pResidentProfileId) throws BusinessLogicException {
//
//        // creates the logger
//        LOGGER.log(Level.INFO, "Start association between notification and residentProfile with id = {0}", pNotificationId);
//
//        // finds existing residentProfile
//        ResidentProfileEntity residentProfileEntity = residentProfilePersistence.find(pResidentProfileId);
//
//        // residentProfile must exist
//        if (residentProfileEntity == null) {
//            throw new BusinessLogicException("The residentProfile must exist.");
//        }
//
//        // finds existing notification
//        NotificationEntity notificationEntity = notificationPersistence.find(pNotificationId);
//
//        // notification must exist
//        if (notificationEntity == null) {
//            throw new BusinessLogicException("The notification must exist.");
//        }
//
//        // set residentProfile of thr notification
//        notificationEntity.setAuthor(residentProfileEntity);
//
//        // add the notification to the residentProfile
//        residentProfileEntity.getNotifications().add(notificationEntity);
//
//        LOGGER.log(Level.INFO, "End association between notification and residentProfile with id = {0}", pNotificationId);
//        return notificationEntity;
//    }
//
//    /**
//     * Gets a collection of notification entities associated with  residentProfile
//     *
//     * @param pResidentProfileId the residentProfile id
//     * @return collection of notification entities associated with  residentProfile
//     */
//    public List<NotificationEntity> getNotifications(Long pResidentProfileId) {
//        LOGGER.log(Level.INFO, "Gets all notificationes belonging to residentProfile with id = {0}", pResidentProfileId);
//
//        // returns the list of all notificationes
//        return residentProfilePersistence.find(pResidentProfileId).getNotifications();
//    }
//
//    /**
//     * Gets a service entity associated with  resident
//     *
//     * @param pResidentProfileId the residentProfile id
//     * @param pNotificationId Id from associated entity
//     * @return associated entity
//     * @throws BusinessLogicException If event is not associated
//     */
//    public NotificationEntity getNotification(Long pResidentProfileId, Long pNotificationId) throws BusinessLogicException {
//
//        // logs start
//        LOGGER.log(Level.INFO, "Finding notification with id = {0} from residentProfile with = " + pNotificationId, pResidentProfileId);
//
//        // gets all the notificationes in a residentProfile
//        List<NotificationEntity> notificationes = residentProfilePersistence.find(pResidentProfileId).getNotifications();
//
//        // the busines that was found
//        int index = notificationes.indexOf(notificationPersistence.find(pNotificationId));
//
//        // logs end
//        LOGGER.log(Level.INFO, "Finish notification query with id = {0} from residentProfile with = " + pNotificationId, pResidentProfileId);
//
//        // if the index doesn't exist
//        if (index == -1) {
//            throw new BusinessLogicException("Notification is not associated with the residentProfile");
//        }
//
//        return notificationes.get(index);
//    }
//
//    /**
//     * Replaces notificationes associated with  residentProfile
//     *
//     * @param pResidentProfileId the residentProfile id
//     * @param pNewNotificationesList Collection of service to associate with resident
//     * @return A new collection associated to resident
//     */
//    public List<NotificationEntity> replaceNotificationes(Long pResidentProfileId, List<NotificationEntity> pNewNotificationesList) {
//
//        //logs start 
//        LOGGER.log(Level.INFO, "Start replacing notificationes related to residentProfile with id = {0}", pResidentProfileId);
//
//        // finds the residentProfile
//        ResidentProfileEntity residentProfile = residentProfilePersistence.find(pResidentProfileId);
//
//        // finds all the notificationes
//        List<NotificationEntity> currentNotificationesList = notificationPersistence.findAll();
//
//        // for all notificationes in the database, check if a notification in the new list already exists. 
//        // if this notification exists, change the residentProfile it is associated with
//        for (int i = 0; i < currentNotificationesList.size(); i++) {
//            NotificationEntity current = currentNotificationesList.get(i);
//            if (pNewNotificationesList.contains(current)) {
//                current.setAuthor(residentProfile);
//            } // if the current notification in the list has the desired residentProfile as its residentProfile,
//            // set this residentProfile to null since it is not in the list of notificationes we want the 
//            // residentProfile to have
//            else if (current.getAuthor().equals(residentProfile)) {
//                current.setAuthor(null);
//            }
//        }
//
//        // logs end
//        LOGGER.log(Level.INFO, "End replacing notificationes related to residentProfile with id = {0}", pResidentProfileId);
//
//        return pNewNotificationesList;
//    }
//
//    /**
//     * Removes a notification from residentProfile.
//     *
//     * @param pResidentProfileId Id from resident
//     * @param pNotificationId Id from service
//     */
//    public void removeNotification(Long pResidentProfileId, Long pNotificationId) {
//        LOGGER.log(Level.INFO, "Start removing otification from residentProfile with id = {0}", pNotificationId);
//
//        // desired residentProfile
//        ResidentProfileEntity residentProfile = residentProfilePersistence.find(pResidentProfileId);
//
//        // notification to delete
//        NotificationEntity notification = notificationPersistence.find(pNotificationId);
//
//        // notification to remove from residentProfile   
//        residentProfile.getNotifications().remove(notification);
//
//        // group to remove from event
//        notification.setAuthor(null);
//
//        LOGGER.log(Level.INFO, "Finished removing  event from group con id = {0}", pNotificationId);
//    }
//}
