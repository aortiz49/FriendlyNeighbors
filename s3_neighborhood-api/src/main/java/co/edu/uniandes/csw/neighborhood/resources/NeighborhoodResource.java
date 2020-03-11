/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.NeighborhoodDTO;
import co.edu.uniandes.csw.neighborhood.dtos.NeighborhoodDetailDTO;
import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.NeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
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

@Path("neighborhoods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class NeighborhoodResource {

    private static final Logger LOGGER = Logger.getLogger(NeighborhoodResource.class.getName());

    @Inject
    private NeighborhoodLogic logic;

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
    @Path("{neighborhoodsId: \\d+}")
    public NeighborhoodDetailDTO getNeighborhood(@PathParam("neighborhoodsId") Long neighborhoodsId) {
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
    @Path("{neighborhoodsId: \\d+}")
    public NeighborhoodDetailDTO updateNeighborhood(@PathParam("neighborhoodsId") Long neighborhoodsId,
            NeighborhoodDetailDTO neighborhood) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating neighborhood from resource: input: neighborhoodsId: {0} , neighborhood: {1}", new Object[]{neighborhoodsId, neighborhood});
        neighborhood.setId(neighborhoodsId);
        if (logic.getNeighborhood(neighborhoodsId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + neighborhoodsId + " does not exist.", 404);
        }
        NeighborhoodDetailDTO detailDTO = new NeighborhoodDetailDTO(logic.updateNeighborhood(neighborhoodsId, neighborhood.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating resident from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    @DELETE
    @Path("{neighborhoodsId: \\d+}")
    public void deleteNeighborhood(@PathParam("neighborhoodsId") Long neighborhoodsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "neighborhoodResource deleteNeighborhood: input: {0}", neighborhoodsId);
        if (logic.getNeighborhood(neighborhoodsId) == null) {
            throw new WebApplicationException("The resource /neighbors/" + neighborhoodsId + " does not exist.", 404);
        }
        logic.deleteNeighborhood(neighborhoodsId);
        LOGGER.info("neighborhoodResource deleteNeighborhood: output: void");
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
