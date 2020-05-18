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

import co.edu.uniandes.csw.neighborhood.dtos.EventDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.EventGroupLogic;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.ejb.EventLogic;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "groups/{id}/events".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventGroupResource {

    private static final Logger LOGGER = Logger.getLogger(EventGroupResource.class.getName());

    @Inject
    private EventGroupLogic groupEventLogic;

    @Inject
    private EventLogic eventLogic;

    /**
     * Associates a event with existing group
     *
     * @param eventsId id from event to be associated
     * @param groupsId id from group
     * @param neighId parent neighborhood
     * @return JSON {@link EventDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{eventsId: \\d+}")
    public EventDetailDTO associateEventToGroup(@PathParam("groupsId") Long groupsId, @PathParam("eventsId") Long eventsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating event to group from resource: input: groupsId {0} , eventsId {1}", new Object[]{groupsId, eventsId});
        if (eventLogic.getEvent(eventsId, neighId) == null) {
            throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        EventEntity e = groupEventLogic.associateEventToGroup(groupsId, eventsId, neighId);
        
        EventDetailDTO detailDTO = new EventDetailDTO(e);

        LOGGER.log(Level.INFO, "Ended associating event to group from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the events associated to a group and returns it
     *
     * @param groupsId id from group whose events are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link EventDetailDTO} - events found in group. An
     * empty list if none is found
     */
    @GET
    public List<EventDetailDTO> getEvents(@PathParam("groupsId") Long groupsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for events from resources: input: {0}", groupsId);
        List<EventDetailDTO> list = eventsListEntity2DTO(groupEventLogic.getEvents(groupsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for events from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a event with specified ID by URL which is associated with 
     * group and returns it
     *
     * @param eventsId id from wanted event
     * @param groupsId id from group whose event is wanted
     * @param neighId parent neighborhood
     * @return {@link EventDetailDTO} - event found inside group
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if event not found
     */
    @GET
    @Path("{eventsId: \\d+}")
    public EventDetailDTO getEvent(@PathParam("groupsId") Long groupsId, @PathParam("eventsId") Long eventsId,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for event: input: groupsId {0} , eventsId {1}", new Object[]{groupsId, eventsId});
        if (eventLogic.getEvent(eventsId, neighId) == null) {
            throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        EventDetailDTO detailDTO = new EventDetailDTO(groupEventLogic.getEvent(groupsId, eventsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for event: output: {0}", detailDTO);
        return detailDTO;
    }


    /**
     * Removes a event from group
     *
     * @param groupsId id from group whose event is to be removed
     * @param eventsId id from event to be removed
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{eventsId: \\d+}")
    public void removeEvent(@PathParam("groupsId") Long groupsId, @PathParam("eventsId") Long eventsId,  @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Removing event from group: input: groupsId {0} , eventsId {1}", new Object[]{groupsId, eventsId});
        if (eventLogic.getEvent(eventsId, neighId) == null) {
                 throw new WebApplicationException("Resource /events/" + eventsId + " does not exist.", 404);
        }
        groupEventLogic.removeEvent(groupsId, eventsId, neighId);
        LOGGER.info("Ended removing event from group: output: void");
    }

    /**
     * Converts an entity list with events to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<EventDetailDTO> eventsListEntity2DTO(List<EventEntity> entityList) {
        List<EventDetailDTO> list = new ArrayList<>();
        for (EventEntity entity : entityList) {
            list.add(new EventDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with events to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<EventEntity> eventsListDTO2Entity(List<EventDetailDTO> dtos) {
        List<EventEntity> list = new ArrayList<>();
        for (EventDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
