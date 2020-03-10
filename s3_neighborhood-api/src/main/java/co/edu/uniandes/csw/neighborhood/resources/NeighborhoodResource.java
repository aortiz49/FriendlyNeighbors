/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.NeighborhoodDTO;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;

@Path("neighborhoods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class NeighborhoodResource {

    private static final Logger LOGGER = Logger.getLogger(NeighborhoodResource.class.getName());

    @Inject
    private NeighborhoodPersistence logic;

    @POST
    public NeighborhoodDTO createNeighborhood(NeighborhoodDTO n) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating neighborhood from resource: input: {0}", n);

        NeighborhoodDTO nd = new NeighborhoodDTO(logic.create(n.toEntity()));
        
        

        LOGGER.log(Level.INFO, "Created neighborhood from resource: output: {0}", nd);
        return nd;
    }

   

}
