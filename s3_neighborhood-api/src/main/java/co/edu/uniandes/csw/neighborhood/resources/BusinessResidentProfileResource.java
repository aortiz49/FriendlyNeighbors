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
import co.edu.uniandes.csw.neighborhood.ejb.BusinessResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;

import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Class that represents the "residents/{residentId}/businesses" resource .
 *
 * @author aortiz93
 */
public class BusinessResidentProfileResource {

//===================================================
// Attributes
//===================================================
    /**
     * Logger to display messages to the console.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessResidentProfileResource.class.getName());

    /**
     * Injects logic that associates a business to a resident.
     */
    @Inject
    private BusinessResidentProfileLogic businessResidentLogic;

    @Inject
    private BusinessLogic businessLogic;

    /**
     *
     * Creates a new business with the information received in the body of the petition and returns
     * a new identical object with and auto-generated id by the database.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the businesses
     * @param pOwnerId the id of the business owner
     * @param pBusiness {@link BusinessDTO} the business to be saved
     *
     * @return JSON {@link BusinessDTO} the saved business with auto-generated id
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is an error when
     * creating the business
     */
    @POST
    public BusinessDTO createBusiness(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pOwnerId,
            BusinessDTO pBusiness) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "BusinessResource createBusiness: input: {0}", pBusiness);

        BusinessEntity businessEntity = businessLogic.createBusiness(pNeighborhoodId, pOwnerId, pBusiness.toEntity());

        BusinessDTO newBusinessDTO = new BusinessDTO(businessLogic.getBusiness(pNeighborhoodId, businessEntity.getId()));

        LOGGER.log(Level.INFO, "BusinessResource createBusiness: output: {0}", newBusinessDTO);
        return newBusinessDTO;
    }

    /**
     * Returns all the businesses owned by a resident.
     *
     * @param pNeighborhoodId the id of the neighborhood containing the businesses
     * @param pOwnerId the id of the business owner
     *
     * @return JSONArray {@link BusinessDTO} - businesses owned by the resident. An empty list if
     * none are found.
     */
    @GET
    public List<BusinessDTO> getBusinesses(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pOwnerId) {

        LOGGER.log(Level.INFO, "Looking for businesses from resources: input: {0}", pOwnerId);

        List<BusinessDTO> list = businessEntity2DTO(businessResidentLogic.getBusinesses(
                pNeighborhoodId, pOwnerId));

        LOGGER.log(Level.INFO, "Ended looking for businesses from resources: output: {0}", list);
        return list;
    }

    /**
     * Returns the business specified by the ID in the URI.
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessId the id of associated business
     *
     * @return {@link PostDetailDTO} - post found inside resident
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not
     * met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} Logic error if post not
     * found
     */
    @GET
    @Path("{businessesId: \\d+}")
    public BusinessDTO getbusiness(@PathParam("neighborhoodId") Long neighId,
            @PathParam("residentsId") Long residentsId,
            @PathParam("businessesId") Long businessId) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Looking for business: input: residentsId {0} , postsId {1}", new Object[]{residentsId, businessId});

        if (businessLogic.getBusiness(neighId, businessId) == null) {
            throw new WebApplicationException("Resource /businessess/" + businessId + " does not exist.", 404);
        }

        BusinessDTO detailDTO = new BusinessDTO(businessResidentLogic.getBusiness(neighId, residentsId, businessId));

        LOGGER.log(Level.INFO, "Ended looking for business: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     *
     * Updates a resident's owned business with business from a list given by the path.
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param businessDTOList the list of business DTOs
     *
     * @return JSONArray {@link PostDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} Error if not found
     */
    @PUT
    public List<BusinessDTO> replaceBusinesses(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pOwnerId,
            List<BusinessDTO> businessDTOList) throws WebApplicationException {

        LOGGER.log(Level.INFO, "Replacing resident businesses from resource: input: residentsId {0} , posts {1}", new Object[]{pOwnerId, businessDTOList});

        for (BusinessDTO business : businessDTOList) {
            if (businessLogic.getBusiness(pNeighborhoodId, business.getId()) == null) {
                throw new WebApplicationException("Resource /businesses/" + businessDTOList + " does not exist.", 404);
            }
        }

        List<BusinessDTO> lista = businessEntity2DTO(businessResidentLogic.replaceBusinesses(
                pNeighborhoodId, pOwnerId, businessDTO2Entity(businessDTOList)));

        LOGGER.log(Level.INFO, "Ended replacing resident posts from resource: output:{0}", lista);
        return lista;
    }

    ////
   
    // TODO: update
    ///
    /**
     * Removes a business from an owner.
     *
     * @param pNeighborhoodId the id of neighborhood the business is in
     * @param pOwnerId the id of the business owner
     * @param pBusinessId the id of associated business
     *
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} if if not found
     */
    @DELETE
    @Path("{businessesId: \\d+}")
    public void removebusiness(@PathParam("neighborhoodId") Long pNeighborhoodId,
            @PathParam("residentsId") Long pOwnerId,
            @PathParam("businessesId") Long pBusinessId) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "Removing business from owner: input: residentsId {0} , "
                + "postsId {1}", new Object[]{pOwnerId, pBusinessId});

        if (businessLogic.getBusiness(pNeighborhoodId, pBusinessId) == null) {
            throw new WebApplicationException("Resource /businesses/" + pBusinessId + " does not exist.", 404);
        }

        businessResidentLogic.removeBusiness(pNeighborhoodId, pOwnerId, pBusinessId);
        LOGGER.info("Ended removing post from resident: output: void");
    }

    /**
     * Converts a list of business entities to a list of business DTOs.
     *
     * @param pBusinessEntityList list of business entities
     *
     * @return list of business DTOs
     */
    private List<BusinessDTO> businessEntity2DTO(List<BusinessEntity> pBusinessEntityList) {

        List<BusinessDTO> list = new ArrayList<>();

        for (BusinessEntity business : pBusinessEntityList) {
            list.add(new BusinessDTO(business));
        }
        return list;
    }

    /**
     * Converts a list of business DTOs to a list of business entities.
     *
     * @param businessDTOList the list of business DTOs
     *
     * @return the list of business entities
     */
    private List<BusinessEntity> businessDTO2Entity(List<BusinessDTO> businessDTOList) {

        List<BusinessEntity> list = new ArrayList<>();

        for (BusinessDTO business : businessDTOList) {
            list.add(business.toEntity());
        }
        return list;
    }

}
