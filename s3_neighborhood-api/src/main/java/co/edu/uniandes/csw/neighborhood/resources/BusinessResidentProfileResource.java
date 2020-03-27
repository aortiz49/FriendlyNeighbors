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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

        BusinessDTO newBusinessDTO = new BusinessDTO(businessLogic.getBusiness(pNeighborhoodId,businessEntity.getId()));

        LOGGER.log(Level.INFO, "BusinessResource createBusiness: output: {0}", newBusinessDTO);
        return newBusinessDTO;
    }
    
     /**
     * Returns the business specified by the ID in the URI. 
     *
     * @param postsId postId from wanted post
     * @param residentsId postId from resident whose post is wanted
     * @param neighId parent neighborhood
     * @return {@link PostDetailDTO} - post found inside resident
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if post not found
     */
    @GET
    @Path("{businessesId: \\d+}")
    public BusinessDTO getPost(@PathParam("residentsId") Long residentsId, 
            @PathParam("businessesId") Long businessId, 
            @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        
        LOGGER.log(Level.INFO, "Looking for business: input: residentsId {0} , postsId {1}", new Object[]{residentsId, businessId});
        if (businessLogic.getBusiness(neighId,businessId) == null) {
            throw new WebApplicationException("Resource /posts/" + businessId + " does not exist.", 404);
        }
        
        BusinessDTO detailDTO = new BusinessDTO(businessResidentLogic.getBusiness(neighId,residentsId, businessId));
        LOGGER.log(Level.INFO, "Ended looking for post: output: {0}", detailDTO);
        return detailDTO;
    }



}
