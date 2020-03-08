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

import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.FavorHelperLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
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
 * Class implementing resource "favors/{id}/helpers".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FavorHelperResource {

    private static final Logger LOGGER = Logger.getLogger(FavorHelperResource.class.getName());

    @Inject
    private FavorHelperLogic favorHelperLogic;

    @Inject
    private ResidentProfileLogic helperLogic;

    /**
     * Associates a helper with an existing favor
     *
     * @param helpersId id from helper to be associated
     * @param favorsId id from favor
     * @return JSON {@link ResidentProfileDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{helpersId: \\d+}")
    public ResidentProfileDetailDTO associateHelperToFavor(@PathParam("favorsId") Long favorsId, @PathParam("helpersId") Long helpersId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Associating helper to favor from resource: input: favorsId {0} , helpersId {1}", new Object[]{favorsId, helpersId});
        if (helperLogic.getResident(helpersId) == null) {
            throw new WebApplicationException("Resource /helpers/" + helpersId + " does not exist.", 404);
        }
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(favorHelperLogic.associateResidentProfileToFavor(favorsId, helpersId));

        LOGGER.log(Level.INFO, "Ended associating helper to favor from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the helpers associated to a favor and returns it
     *
     * @param favorsId id from favor whose helpers are wanted
     * @return JSONArray {@link ResidentProfileDetailDTO} - helpers found in favor. An
     * empty list if none is found
     */
    @GET
    public List<ResidentProfileDetailDTO> getHelpers(@PathParam("favorsId") Long favorsId) {
        LOGGER.log(Level.INFO, "Looking for helpers from resources: input: {0}", favorsId);
        List<ResidentProfileDetailDTO> list = helpersListEntity2DTO(favorHelperLogic.getResidentProfiles(favorsId));
        LOGGER.log(Level.INFO, "Ended looking for helpers from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a helper with specified ID by URL which is associated with a
     * favor and returns it
     *
     * @param helpersId id from wanted helper
     * @param favorsId id from favor whose helper is wanted
     * @return {@link ResidentProfileDetailDTO} - helper found inside favor
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if helper not found
     */
    @GET
    @Path("{helpersId: \\d+}")
    public ResidentProfileDetailDTO getHelper(@PathParam("favorsId") Long favorsId, @PathParam("helpersId") Long helpersId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for helper: input: favorsId {0} , helpersId {1}", new Object[]{favorsId, helpersId});
        if (helperLogic.getResident(helpersId) == null) {
            throw new WebApplicationException("Resource /helpers/" + helpersId + " does not exist.", 404);
        }
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(favorHelperLogic.getResidentProfile(favorsId, helpersId));
        LOGGER.log(Level.INFO, "Ended looking for helper: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * 
     * Updates a list from helpers inside a favor which is received in body
     *
     * @param favorsId  id from favor whose list of helpers is to be updated
     * @param helpers JSONArray {@link ResidentProfileDetailDTO} - modified helpers list 
     * @return JSONArray {@link ResidentProfileDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<ResidentProfileDetailDTO> replaceHelpers(@PathParam("favorsId") Long favorsId, List<ResidentProfileDetailDTO> helpers) {
        LOGGER.log(Level.INFO, "Replacing favor helpers from resource: input: favorsId {0} , helpers {1}", new Object[]{favorsId, helpers});
        for (ResidentProfileDetailDTO helper : helpers) {
            if (helperLogic.getResident(helper.getId()) == null) {
                     throw new WebApplicationException("Resource /helpers/" + helpers + " does not exist.", 404);
            }
        }
        List<ResidentProfileDetailDTO> lista = helpersListEntity2DTO(favorHelperLogic.replaceResidentProfiles(favorsId, helpersListDTO2Entity(helpers)));
        LOGGER.log(Level.INFO, "Ended replacing favor helpers from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a helper from a favor
     *
     * @param favorsId id from favor whose helper is to be removed
     * @param helpersId id from helper to be removed
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{helpersId: \\d+}")
    public void removeHelper(@PathParam("favorsId") Long favorsId, @PathParam("helpersId") Long helpersId) {
        LOGGER.log(Level.INFO, "Removing helper from favor: input: favorsId {0} , helpersId {1}", new Object[]{favorsId, helpersId});
        if (helperLogic.getResident(helpersId) == null) {
                 throw new WebApplicationException("Resource /helpers/" + helpersId + " does not exist.", 404);
        }
        favorHelperLogic.removeResidentProfile(favorsId, helpersId);
        LOGGER.info("Ended removing helper from favor: output: void");
    }

    /**
     * Converts an entity list with helpers to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<ResidentProfileDetailDTO> helpersListEntity2DTO(List<ResidentProfileEntity> entityList) {
        List<ResidentProfileDetailDTO> list = new ArrayList<>();
        for (ResidentProfileEntity entity : entityList) {
            list.add(new ResidentProfileDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with helpers to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<ResidentProfileEntity> helpersListDTO2Entity(List<ResidentProfileDetailDTO> dtos) {
        List<ResidentProfileEntity> list = new ArrayList<>();
        for (ResidentProfileDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
