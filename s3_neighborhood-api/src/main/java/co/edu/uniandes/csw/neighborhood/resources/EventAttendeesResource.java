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
import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.EventAttendeesLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
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
 * Class implementing resource "events/{id}/attendees".
 *
 * @author aortiz49
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventAttendeesResource {
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
    private EventAttendeesLogic eventAttendeesLogic;

    /**
     * Injects ResidentProfileLogic dependencies.
     */
    @Inject
    private ResidentProfileLogic residentProfileLogic;

//===================================================
// REST API
//=================================================== 
    /**
     * Adds an attendee to an existing event.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAttendeeId the attendee's id
     *
     * @return JSON {@link ResidentProfileDetailDTO} with the information from the attendee
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found.
     */
    @POST
    @Path("{attendeeId: \\d+}")
    public ResidentProfileDetailDTO associateAttendeeToEvent(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Begin adding an attendee to the event: "
                + "attendeeId {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        // adds the attendee to the event
        ResidentProfileEntity attendeeEntity = eventAttendeesLogic.
                associateAttendeeToEvent(pNeighborhoodId, pEventId, pAttendeeId);

        // creates the resident [profile DTO
        ResidentProfileDetailDTO attendeeDTO = new ResidentProfileDetailDTO(attendeeEntity);

        LOGGER.log(Level.INFO, "End adding an attendee to the event: "
                + "attendeeId {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        return attendeeDTO;
    }

    /**
     * Returns all the attendees associated to an event.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     *
     * @return JSON array {@link ResidentProfileDetailDTO} of attendees at an event. An empty list
     * if none is found.
     */
    @GET
    public List<ResidentProfileDetailDTO> getAttendees(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId) {

        LOGGER.log(Level.INFO, "Begin searching for attendees at event {0}", pEventId);

        // obtains the list of attendees
        List<ResidentProfileDetailDTO> attendeeList = attendeeEntityList2DTOList(
                eventAttendeesLogic.getAttendees(pNeighborhoodId, pEventId));

        LOGGER.log(Level.INFO, "End searching for attendees at event {0}", pEventId);

        return attendeeList;
    }

    /**
     * Returns an attendee with the associated id given by the URI.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAttendeeId the attendee's id
     *
     * @return JSON {@link ResidentProfileDetailDTO} with the information from the attendee
     * @throws BusinessLogicException {@link BusinessLogicExcetionMapper} if there is a logic error
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found.
     */
    @GET
    @Path("{attendeeId: \\d+}")
    public ResidentProfileDetailDTO getAttendee(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException,
            BusinessLogicException {

        LOGGER.log(Level.INFO, "Begin searching for attendee at event: attendee {0} , eventId {1}",
                new Object[]{pAttendeeId, pEventId});

        // if the resident doesn't exist, throw an exception
        if (residentProfileLogic.getResident(pAttendeeId, pNeighborhoodId) == null) {
            throw new WebApplicationException("Resource /attendees/" + pAttendeeId
                    + " does not exist.", 404);
        }

        // creates a residentProfileDTO from found attendee
        ResidentProfileDetailDTO attendeeDTO = new ResidentProfileDetailDTO(
                eventAttendeesLogic.getAttendee(pNeighborhoodId, pEventId, pAttendeeId));

        LOGGER.log(Level.INFO, "End searching for attendee at event: attendee {0} , eventId {1}",
                new Object[]{pAttendeeId, pEventId});

        return attendeeDTO;
    }

    /**
     * Replaces an event's attendees by a list of residents from the request body.
     *
     * @param pNeighborhoodId the neighborhood's id
     * @param pEventId the event's id
     * @param pAtendeeList the new list of attendees
     *
     * @return JSON array {@link ResidentProfileDetailDTO} containing the updated list of attendees
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if a resource is not
     * found
     */
    @PUT
    public List<ResidentProfileDetailDTO> replaceAttendees(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            List<ResidentProfileDetailDTO> pAtendeeList) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Begin replacing attendees from event: attendeesId {0} , event {1}",
                new Object[]{pAtendeeList, pEventId});

        for (ResidentProfileDetailDTO attendee : pAtendeeList) {
            if (residentProfileLogic.getResident(attendee.getId(), pNeighborhoodId) == null) {
                throw new WebApplicationException("Resource /attendees/" + attendee.getId()
                        + " does not exist.", 404);
            }
        }

        // convert DTO list to entity list
        List<ResidentProfileEntity> entityList = attendeeDTOList2EntityList(pAtendeeList);

        // replaces event's attendees
        List<ResidentProfileDetailDTO> newAttendeesList = attendeeEntityList2DTOList(
                eventAttendeesLogic.replaceAttendees(pNeighborhoodId, pEventId, entityList));

        LOGGER.log(Level.INFO, "End replacing attendees frosm event: attendeesId {0} , event {1}",
                new Object[]{pAtendeeList, pEventId});

        return newAttendeesList;

    }

    /**
     * Removes an attendee with the associated id given by the URI.
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
    @Path("{attendeeId: \\d+}")
    public void removeAttendee(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("eventId") Long pEventId,
            @PathParam("attendeeId") Long pAttendeeId) throws WebApplicationException,
            BusinessLogicException {

        LOGGER.log(Level.INFO, "Begin removing attendee from event:"
                + "attendeeID {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});

        if (residentProfileLogic.getResident(pAttendeeId, pNeighborhoodId) == null) {
            throw new WebApplicationException("Resource /attendees/" + pAttendeeId
                    + " does not exist.", 404);
        }
        eventAttendeesLogic.removeAttendee(pNeighborhoodId, pEventId, pAttendeeId);

        LOGGER.log(Level.INFO, "Begin removing attendee from event:"
                + "attendeeID {0} , eventId {1}", new Object[]{pAttendeeId, pEventId});
    }

//===================================================
// Converters
//=================================================== 
    /**
     * Converts a ResidentProfileEntity list to to a ResidentProfileDetailDTO list.
     *
     * @param pResidentList the resident list
     *
     * @return the ResidentProfileDetailDTO list
     */
    private List<ResidentProfileDetailDTO> attendeeEntityList2DTOList(
            List<ResidentProfileEntity> pResidentList) {

        List<ResidentProfileDetailDTO> entityList = new ArrayList<>();

        for (ResidentProfileEntity entity : pResidentList) {
            entityList.add(new ResidentProfileDetailDTO(entity));
        }
        return entityList;
    }

    /**
     * Converts a ResidentProfileEntityDTO list to to a ResidentProfileEntity list.
     *
     * @param pResidentDTOList the resident list
     *
     * @return the ResidentProfileEntity list.
     */
    private List<ResidentProfileEntity> attendeeDTOList2EntityList(List<ResidentProfileDetailDTO> pResidentDTOList) {

        List<ResidentProfileEntity> dtoList = new ArrayList<>();
        for (ResidentProfileDetailDTO dto : pResidentDTOList) {
            dtoList.add(dto.toEntity());
        }
        return dtoList;
    }
}
