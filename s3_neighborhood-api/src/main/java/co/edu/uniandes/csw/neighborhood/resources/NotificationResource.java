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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author v.cardonac1
 */
@Path("notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class NotificationResource {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationResource.class.getName());
    
    @Inject
    private NotificationLogic notificationLogic;
    
    @POST
    public NotificationDTO createNotification(NotificationDTO notification) throws BusinessLogicException{
        NotificationEntity entity = notificationLogic.createNotification(notification.toEntity());
        NotificationDTO notificationDTO = new NotificationDTO(entity);
        return notification;
    }
    
    @GET
    @Path("{notificationsId: \\d+}")
    public NotificationDTO getNotification(@PathParam("notificationsId") Long notificationsId) {
        LOGGER.log(Level.INFO, "Looking for  notification from resource: input: {0}", notificationsId);
        NotificationEntity entity = notificationLogic.getNotification(notificationsId);
        if (entity == null) {
            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
        }
        NotificationDTO notificationDTO = new NotificationDTO(entity);
        LOGGER.log(Level.INFO, "Ended looking for notification from resource: output: {0}", notificationDTO);
        return notificationDTO;
    }
    
    @GET
    public List<NotificationDTO> getNotifications() {
        LOGGER.info("Looking for all notifications from resources: input: void");
        List<NotificationDTO> notifications = listEntity2DTO(notificationLogic.getNotifications());
        LOGGER.log(Level.INFO, "Ended looking for all notifications from resources: output: {0}", notifications);
        return notifications;
    }
    
    @DELETE
    @Path("{notificationsId: \\d+}")
    public void deleteNotification(@PathParam("notificationsId") Long notificationsId) {
        LOGGER.log(Level.INFO, "Deleting notification from resource: input: {0}", notificationsId);
        if (notificationLogic.getNotification(notificationsId) == null) {
            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
        }
        notificationLogic.deleteNotification(notificationsId);
        LOGGER.info("Resident deleted from resource: output: void");
    }
    
    @PUT
    @Path("{notificationsId: \\d+}")
    public NotificationDTO updateNotification(@PathParam("notificationsId") Long notificationsId, NotificationDTO notification) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating notification from resource: input: notificationsId: {0} , notifications: {1}", new Object[]{notificationsId, notification});
        notification.setId(notificationsId);
        if (notificationLogic.getNotification(notificationsId) == null) {
            throw new WebApplicationException("El recurso /notifications/" + notificationsId + " no existe.", 404);
        }
        NotificationDTO notificationDTO = new NotificationDTO(notificationLogic.updateNotification(notification.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating notification from resource: output: {0}", notificationDTO);

        return notificationDTO;
    }

    private List<NotificationDTO> listEntity2DTO(List<NotificationEntity> entityList) {
        List<NotificationDTO> list = new ArrayList<>();
        for (NotificationEntity entity : entityList) {
            list.add(new NotificationDTO(entity));
        }
        return list;    
    }
}
