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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Class that represents the "businesses" resource.
 *
 * @author aortiz93
 */
@Path("businesses")
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
     * a new identical object with an auto-generated id by the data base.
     *
     * @param pBusiness {@link BusinessDTO} the business to be saved
     *
     * @return JSON {@link EditorialDTO} the saved business with an auto-generated id
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} if there is an error when
     * creating the business
     */
    @POST
    public BusinessDTO createBusiness(BusinessDTO pBusiness) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "BusinessResource createBusiness: input: {0}", pBusiness);

        // Converts the BusinessDTO (JSON) to a Business Entuty object to be managed by the logic.
        BusinessEntity businessEntity = pBusiness.toEntity();

        // Invokes the logic to create a new business. 
        BusinessEntity newBusinessEntity = businessLogic.createBusiness(businessEntity);

        // Invokes the BusinessDTO constructor to create a new BusinessDTO object. 
        BusinessDTO nuevoEditorialDTO = new BusinessDTO(newBusinessEntity);

        LOGGER.log(Level.INFO, "EditorialResource createEditorial: output: {0}", nuevoEditorialDTO);
        return nuevoEditorialDTO;
    }

}
