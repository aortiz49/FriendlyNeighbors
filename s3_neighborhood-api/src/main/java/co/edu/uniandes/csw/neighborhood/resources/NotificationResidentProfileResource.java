///*
//MIT License
//
//Copyright (c) 2017 Universidad de los Andes - ISIS2603
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
//package co.edu.uniandes.csw.neighborhood.resources;
//
//import co.edu.uniandes.csw.neighborhood.dtos.NotificationDTO;
//
//import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
//import co.edu.uniandes.csw.neighborhood.ejb.NotificationLogic;
//import co.edu.uniandes.csw.neighborhood.ejb.NotificationResidentProfileLogic;
//import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
//import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
//import java.util.List;
//import javax.inject.Inject;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.ws.rs.WebApplicationException;
//
///**
// * Class implementing resource "resident/{notificationId}/notifications".
// *
// * @author v.cardonac1
// * @version 1.0
// */
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//public class NotificationResidentProfileResource {
//
//    private static final Logger LOGGER = Logger.getLogger(NotificationResidentProfileResource.class.getName());
//
//    @Inject
//    private NotificationResidentProfileLogic residentNotificationLogic;
//
//    @Inject
//    private NotificationLogic notificationLogic;
//
//    /**
//     * Creates a notification with existing resident
//     *
//     * @param notificationsId notificationId from notification to be associated
//     * @param residentsId notificationId from resident
//     * @return JSON {@link NotificationDTO} -
//     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
//     * Logic error if not found
//     */
//    @POST
//    public NotificationDTO createNotificationForResidentProfile(@PathParam("residentsId") Long residentsId, NotificationDTO notification) throws BusinessLogicException {
//        LOGGER.log(Level.INFO, "Creating notification for resident from resource: input: residentsId {0} , notificationsId {1}", new Object[]{residentsId, notification.getId()});
//
//        NotificationEntity entity = null;
//
//        entity = notificationLogic.createNotification(notification.toEntity());
//        
//        Long notificationId = entity.getId();
//        
//        residentNotificationLogic.addNotificationToResidentProfile(notificationId, residentsId);
//
//        NotificationDTO dto = new NotificationDTO(notificationLogic.getNotification(notificationId));
//        LOGGER.log(Level.INFO, "Ended creating notification for resident from resource: output: {0}", dto.getId());
//        return dto;
//    }
//
//    /**
//     * Looks for all the notifications associated to a resident and returns it
//     *
//     * @param residentsId notificationId from resident whose notifications are wanted
//     * @return JSONArray {@link NotificationDTO} - notifications found in resident. An
//     * empty list if none is found
//     */
//    @GET
//    public List<NotificationDTO> getNotifications(@PathParam("residentsId") Long residentsId) {
//        LOGGER.log(Level.INFO, "Looking for notifications from resources: input: {0}", residentsId);
//        List<NotificationDTO> list = notificationsListEntity2DTO(residentNotificationLogic.getNotifications(residentsId));
//        LOGGER.log(Level.INFO, "Ended looking for notifications from resources: output: {0}", list);
//        return list;
//    }
//
//    /**
//     * Looks for a notification with specified ID by URL which is associated with 
//     * resident and returns it
//     *
//     * @param notificationsId notificationId from wanted notification
//     * @param residentsId notificationId from resident whose notification is wanted
//     * @return {@link NotificationDTO} - notification found inside resident
//     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
//     * Logic error if notification not found
//     */
//    @GET
//    @Path("{notificationsId: \\d+}")
//    public NotificationDTO getNotification(@PathParam("residentsId") Long residentsId, @PathParam("notificationsId") Long notificationsId) throws BusinessLogicException {
//        LOGGER.log(Level.INFO, "Looking for notification: input: residentsId {0} , notificationsId {1}", new Object[]{residentsId, notificationsId});
//        if (notificationLogic.getNotification(notificationsId) == null) {
//            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
//        }
//        NotificationDTO detailDTO = new NotificationDTO(residentNotificationLogic.getNotification(residentsId, notificationsId));
//        LOGGER.log(Level.INFO, "Ended looking for notification: output: {0}", detailDTO);
//        return detailDTO;
//    }
//
//    /**
//     *
//     * Updates a list from notifications inside a resident which is received in body
//     *
//     * @param residentsId notificationId from resident whose list of notifications is to be updated
//     * @param notifications JSONArray {@link NotificationDTO} - modified notifications list
//     * @return JSONArray {@link NotificationDTO} - updated list
//     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
//     * Error if not found
//     */
//    @PUT
//    public List<NotificationDTO> replaceNotifications(@PathParam("residentsId") Long residentsId, List<NotificationDTO> notifications) {
//        LOGGER.log(Level.INFO, "Replacing resident notifications from resource: input: residentsId {0} , notifications {1}", new Object[]{residentsId, notifications});
//        for (NotificationDTO notification : notifications) {
//            if (notificationLogic.getNotification(notification.getId()) == null) {
//                throw new WebApplicationException("Resource /notifications/" + notifications + " does not exist.", 404);
//            }
//        }
//        List<NotificationDTO> lista = notificationsListEntity2DTO(residentNotificationLogic.replaceNotificationes(residentsId, notificationsListDTO2Entity(notifications)));
//        LOGGER.log(Level.INFO, "Ended replacing resident notifications from resource: output:{0}", lista);
//        return lista;
//    }
//
//    /**
//     * Removes a notification from resident
//     *
//     * @param residentsId notificationId from resident whose notification is to be removed
//     * @param notificationsId notificationId from notification to be removed
//     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
//     * Error if not found
//     */
//    @DELETE
//    @Path("{notificationsId: \\d+}")
//    public void removeNotification(@PathParam("residentsId") Long residentsId, @PathParam("notificationsId") Long notificationsId) throws BusinessLogicException {
//        LOGGER.log(Level.INFO, "removing otification from resident: input: residentsId {0} , notificationsId {1}", new Object[]{residentsId, notificationsId});
//        if (notificationLogic.getNotification(notificationsId) == null) {
//            throw new WebApplicationException("Resource /notifications/" + notificationsId + " does not exist.", 404);
//        }
//        residentNotificationLogic.removeNotification(residentsId, notificationsId);
//        LOGGER.info("Ended removing otification from resident: output: void");
//    }
//
//    /**
//     * Converts an entity list with notifications to a DTO list.
//     *
//     * @param entityList entity list.
//     * @return DTO list.
//     */
//    private List<NotificationDTO> notificationsListEntity2DTO(List<NotificationEntity> entityList) {
//        List<NotificationDTO> list = new ArrayList<>();
//        for (NotificationEntity entity : entityList) {
//            list.add(new NotificationDTO(entity));
//        }
//        return list;
//    }
//
//    /**
//     * Converts a DTO list with notifications to an entity list.
//     *
//     * @param dtos DTO list.
//     * @return entity list.
//     */
//    private List<NotificationEntity> notificationsListDTO2Entity(List<NotificationDTO> dtos) {
//        List<NotificationEntity> list = new ArrayList<>();
//        for (NotificationDTO dto : dtos) {
//            list.add(dto.toEntity());
//        }
//        return list;
//    }
//}
