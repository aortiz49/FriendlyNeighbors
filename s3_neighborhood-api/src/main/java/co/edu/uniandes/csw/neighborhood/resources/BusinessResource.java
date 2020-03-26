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

import co.edu.uniandes.csw.neighborhood.dtos.BusinessDTO;
import co.edu.uniandes.csw.neighborhood.ejb.BusinessLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * Class that represents the "businesses" resource.
 *
 * @author aortiz93
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class BusinessResource {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to display messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessResource.class.getName());

    /**
     * Injects BusinessLogic dependencies.
     */
    @Inject
    private BusinessLogic businessLogic;

//===================================================
// REST API
//===================================================
    /**
     *
     * Creates a new business with the information received in the body of the petition and returns
     * a new identical object with auto-generated id by the data base.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the businesses
     *
     * @return JSONArray {@link BusinessDTO} - All the businesses in a neighborhood if found.
     * Otherwise, an empty list.
     */
    @GET
    public List<BusinessDTO> getAllBusinesses(@PathParam("neighborhoodId") Long pNeighborhoodId) {
        LOGGER.info("Looking for all businesses from resources: input: void");
        List<BusinessDTO> businesses = listEntity2DTO(businessLogic.getBusinesses(pNeighborhoodId));
        LOGGER.log(Level.INFO, "Ended looking for all businesses from resources: output: {0}", businesses);
        return businesses;
    }

    /**
     * Looks for the business with id received in the URL and returns it.
     *
     * @param pNeighborhoodId the neighborhood containing the business
     * @param pBusinessId the desired business id
     *
     * @return JSON {@link PostDetailDTO} - Wanted post DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} - Logic error if not
     * found
     */
    @GET
    @Path("{businessesId: \\d+}")
    public BusinessDTO getBusiness(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("businessesId") Long pBusinessId) {

        LOGGER.log(Level.INFO, "Looking for  business from resource: input: {0}", pBusinessId);

        BusinessEntity businessEntity = businessLogic.getBusiness(pNeighborhoodId, pBusinessId);

        if (businessEntity == null) {
            throw new WebApplicationException("The Resource /neighborhoods/"
                    + pBusinessId + " does not exist.", 404);
        }

        BusinessDTO detailDTO = new BusinessDTO(businessEntity);

        LOGGER.log(Level.INFO, "Ended looking for business from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates a business with id with the information contained in the request body.
     *
     * @param pNeighborhoodId the neighborhood containing the business
     * @param pBusinessId the business id of the resource to be modified.
     * @param pBusiness the business to update with
     *
     * @return the updated business
     * @throws BusinessLogicException if the business cannot be updated
     * @throws WebApplicationException if the business cannot be updated
     *
     */
    @PUT
    @Path("{businessesId: \\d+}")
    public BusinessDTO updateBusiness(
            @PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("businessesId") Long pBusinessId,
            BusinessDTO pBusiness) throws BusinessLogicException, WebApplicationException {

        LOGGER.log(Level.INFO, "Updating neighborhood from resource: input: businessesId: {0} , "
                + "neighborhood: {1}", new Object[]{pBusinessId, pBusiness});

        pBusiness.setId(pBusinessId);

        if (businessLogic.getBusiness(pNeighborhoodId, pBusinessId) == null) {
            throw new WebApplicationException("Resource /neighborhoods/" + pBusinessId
                    + " does not exist.", 404);
        }

        BusinessDTO detailDTO = new BusinessDTO(businessLogic.updateBusiness(pNeighborhoodId,
                pBusiness.toEntity()));

        LOGGER.log(Level.INFO, "Ended updating businessesId from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the business with the associated id received by the URL.
     *
     * @param pNeighborhoodId the neighborhood containing the business
     * @param pBusinessId id of the business to be deleted
     *
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} Logic error if not
     * found
     */
    @DELETE
    @Path("{businessesId: \\d+}")
    public void deleteBusiness(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("businessesId") Long pBusinessId) throws WebApplicationException {

        LOGGER.log(Level.INFO, "businessResource deleteBusiness: input: {0}", pBusinessId);

        if (businessLogic.getBusiness(pNeighborhoodId, pBusinessId) == null) {
            throw new WebApplicationException("The resource /neighbors/" + pBusinessId + " does not exist.", 404);
        }

        businessLogic.deleteBusiness(pNeighborhoodId, pBusinessId);

        LOGGER.info("businessResource deleteBusiness: output: void");
    }

    /**
     * Converts an entity list to a DTO list for businesses.
     *
     * @param pEntityList business entity list to be converted.
     * @return business DTO list
     */
    private List<BusinessDTO> listEntity2DTO(List<BusinessEntity> pEntityList) {
        List<BusinessDTO> list = new ArrayList<>();
        for (BusinessEntity entity : pEntityList) {
            list.add(new BusinessDTO(entity));
        }
        return list;
    }

}
