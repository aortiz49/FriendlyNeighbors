/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.FavorDTO;
import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author v.cardonac1
 */
@Path("favors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class FavorResource {
    
    private static final Logger LOGGER = Logger.getLogger(FavorResource.class.getName());
    
    @Inject
    private FavorLogic favorLogic;
    

    
    @GET
    @Path("{favorsId: \\d+}")
    public FavorDTO getFavor(@PathParam("favorsId") Long favorsId) {
        LOGGER.log(Level.INFO, "Looking for  favor from resource: input: {0}", favorsId);
        FavorEntity entity = favorLogic.getFavor(favorsId);
        if (entity == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorDTO favorDTO = new FavorDTO(entity);
        LOGGER.log(Level.INFO, "Ended looking for favor from resource: output: {0}", favorDTO);
        return favorDTO;
    }
    
    @GET
    public List<FavorDTO> getFavors() {
        LOGGER.info("Looking for all favors from resources: input: void");
        List<FavorDTO> favors = listEntity2DTO(favorLogic.getFavors());
        LOGGER.log(Level.INFO, "Ended looking for all favors from resources: output: {0}", favors);
        return favors;
    }
    
    @DELETE
    @Path("{favorsId: \\d+}")
    public void deleteFavor(@PathParam("favorsId") Long favorsId) {
        LOGGER.log(Level.INFO, "Deleting favor from resource: input: {0}", favorsId);
        if (favorLogic.getFavor(favorsId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        favorLogic.deleteFavor(favorsId);
        LOGGER.info("Resident deleted from resource: output: void");
    }
    
    @PUT
    @Path("{favorsId: \\d+}")
    public FavorDTO updateFavor(@PathParam("favorsId") Long favorsId, FavorDTO favor) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating favor from resource: input: favorsId: {0} , favors: {1}", new Object[]{favorsId, favor});
        favor.setId(favorsId);
        if (favorLogic.getFavor(favorsId) == null) {
            throw new WebApplicationException("El recurso /favors/" + favorsId + " no existe.", 404);
        }
        FavorDTO favorDTO = new FavorDTO(favorLogic.updateFavor(favor.toEntity()));
        LOGGER.log(Level.INFO, "Ended updating favor from resource: output: {0}", favorDTO);

        return favorDTO;
    }

    private List<FavorDTO> listEntity2DTO(List<FavorEntity> entityList) {
        List<FavorDTO> list = new ArrayList<>();
        for (FavorEntity entity : entityList) {
            list.add(new FavorDTO(entity));
        }
        return list;    
    }
    
}
