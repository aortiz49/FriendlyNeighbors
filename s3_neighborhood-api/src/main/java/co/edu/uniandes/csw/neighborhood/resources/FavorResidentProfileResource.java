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

import co.edu.uniandes.csw.neighborhood.dtos.FavorDTO;
import co.edu.uniandes.csw.neighborhood.dtos.FavorDetailDTO;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.ejb.FavorLogic;
import co.edu.uniandes.csw.neighborhood.ejb.FavorResidentProfileLogic;
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
 * Class implementing resource "resident/{favorId}/favors".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FavorResidentProfileResource {

    private static final Logger LOGGER = Logger.getLogger(FavorResidentProfileResource.class.getName());

    @Inject
    private FavorResidentProfileLogic residentFavorLogic;

    @Inject
    private FavorLogic favorLogic;

    /**
     * Creates a favor with an existing resident
     *
     * @param favorsId favorId from favor to be associated
     * @param residentsId favorId from resident
     * @return JSON {@link FavorDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    public FavorDetailDTO createFavorForResidentProfile(@PathParam("residentsId") Long residentsId, FavorDTO favor) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating favor for resident from resource: input: residentsId {0} , favorsId {1}", new Object[]{residentsId, favor.getId()});

        FavorEntity entity = null;

        entity = favorLogic.createFavor(favor.toEntity());
        
        Long favorId = entity.getId();
        
        residentFavorLogic.associateFavorToResident(favorId, residentsId);

        FavorDetailDTO dto = new FavorDetailDTO(favorLogic.getFavor(favorId));
        LOGGER.log(Level.INFO, "Ended creating favor for resident from resource: output: {0}", dto.getId());
        return dto;
    }

    /**
     * Looks for all the favors associated to a resident and returns it
     *
     * @param residentsId favorId from resident whose favors are wanted
     * @return JSONArray {@link FavorDetailDTO} - favors found in resident. An
     * empty list if none is found
     */
    @GET
    public List<FavorDetailDTO> getFavors(@PathParam("residentsId") Long residentsId) {
        LOGGER.log(Level.INFO, "Looking for favors from resources: input: {0}", residentsId);
        List<FavorDetailDTO> list = favorsListEntity2DTO(residentFavorLogic.getFavors(residentsId));
        LOGGER.log(Level.INFO, "Ended looking for favors from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a favor with specified ID by URL which is associated with a
     * resident and returns it
     *
     * @param favorsId favorId from wanted favor
     * @param residentsId favorId from resident whose favor is wanted
     * @return {@link FavorDetailDTO} - favor found inside resident
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if favor not found
     */
    @GET
    @Path("{favorsId: \\d+}")
    public FavorDetailDTO getFavor(@PathParam("residentsId") Long residentsId, @PathParam("favorsId") Long favorsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for favor: input: residentsId {0} , favorsId {1}", new Object[]{residentsId, favorsId});
        if (favorLogic.getFavor(favorsId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        FavorDetailDTO detailDTO = new FavorDetailDTO(residentFavorLogic.getFavor(residentsId, favorsId));
        LOGGER.log(Level.INFO, "Ended looking for favor: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     *
     * Updates a list from favors inside a resident which is received in body
     *
     * @param residentsId favorId from resident whose list of favors is to be updated
     * @param favors JSONArray {@link FavorDetailDTO} - modified favors list
     * @return JSONArray {@link FavorDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<FavorDetailDTO> replaceFavors(@PathParam("residentsId") Long residentsId, List<FavorDetailDTO> favors) {
        LOGGER.log(Level.INFO, "Replacing resident favors from resource: input: residentsId {0} , favors {1}", new Object[]{residentsId, favors});
        for (FavorDetailDTO favor : favors) {
            if (favorLogic.getFavor(favor.getId()) == null) {
                throw new WebApplicationException("Resource /favors/" + favors + " does not exist.", 404);
            }
        }
        List<FavorDetailDTO> lista = favorsListEntity2DTO(residentFavorLogic.replaceFavors(residentsId, favorsListDTO2Entity(favors)));
        LOGGER.log(Level.INFO, "Ended replacing resident favors from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a favor from a resident
     *
     * @param residentsId favorId from resident whose favor is to be removed
     * @param favorsId favorId from favor to be removed
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{favorsId: \\d+}")
    public void removeFavor(@PathParam("residentsId") Long residentsId, @PathParam("favorsId") Long favorsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Removing favor from resident: input: residentsId {0} , favorsId {1}", new Object[]{residentsId, favorsId});
        if (favorLogic.getFavor(favorsId) == null) {
            throw new WebApplicationException("Resource /favors/" + favorsId + " does not exist.", 404);
        }
        residentFavorLogic.removeFavor(residentsId, favorsId);
        LOGGER.info("Ended removing favor from resident: output: void");
    }

    /**
     * Converts an entity list with favors to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<FavorDetailDTO> favorsListEntity2DTO(List<FavorEntity> entityList) {
        List<FavorDetailDTO> list = new ArrayList<>();
        for (FavorEntity entity : entityList) {
            list.add(new FavorDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with favors to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<FavorEntity> favorsListDTO2Entity(List<FavorDetailDTO> dtos) {
        List<FavorEntity> list = new ArrayList<>();
        for (FavorDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
