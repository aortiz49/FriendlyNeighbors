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

import co.edu.uniandes.csw.neighborhood.dtos.ServiceDTO;

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.ejb.ServiceLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ServiceResidentProfileLogic;
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
 * Class implementing resource "resident/{serviceId}/services".
 *
 * @author v.cardonac1
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServiceResidentProfileResource {

    private static final Logger LOGGER = Logger.getLogger(ServiceResidentProfileResource.class.getName());

    @Inject
    private ServiceResidentProfileLogic residentServiceLogic;

    @Inject
    private ServiceLogic serviceLogic;

    /**
     * Creates a service with existing resident
     *
     * @param service serviceId from service to be associated
     * @param residentsId serviceId from resident
     * @param neighId parent neighborhood
     * @return JSON {@link ServiceDTO} -
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} 
     * Logic error if not found
     */
    @POST
    public ServiceDTO createService(@PathParam("residentsId") Long residentsId, ServiceDTO service,  @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creating service for resident from resource: input: residentsId {0} , servicesId {1}", new Object[]{residentsId, service.getId()});

        ServiceEntity entity = null;

        entity = serviceLogic.createService(service.toEntity(), residentsId, neighId);
        
        ServiceDTO dto = new ServiceDTO(serviceLogic.getService(entity.getId(), neighId));
        LOGGER.log(Level.INFO, "Ended creating service for resident from resource: output: {0}", dto.getId());
        return dto;
    }

    /**
     * Looks for all the services associated to a resident and returns it
     *
     * @param residentsId serviceId from resident whose services are wanted
     * @param neighId parent neighborhood
     * @return JSONArray {@link ServiceDTO} - services found in resident. An
     * empty list if none is found
     */
    @GET
    public List<ServiceDTO> getServices(@PathParam("residentsId") Long residentsId, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Looking for services from resources: input: {0}", residentsId);
        List<ServiceDTO> list = servicesListEntity2DTO(residentServiceLogic.getServices(residentsId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for services from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a service with specified ID by URL which is associated with 
     * resident and returns it
     *
     * @param servicesId serviceId from wanted service
     * @param residentsId serviceId from resident whose service is wanted
     * @param neighId parent neighborhood
     * @return {@link ServiceDTO} - service found inside resident
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if service not found
     */
    @GET
    @Path("{servicesId: \\d+}")
    public ServiceDTO getService(@PathParam("residentsId") Long residentsId, @PathParam("servicesId") Long servicesId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for service: input: residentsId {0} , servicesId {1}", new Object[]{residentsId, servicesId});
        if (serviceLogic.getService(servicesId, neighId) == null) {
            throw new WebApplicationException("Resource /services/" + servicesId + " does not exist.", 404);
        }
        ServiceDTO detailDTO = new ServiceDTO(residentServiceLogic.getService(residentsId, servicesId, neighId));
        LOGGER.log(Level.INFO, "Ended looking for service: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     *
     * Updates a list from services inside a resident which is received in body
     *
     * @param residentsId serviceId from resident whose list of services is to be updated
     * @param services JSONArray {@link ServiceDTO} - modified services list
     * @param neighId parent neighborhood
     * @return JSONArray {@link ServiceDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<ServiceDTO> replaceServices(@PathParam("residentsId") Long residentsId, List<ServiceDTO> services, @PathParam("neighborhoodId") Long neighId) {
        LOGGER.log(Level.INFO, "Replacing resident services from resource: input: residentsId {0} , services {1}", new Object[]{residentsId, services});
        for (ServiceDTO service : services) {
            if (serviceLogic.getService(service.getId(), neighId) == null) {
                throw new WebApplicationException("Resource /services/" + services + " does not exist.", 404);
            }
        }
        List<ServiceDTO> lista = servicesListEntity2DTO(residentServiceLogic.replaceServices(residentsId, servicesListDTO2Entity(services), neighId));
        LOGGER.log(Level.INFO, "Ended replacing resident services from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a service from resident
     *
     * @param residentsId serviceId from resident whose service is to be removed
     * @param servicesId serviceId from service to be removed
     * @param neighId parent neighborhood
     * @throws co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException if rules are not met
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} 
     * Error if not found
     */
    @DELETE
    @Path("{servicesId: \\d+}")
    public void removeService(@PathParam("residentsId") Long residentsId, @PathParam("servicesId") Long servicesId, @PathParam("neighborhoodId") Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Removing service from resident: input: residentsId {0} , servicesId {1}", new Object[]{residentsId, servicesId});
        if (serviceLogic.getService(servicesId, neighId) == null) {
            throw new WebApplicationException("Resource /services/" + servicesId + " does not exist.", 404);
        }
        residentServiceLogic.removeService(residentsId, servicesId, neighId);
        LOGGER.info("Ended removing service from resident: output: void");
    }

    /**
     * Converts an entity list with services to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<ServiceDTO> servicesListEntity2DTO(List<ServiceEntity> entityList) {
        List<ServiceDTO> list = new ArrayList<>();
        for (ServiceEntity entity : entityList) {
            list.add(new ServiceDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with services to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<ServiceEntity> servicesListDTO2Entity(List<ServiceDTO> dtos) {
        List<ServiceEntity> list = new ArrayList<>();
        for (ServiceDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
