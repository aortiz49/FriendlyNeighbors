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

import co.edu.uniandes.csw.neighborhood.dtos.EventDTO;
import co.edu.uniandes.csw.neighborhood.dtos.EventDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.ejb.EventResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * Class that implements the "resident/{residentId}/events" resource.
 *
 * @author aortiz49
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class EventResidentProfileResource {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to send messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(
            EventResidentProfileResource.class.getName());

    /**
     * Injects event-residentProfile logic dependencies.
     */
    @Inject
    private EventResidentProfileLogic eventResidentProfileLogic;

    /**
     * Injects event logic dependencies.
     */
    @Inject
    private EventLogic eventLogic;

//===================================================
// REST API
//===================================================
    /**
     * Creates a hosted event with the information given in the request body.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pHostId the host's id
     * @param pHostedEvent the event to host
     *
     * @return JSON {@link EventDetailDTO} with the created event.
     *
     * @throws WebApplicationException {@link WebAplicationExceptionMapper} if the resources are not
     * found.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is a logic
     * error.
     */
    @POST
    public EventDetailDTO createHostedEvent(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pHostId, EventDTO pHostedEvent)
            throws WebApplicationException, BusinessLogicException {

        LOGGER.log(Level.INFO, "Creating event under a host from resource: input: "
                + "residentsId {0} , eventId {1}", new Object[]{pHostId, pHostedEvent.getId()});

        EventEntity eventEntity = eventLogic.createEvent(
                pNeighborhoodId, pHostId, pHostedEvent.getLocation().getId(),
                pHostedEvent.toEntity());

        EventDetailDTO eventDetailDTO = new EventDetailDTO(
                eventLogic.getEvent(pNeighborhoodId, eventEntity.getId()));

        LOGGER.log(Level.INFO, "Finished creating event under a resident from resource: input: "
                + "residentsId {0} , eventId {1}", new Object[]{pHostId, pHostedEvent.getId()});

        return eventDetailDTO;
    }

    /**
     * Returns all the events hosted by a resident.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pHostId the host's id
     *
     * @return JSON Array{@link EventDetailDTO} with events hosted by a resident.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is a logic
     * error.
     */
    @GET
    public List<EventDetailDTO> getHostedEvents(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pHostId) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Looking for events from host: input: {0}", pHostId);

        List<EventDetailDTO> eventList = entEntityList2DTOList(
                eventResidentProfileLogic.getHostedEvents(pNeighborhoodId, pHostId));

        LOGGER.log(Level.INFO, "Finished looking for events from host: input: {0}", pHostId);

        return eventList;
    }

    /**
     * Returns all the events hosted by a resident.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pHostId the host's id
     * @param pEventId the event's id
     *
     * @return JSON {@link EventDetailDTO} with the event hosted by a resident.
     * @throws WebApplicationException {@link WebAplicationExceptionMapper} if the resources are not
     * found.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is a logic
     * error.
     */
    @GET
    @Path("{eventId: \\d+}")
    public EventDetailDTO getHostedEvent(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pHostId, @PathParam("eventId") Long pEventId)
            throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Looking for event from host: input: eventId {0}"
                + "residentId {1}", new Object[]{pEventId, pHostId});

        if (eventLogic.getEvent(pNeighborhoodId, pEventId) == null) {
            throw new WebApplicationException("Resource /events/" + pEventId + " does not exist.", 404);
        }

        EventDetailDTO eventDetailDTO = new EventDetailDTO(eventResidentProfileLogic.
                getHostedEvent(pNeighborhoodId, pHostId, pEventId));

        LOGGER.log(Level.INFO, "Finished looking for event from host: input: eventId {0}"
                + "residentId {1}", new Object[]{pEventId, pHostId});
        return eventDetailDTO;
    }

//===================================================
// Converters
//===================================================
    /**
     * Converts an event entity list to an event DTO list.
     *
     * @param pEventEntityList the event entity list.
     * @return DTO list.
     */
    private List<EventDetailDTO> entEntityList2DTOList(List<EventEntity> pEventEntityList) {
        List<EventDetailDTO> list = new ArrayList<>();
        for (EventEntity event : pEventEntityList) {
            list.add(new EventDetailDTO(event));
        }
        return list;
    }
}
