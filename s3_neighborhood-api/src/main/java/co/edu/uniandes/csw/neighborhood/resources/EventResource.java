/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.EventDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
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
 * Class implementing resource "events".
 *
 * @author K.romero
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class EventResource {

    private static final Logger LOGGER = Logger.getLogger(EventResource.class.getName());

    @Inject
    private EventLogic eventLogic;

    /**
     * Looks for all events on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link EventDetailDTO} - All the events on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<EventDetailDTO> getEvents(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all events from resources: input: void");
        List<EventDetailDTO> events = listEntity2DTO(eventLogic.getEvents(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all events from resources: output: {0}", events);
        return events;
    }

    /**
     * Looks for the event with id received in the URL y returns it.
     *
     * @param eventsId Id from wanted event. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link EventDetailDTO} - Wanted event DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{eventsId: \\d+}")
    public EventDetailDTO getEvent(@PathParam("eventsId") Long eventsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  event from resource: input: {0}", eventsId);
        EventEntity eventEntity = eventLogic.getEvent(eventsId, neighId);
        if (eventEntity == null) {
            throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        EventDetailDTO detailDTO = new EventDetailDTO(eventEntity);
        LOGGER.log(Level.INFO, "Ended looking for event from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates event with id from URL with the information contained in
     * request body.
     *
     * @param eventsId ID from event to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param event {@link EventDetailDTO} Event to be updated.
     * @return JSON {@link EventDetailDTO} - Updated event
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{eventsId: \\d+}")
    public EventDetailDTO updateAuthor(@PathParam("eventsId") Long eventsId, @PathParam("neighborhoodId") Long neighId, EventDetailDTO event) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating event from resource: input: authorsId: {0} , author: {1}", new Object[]{eventsId, event});
        event.setId(eventsId);
        if (eventLogic.getEvent(eventsId, neighId) == null) {
            throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        EventDetailDTO detailDTO = new EventDetailDTO(eventLogic.updateEvent(event.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating event from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the event with the associated id received in URL
     *
     * @param eventsId id from event to be deleted
     * @param neighId parent neighborhood
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{eventsId: \\d+}")
    public void deleteEvent(@PathParam("eventsId") Long eventsId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting event from resource: input: {0}", eventsId);
        if (eventLogic.getEvent(eventsId, neighId) == null) {
            throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        eventLogic.deleteEvent(eventsId, neighId);
        LOGGER.info("Event deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for events.
     *
     * @param entityList Event entity list to be converted.
     * @return Event DTO list
     */
    private List<EventDetailDTO> listEntity2DTO(List<EventEntity> entityList) {
        List<EventDetailDTO> list = new ArrayList<>();
        for (EventEntity entity : entityList) {
            list.add(new EventDetailDTO(entity));
        }
        return list;
    }


}
