/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.ServiceDTO;
import co.edu.uniandes.csw.neighborhood.ejb.ServiceLogic;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
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
 * Class implementing resource "services".
 *
 * @author k.romero
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ServiceResource {

    private static final Logger LOGGER = Logger.getLogger(ServiceResource.class.getName());

    @Inject
    private ServiceLogic serviceLogic;

    /**
     * Looks for all services on application and returns them.
     *
     * @param neighId parent neighborhood
     * @return JSONArray {@link ServiceDTO} - All the services on
     * application if found. Otherwise, an empty list.
     */
    @GET
    public List<ServiceDTO> getServices(@PathParam("neighborhoodId") Long neighId) {
        LOGGER.info("Looking for all services from resources: input: void");
        List<ServiceDTO> services = listEntity2DTO(serviceLogic.getServices(neighId));
        LOGGER.log(Level.INFO, "Ended looking for all services from resources: output: {0}", services);
        return services;
    }

    /**
     * Looks for the service with id received in the URL y returns it.
     *
     * @param servicesId Id from wanted service. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @return JSON {@link ServiceDTO} - Wanted service DTO
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @GET
    @Path("{servicesId: \\d+}")
    public ServiceDTO getService(@PathParam("servicesId") Long servicesId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for  service from resource: input: {0}", servicesId);
        ServiceEntity serviceEntity = serviceLogic.getService(servicesId, neighId);
        if (serviceEntity == null) {
            throw new WebApplicationException("Resource /services/" + servicesId + " does not exist.", 404);
        }
        ServiceDTO detailDTO = new ServiceDTO(serviceEntity);
        LOGGER.log(Level.INFO, "Ended looking for service from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Updates service with id from URL with the information contained in
     * request body.
     *
     * @param servicesId ID from service to be updated. Must be a sequence of digits.
     * @param neighId parent neighborhood
     * @param service {@link ServiceDTO} Service to be updated.
     * @return JSON {@link ServiceDTO} - Updated service
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @PUT
    @Path("{servicesId: \\d+}")
    public ServiceDTO updateAuthor(@PathParam("servicesId") Long servicesId, @PathParam("neighborhoodId") Long neighId, ServiceDTO service) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Updating service from resource: input: authorsId: {0} , author: {1}", new Object[]{servicesId, service});
        service.setId(servicesId);
        if (serviceLogic.getService(servicesId, neighId) == null) {
            throw new WebApplicationException("Resource /services/" + servicesId + " does not exist.", 404);
        }
        ServiceDTO detailDTO = new ServiceDTO(serviceLogic.updateService(service.toEntity(), neighId ));
        LOGGER.log(Level.INFO, "Ended updating service from resource: output: {0}", detailDTO);

        return detailDTO;
    }

    /**
     * Deletes the service with the associated id received in URL
     *
     * @param servicesId id from service to be deleted
     * @param neighId parent neighborhood
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if not found
     */
    @DELETE
    @Path("{servicesId: \\d+}")
    public void deleteService(@PathParam("servicesId") Long servicesId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting service from resource: input: {0}", servicesId);
        if (serviceLogic.getService(servicesId, neighId) == null) {
            throw new WebApplicationException("Resource /services/" + servicesId + " does not exist.", 404);
        }
        serviceLogic.deleteService(servicesId, neighId);
        LOGGER.info("Service deleted from resource: output: void");
    }

    /**
     * Converts an entity list to a DTO list for services.
     *
     * @param entityList Service entity list to be converted.
     * @return Service DTO list
     */
    private List<ServiceDTO> listEntity2DTO(List<ServiceEntity> entityList) {
        List<ServiceDTO> list = new ArrayList<>();
        for (ServiceEntity entity : entityList) {
            list.add(new ServiceDTO(entity));
        }
        return list;
    }


}
