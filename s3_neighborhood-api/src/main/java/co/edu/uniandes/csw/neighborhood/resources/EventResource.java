/*
MIT License
Copyright (c) 2020 Universidad de los Andes - ISIS2603
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
package co.edu.uniandes.csw.neighborhood.resources;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.dtos.EventDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * Class that represents the "events" resource.
 *
 * @author aortiz49
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class EventResource {

//===================================================
// Attributes
//===================================================
    /**
     * Logger to send messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(FavorResource.class.getName());

    /**
     * Injects event logic dependencies.
     */
    @Inject
    private EventLogic eventLogic;

//===================================================
// REST API
//===================================================
    /**
     * Returns all events in a neighborhood.
     *
     * @param pNeighborhoodId the neighborhood's id
     *
     * @return JSON array {@link FavorDetailDTO} containing all events. Otherwise, returns an empty
     * list.
     */
    @GET
    public List<EventDetailDTO> getFavors(@PathParam("neighborhoodId") Long pNeighborhoodId) {

        LOGGER.log(Level.INFO, "Obtaining all events from neighborhood: input: {0}",
                pNeighborhoodId);

        // obtains lst of all events in the neighborhood
        List<EventDetailDTO> events = listEntity2DTO(eventLogic.getEvents(pNeighborhoodId));

        LOGGER.log(Level.INFO, "Finished obtaining all events from neighborhood: output: {0}",
                events);

        return events;
    }

    /**
     * Returns the event with the associated id given in the URL.
     *
     * @param pNeighborhoodId the id of the neighborhood
     * @param pEventId the id of the event to be found
     *
     * @return JSON array {@link EventDetailDTO} of the desired event DTO.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if the event does not
     * exist
     */
    @GET
    @Path("{eventId: \\d+}")
    public EventDetailDTO getEvent(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId) {

        LOGGER.log(Level.INFO, "Obtaining event : input: {0}", pEventId);

        EventEntity foundEvent = eventLogic.getEvent(pNeighborhoodId, pEventId);

        if (foundEvent == null) {
            throw new WebApplicationException("Resource /events/" + pEventId
                    + " does not exist.", 404);
        }

        EventDetailDTO foundEventDTO = new EventDetailDTO(foundEvent);

        LOGGER.log(Level.INFO, "Finished event : input: {0}", pEventId);

        return foundEventDTO;
    }

    /**
     * Updates an event with the associated id given in the URL with information contained in the
     * request body.
     *
     * @param pNeighborhoodId the id of the neighborhood
     * @param pEventId the id of the event to update
     * @param pEvent the request body of the event to update with
     *
     * @return JSON array {@link EventDetailDTO} of the updated event DTO.
     *
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if the new event
     * information violates logic rules
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if the event resource
     * to be modified does not exist
     */
    @PUT
    @Path("{eventId: \\d+}")
    public EventDetailDTO updateEvent(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId, EventDetailDTO pEvent)
            throws BusinessLogicException, WebApplicationException {

        LOGGER.log(Level.INFO, "Updating event from resource: input: eventId: {0} , event: {1}",
                new Object[]{pEventId, pEvent});

        // sets the id of the new event to the id of the original event
        pEvent.setId(pEventId);

        EventEntity original = eventLogic.getEvent(pNeighborhoodId, pEventId);

        // checks if the original event resource exists
        if (original == null) {
            throw new WebApplicationException("Resource /events/" + pEventId
                    + " does not exist.", 404);
        }

        // updates the event
        EventEntity updatedEvent = eventLogic.updateEvent(pNeighborhoodId, pEvent.toEntity());

        // creates a new EventDetailDTO from updated event
        EventDetailDTO eventDetailDTO = new EventDetailDTO(updatedEvent);

        LOGGER.log(Level.INFO, "Finished updating event from resource: input: eventId: {0} , "
                + "event: {1}", new Object[]{pEventId, pEvent});

        return eventDetailDTO;
    }

    /**
     * Deletes the event with the associated id given in the URL.
     *
     * @param pNeighborhoodId the id of the neighborhood
     * @param pEventId the id of the event to delete
     *
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if the event resource
     * to be deleted does not exist
     */
    @DELETE
    @Path("{eventId: \\d+}")
    public void deleteEvent(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("pEventId") Long pEventId) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Deleting event : input: {0}", pEventId);

        // obtains the event to be deleted
        EventEntity foundEvent = eventLogic.getEvent(pNeighborhoodId, pEventId);

        if (foundEvent == null) {
            throw new WebApplicationException("Resource /events/" + pEventId
                    + " does not exist.", 404);
        }

        // deletes the event
        eventLogic.deleteEvent(pNeighborhoodId, pEventId);

        LOGGER.log(Level.INFO, "Finished deleting event : input: {0}", pEventId);

    }

//===================================================
// Methods
//===================================================
    /**
     * Converts a list of event entities to a list of event DTOs.
     *
     * @param pEventEntityList list of event entities to be converted
     *
     * @return list of event DTOs
     */
    private List<EventDetailDTO> listEntity2DTO(List<EventEntity> pEventEntityList) {

        // list of event DTOs
        List<EventDetailDTO> list = new ArrayList<>();

        for (EventEntity entity : pEventEntityList) {
            list.add(new EventDetailDTO(entity));
        }

        return list;
    }

}
