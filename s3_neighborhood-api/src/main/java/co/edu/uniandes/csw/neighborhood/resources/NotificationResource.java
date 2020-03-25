/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.NotificationDTO;
import co.edu.uniandes.csw.neighborhood.ejb.NotificationLogic;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "notifications".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class NotificationResource {

    private static final Logger LOGGER = Logger.getLogger(NotificationResource.class.getName());

    @Inject
    private NotificationLogic notificationLogic;

    /**
     * Looks for all notifications on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link NotificationDTO} - All the notifications on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<NotificationDTO> getNotifications(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all notifications from resources: input: void");
        List<NotificationDTO> notifications = listEntity2DTO(notificationLogic.getNotifications(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all notifications from resources: output: {0}", notifications);
        return notifications;
    }

    /**
     * Looks for the notification with id received in the URL y returns it.
     *
     * @param notificationsId Id from wanted notification. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link NotificationDTO} - Wanted notification DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{notificationsId: \\d+}")
    public NotificationDTO getNotification(@PathParam("notificationsId") Long notificationsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  notification from resource: input: {0}", notificationsId);
        NotificationEntity notificationEntity = notificationLogic.getNotification(notificationsId, neighId);
        if (notificationEntity == null) {
            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
        }
        NotificationDTO detailDTO = new NotificationDTO(notificationEntity);
        LOGGER.log(Level.INFO, "Ended looking for notification from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates notification with id from URL with the information contained in
     * request body.
     *
     * @param notificationsId ID from notification to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param notification {@link NotificationDTO} Notification to be updated.
     * @return JSON {@link NotificationDTO} - Updated notification
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{notificationsId: \\d+}")
    public NotificationDTO updateAuthor(@PathParam("notificationsId") Long notificationsId, @PathParam("neighborhoodId") Long neighId, NotificationDTO notification) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating notification from resource: input: authorsId: {0} , author: {1}", new Object[]{notificationsId, notification});
        notification.setId(notificationsId);
        if (notificationLogic.getNotification(notificationsId, neighId) == null) {
            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
        }
        NotificationDTO detailDTO = new NotificationDTO(notificationLogic.updateNotification(notification.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating notification from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the notification with the associated id received in URL
     *
     * @param notificationsId id from notification to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{notificationsId: \\d+}")
    public void deleteNotification(@PathParam("notificationsId") Long notificationsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Deleting notification from resource: input: {0}", notificationsId);
        if (notificationLogic.getNotification(notificationsId, neighId) == null) {
            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
        }
        notificationLogic.deleteNotification(notificationsId, neighId);
        LOGGER.info("Notification deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for notifications.
     *
     * @param entityList Notification entity list to be converted.
     * @return Notification DTO list
     */
    private List<NotificationDTO> listEntity2DTO(List<NotificationEntity> entityList) {
        List<NotificationDTO> list = new ArrayList<>();
        for (NotificationEntity entity : entityList) {
            list.add(new NotificationDTO(entity));
        }
        return list;
    }
}
