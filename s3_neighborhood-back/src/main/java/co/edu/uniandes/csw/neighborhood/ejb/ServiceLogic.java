/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

/**
 *
 * @author * @author  *
 */
@Stateless
public class ServiceLogic {

    private static final Logger LOGGER = Logger.getLogger(ServiceLogic.class.getName());

    @Inject
    private ServicePersistence persistence;

    public ServiceEntity createService(ServiceEntity serviceEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for service has started");

        persistence.create(serviceEntity);
        LOGGER.log(Level.INFO, "Creation process for service eneded");

        return serviceEntity;
    }

    public void deleteService(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for service with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for service with id = {0}", id);
    }

    public List<ServiceEntity> getServices() {

        LOGGER.log(Level.INFO, "Starting querying process for all services");
        List<ServiceEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all services");
        return residents;
    }

    public ServiceEntity getService(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for service with id ", id);
        ServiceEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  service with id", id);
        return resident;
    }

    public ServiceEntity updateService(ServiceEntity serviceEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for service with id ", serviceEntity.getId());

        ServiceEntity modified = persistence.update(serviceEntity);
        LOGGER.log(Level.INFO, "Ended update process for service with id ", serviceEntity.getId());
        return modified;
    }

}
