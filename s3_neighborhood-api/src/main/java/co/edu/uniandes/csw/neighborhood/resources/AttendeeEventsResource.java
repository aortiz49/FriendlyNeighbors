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
package co.edu.uniandes.csw.neighborhood.resources;

//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.dtos.EventDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.AttendeeEventsLogic;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * Class implementing resource "residents/{id}/events".
 *
 * @author aortiz49
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AttendeeEventsResource {
//===================================================
// Attributes
//===================================================

    /**
     * Logs messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(FavorHelperResource.class.getName());

    /**
     * Injects EventAttendeesLogic dependencies.
     */
    @Inject
    private AttendeeEventsLogic attendeeEventsLogic;

    /**
     * Injects ResidentProfileLogic dependencies.
     */
    @Inject
    private EventLogic eventLogic;

//===================================================
// REST API
//=================================================== 
    /**
     * Adds an event to an existing attendee.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAttendeeId the attendee's id
     *
     * @return JSON {@link EventDetailDTO} with the information from the event
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found.
     */
    @POST
    @Path("{eventId: \\d+}")
    public EventDetailDTO associateEventToAttendee(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Begin adding an event to the attendee: "
                + "attendeeId {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        // adds the attendee to the event
        EventEntity eventEntity = attendeeEventsLogic.
                associateEventToAttendee(pNeighborhoodId, pAttendeeId, pEventId);

        // creates the resident [profile DTO
        EventDetailDTO eventDTO = new EventDetailDTO(eventEntity);

        LOGGER.log(Level.INFO, "End adding an event to the attendee: "
                + "attendeeId {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        return eventDTO;
    }

    /**
     * Returns all the events associated to an attendee.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pAttendeeId the attendee's id
     *
     * @return JSON array {@link EventDetailDTO} of events from an attendee. An empty list if none
     * is found.
     */
    @GET
    public List<EventDetailDTO> getAttendedEvents(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("attendeeId") Long pAttendeeId) {

        LOGGER.log(Level.INFO, "Begin searching for events from attendee {0}", pAttendeeId);

        // obtains the list of attendees
        List<EventDetailDTO> eventList = eventEntityList2DTOList(
                attendeeEventsLogic.getAttendedEvents(pNeighborhoodId, pAttendeeId));

        LOGGER.log(Level.INFO, "End searching for events from attendee {0}", pAttendeeId);

        return eventList;
    }

    /**
     * Returns an event with the associated id given by the URI.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAttendeeId the attendee's id
     *
     * @return JSON {@link EventDetailDTO} with the information from the event
     * @throws BusinessLogicException {@link BusinessLogicExcetionMapper} if there is a logic error
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found.
     */
    @GET
    @Path("{eventId: \\d+}")
    public EventDetailDTO getAttendedEvent(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException,
            BusinessLogicException {

        LOGGER.log(Level.INFO, "Begin searching for event from attendee: attendee {0} , eventId {1}",
                new Object[]{pAttendeeId, pEventId});

        // if the event doesn't exist, throw an exception
        if (eventLogic.getEvent(pNeighborhoodId, pEventId) == null) {
            throw new WebApplicationException("Resource /events/" + pEventId
                    + " does not exist.", 404);
        }

        // creates a eventDTO from found attendee
        EventDetailDTO eventDTO = new EventDetailDTO(
                attendeeEventsLogic.getAttendedEvent(pNeighborhoodId, pAttendeeId, pEventId));

        LOGGER.log(Level.INFO, "End searching for event from attendee: attendee {0} , eventId {1}",
                new Object[]{pAttendeeId, pEventId});

        return eventDTO;
    }

    /**
     * Replaces an attendee's events with a list of events from the request body.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pAttendeeId the attendee's id
     * @param pEventList the new list of attendees
     *
     * @return JSON array {@link EventDetailDTO} containing the updated list of attendees
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found
     */
    @PUT
    public List<EventDetailDTO> replaceEvents(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("attendeeId") Long pAttendeeId,
            List<EventDetailDTO> pEventList) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Begin replacing events from attendee: attendeesId {0} , event {1}",
                new Object[]{pEventList, pAttendeeId});

        for (EventDetailDTO event : pEventList) {
            if (eventLogic.getEvent(pNeighborhoodId, event.getId()) == null) {
                throw new WebApplicationException("Resource /events/" + event.getId()
                        + " does not exist.", 404);
            }
        }

        // convert DTO list to entity list
        List<EventEntity> entityList = eventDTOList2EntityList(pEventList);

        // replaces attendees's events
        List<EventDetailDTO> newEventsList = eventEntityList2DTOList(
                attendeeEventsLogic.replaceAttendedEvents(pNeighborhoodId, pAttendeeId, entityList));

        LOGGER.log(Level.INFO, "End replacing attendees frosm event: attendeesId {0} , event {1}",
                new Object[]{pEventList, pAttendeeId});

        return newEventsList;

    }

    /**
     * Removes an event with the associated id given by the URI.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAttendeeId the attendee's id
     *
     * @throws BusinessLogicException {@link BusinessLogicExcetionMapper} if there is a logic error
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found.
     */
    @DELETE
    @Path("{eventId: \\d+}")
    public void removeAttendee(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException,
            BusinessLogicException {

        LOGGER.log(Level.INFO, "Begin removing event from attendee:"
                + "attendeeID {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        if (eventLogic.getEvent(pNeighborhoodId, pEventId) == null) {
            throw new WebApplicationException("Resource /events/" + pEventId
                    + " does not exist.", 404);
        }
        attendeeEventsLogic.removeAttendedEvent(pNeighborhoodId, pAttendeeId, pEventId);

        LOGGER.log(Level.INFO, "Begin removing event from attendee:"
                + "attendeeID {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});
    }

//===================================================
// Converters
//=================================================== 
    /**
     * Converts a EventEntity list to to a EventDetailDTO list.
     *
     * @param pResidentList the resident list
     *
     * @return the EventDetailDTO list
     */
    private List<EventDetailDTO> eventEntityList2DTOList(
            List<EventEntity> pResidentList) {

        List<EventDetailDTO> entityList = new ArrayList<>();

        for (EventEntity entity : pResidentList) {
            entityList.add(new EventDetailDTO(entity));
        }
        return entityList;
    }

    /**
     * Converts a EventEntityDTO list to to a EventEntity list.
     *
     * @param pResidentDTOList the resident list
     *
     * @return the EventEntity list.
     */
    private List<EventEntity> eventDTOList2EntityList(List<EventDetailDTO> pResidentDTOList) {

        List<EventEntity> dtoList = new ArrayList<>();
        for (EventDetailDTO dto : pResidentDTOList) {
            dtoList.add(dto.toEntity());
        }
        return dtoList;
    }
}
