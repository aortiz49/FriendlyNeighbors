/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.ResidentLoginDTO;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentLoginLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
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
 * Class implementing resource "residentLogins".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ResidentLoginResource {

    private static final Logger LOGGER = Logger.getLogger(ResidentLoginResource.class.getName());

    @Inject
    private ResidentLoginLogic residentLoginLogic;

 
    /**
     * Looks for the residentLogin with id received in the URL y returns it.
     *
     * @param residentLoginsId Id from wanted residentLogin. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link ResidentLoginDTO} - Wanted residentLogin DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{residentLoginsId: \\d+}")
    public ResidentLoginDTO getResidentLogin(@PathParam("residentLoginsId") Long residentLoginsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  residentLogin from resource: input: {0}", residentLoginsId);
        ResidentLoginEntity residentLoginEntity = residentLoginLogic.getResidentLogin(residentLoginsId, neighId);
        if (residentLoginEntity == null) {
            throw new WebApplicationException("Resource /residentLogins/" + residentLoginsId + " does not exist.", 404);
        }
        ResidentLoginDTO detailDTO = new ResidentLoginDTO(residentLoginEntity);
        LOGGER.log(Level.INFO, "Ended looking for residentLogin from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates residentLogin with id from URL with the information contained in
     * request body.
     *
     * @param residentLoginsId ID from residentLogin to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param residentLogin {@link ResidentLoginDTO} ResidentLogin to be updated.
     * @return JSON {@link ResidentLoginDTO} - Updated residentLogin
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{residentLoginsId: \\d+}")
    public ResidentLoginDTO updateResidentLogin(@PathParam("residentLoginsId") Long residentLoginsId, @PathParam("neighborhoodId") Long neighId, ResidentLoginDTO residentLogin) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating residentLogin from resource: input: authorsId: {0} , author: {1}", new Object[]{residentLoginsId, residentLogin});
        residentLogin.setId(residentLoginsId);
        if (residentLoginLogic.getResidentLogin(residentLoginsId, neighId) == null) {
            throw new WebApplicationException("Resource /residentLogins/" + residentLoginsId + " does not exist.", 404);
        }
        ResidentLoginDTO detailDTO = new ResidentLoginDTO(residentLoginLogic.updateResidentLogin(residentLogin.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating residentLogin from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the residentLogin with the associated id received in URL
     *
     * @param residentLoginsId id from residentLogin to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{residentLoginsId: \\d+}")
    public void deleteResidentLogin(@PathParam("residentLoginsId") Long residentLoginsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Deleting residentLogin from resource: input: {0}", residentLoginsId);
        if (residentLoginLogic.getResidentLogin(residentLoginsId, neighId) == null) {
            throw new WebApplicationException("Resource /residentLogins/" + residentLoginsId + " does not exist.", 404);
        }
        residentLoginLogic.deleteResidentLogin(residentLoginsId, neighId);
        LOGGER.info("ResidentLogin deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for residentLogins.
     *
     * @param entityList ResidentLogin entity list to be converted.
     * @return ResidentLogin DTO list
     */
    private List<ResidentLoginDTO> listEntity2DTO(List<ResidentLoginEntity> entityList) {
        List<ResidentLoginDTO> list = new ArrayList<>();
        for (ResidentLoginEntity entity : entityList) {
            list.add(new ResidentLoginDTO(entity));
        }
        return list;
    }
}
