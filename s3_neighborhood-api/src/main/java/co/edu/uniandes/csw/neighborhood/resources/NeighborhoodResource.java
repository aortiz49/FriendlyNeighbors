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
import co.edu.uniandes.csw.neighborhood.dtos.NeighborhoodDTO;
import co.edu.uniandes.csw.neighborhood.dtos.NeighborhoodDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * class that represents the "neighborhoods" resource.
 *
 * @author aortiz49
 */
@Path("neighborhoods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class NeighborhoodResource {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to display messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(NeighborhoodResource.class.getName());

    /**
     * Injects neighborhood logic dependencies.
     */
    @Inject
    private NeighborhoodLogic logic;

//===================================================
// REST API
//===================================================
    /**
     * Creates a new neighborhood with the information received in the body of the petition and
     * returns a new identical object with and auto-generated id by the database.
     *
     * @param pNeighborhood {@link NeighborhoodDTO} the business to be saved
     *
     * @return
     * @throws BusinessLogicException
     */
    @POST
    public NeighborhoodDTO createNeighborhood(NeighborhoodDTO pNeighborhood) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating neighborhood from resource: input: {0}", pNeighborhood);
        // Converts the BusinessDTO (JSON) to a Business Entuty object to be managed by the logic.
        NeighborhoodEntity neighborhoodEntity = pNeighborhood.toEntity();

        // Invokes the logic to create a new business. 
        NeighborhoodEntity newBusinessEntity = logic.createNeighborhood(neighborhoodEntity);

        // Invokes the BusinessDTO constructor to create a new BusinessDTO object. 
        NeighborhoodDTO newNeighborhoodDTO = new NeighborhoodDTO(newBusinessEntity);

        LOGGER.log(Level.INFO, "Created neighborhood from resource: output: {0}", pNeighborhood);
        return newNeighborhoodDTO;
    }

    @GET
    public List<NeighborhoodDetailDTO> getNeighborhoods() {
        LOGGER.info("Looking for all neighborhoods from resources: input: void");
        List<NeighborhoodDetailDTO> neighborhoods = listEntity2DTO(logic.getNeighborhoods());
        LOGGER.log(Level.INFO, "Ended looking for all residents from resources: output: {0}", neighborhoods);
        return neighborhoods;
    }

    /**
     * Looks for the neighborhood with id received in the URL y returns it.
     *
     * @param neighborhoodsId Id from wanted neighborhood. Must be a sequence of digits.
     * @return JSON {@link NeighborhoodDetailDTO} - Wanted resident DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} - Logic error if not
     * found
     */
    @GET
    @Path("{neighborhoodId: \\d+}")
    public NeighborhoodDetailDTO getNeighborhood(@PathParam("neighborhoodId") Long neighborhoodsId) {
        LOGGER.log(Level.INFO, "Looking for  resident from resource: input: {0}", neighborhoodsId);
        NeighborhoodEntity neighborhoodEntity = logic.getNeighborhood(neighborhoodsId);
        if (neighborhoodEntity == null) {
            throw new WebApplicationException("The Resource /neighborhoods/" + neighborhoodsId + " does not exist.", 404);
        }
        NeighborhoodDetailDTO detailDTO = new NeighborhoodDetailDTO(neighborhoodEntity);
        LOGGER.log(Level.INFO, "Ended looking for resident from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    @PUT
    @Path("{neighborhoodId: \\d+}")
    public NeighborhoodDetailDTO updateNeighborhood(@PathParam("neighborhoodId") Long neighborhoodId,
            NeighborhoodDetailDTO neighborhood) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating neighborhood from resource: input: neighborhoodId: {0} , neighborhood: {1}", new Object[]{neighborhoodId, neighborhood});
        neighborhood.setId(neighborhoodId);
        if (logic.getNeighborhood(neighborhoodId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighborhoodId + " does not exist.", 404);
        }
        NeighborhoodDetailDTO detailDTO = new NeighborhoodDetailDTO(logic.updateNeighborhood(neighborhoodId, neighborhood.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating resident from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    @DELETE
    @Path("{neighborhoodId: \\d+}")
    public void deleteNeighborhood(@PathParam("neighborhoodId") Long neighborhoodId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "neighborhoodId deleteNeighborhood: input: {0}", neighborhoodId);
        if (logic.getNeighborhood(neighborhoodId) == null) {
            throw new WebApplicationException("The resource /neighborhoods/" + neighborhoodId + " does not exist.", 404);
        }
        logic.deleteNeighborhood(neighborhoodId);
        LOGGER.info("neighborhoodResource deleteNeighborhood: output: void");
    }

    @Path("{neighborhoodId: \\d+}/businesses")
    public Class<BusinessResource> getBusinessResource(@PathParam("neighborhoodId") Long neighborhoodId) {
        if (logic.getNeighborhood(neighborhoodId) == null) {
            throw new WebApplicationException("The resource /neighborhoods/" + neighborhoodId + " does not exist.", 404);
        }
        return BusinessResource.class;
    }

    @Path("{neighborhoodId: \\d+}/locations")
    public Class<LocationResource> getLocationResource(@PathParam("neighborhoodId") Long neighborhoodsId) {
        if (logic.getNeighborhood(neighborhoodsId) == null) {
            throw new WebApplicationException("The resource /neighborhoods/" + neighborhoodsId + " does not exist.", 404);
        }
        return LocationResource.class;
    }

    @Path("{neighborhoodId: \\d+}/residents")
    public Class<ResidentProfileResource> getResidentResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return ResidentProfileResource.class;
    }

    @Path("{neighborhoodId: \\d+}/groups")
    public Class<GroupResource> getGroupResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return GroupResource.class;
    }

    @Path("{neighborhoodId: \\d+}/events")
    public Class<EventResource> getEventResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return EventResource.class;
    }

    @Path("{neighborhoodId: \\d+}/services")
    public Class<ServiceResource> getServiceResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return ServiceResource.class;
    }

    @Path("{neighborhoodId: \\d+}/favors")
    public Class<FavorResource> getFavorResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return FavorResource.class;
    }

    @Path("{neighborhoodId: \\d+}/posts")
    public Class<PostResource> getPostResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return PostResource.class;
    }

    @Path("{neighborhoodId: \\d+}/comments")
    public Class<CommentResource> getCommentResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return CommentResource.class;
    }

    @Path("{neighborhoodId: \\d+}/notifications")
    public Class<NotificationResource> getNotificationResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return NotificationResource.class;
    }

    @Path("{neighborhoodId: \\d+}/logins")
    public Class<ResidentLoginResource> getResidentLoginResource(@PathParam("neighborhoodId") Long neighId) {
        if (logic.getNeighborhood(neighId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighId + " does not exist.", 404);
        }
        return ResidentLoginResource.class;
    }

    /**
     * Converts an entity list to a DTO list for residents.
     *
     * @param entityList Resident entity list to be converted.
     * @return Resident DTO list
     */
    private List<NeighborhoodDetailDTO> listEntity2DTO(List<NeighborhoodEntity> entityList) {
        List<NeighborhoodDetailDTO> list = new ArrayList<>();
        for (NeighborhoodEntity entity : entityList) {
            list.add(new NeighborhoodDetailDTO(entity));
        }
        return list;
    }

}
