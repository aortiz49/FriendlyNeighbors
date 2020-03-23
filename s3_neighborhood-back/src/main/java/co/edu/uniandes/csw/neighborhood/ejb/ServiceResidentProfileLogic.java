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
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ServicePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author albayona
 */
@Stateless
public class ServiceResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(ServiceResidentProfileLogic.class.getName());

    @Inject
    private ServicePersistence servicePersistence;

    @Inject
    private ResidentProfilePersistence residentPersistence;


    /**
     * /**
     * Gets a collection of services entities associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId ID from resident entity
     * @return collection of service entities associated with  resident
     */
    public List<ServiceEntity> getServices(Long residentId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all services belonging to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return residentPersistence.find(residentId, neighId).getServices();
    }

    /**
     * Gets a service entity associated with  resident
     *
     * @param residentId Id from resident
     * @param neighId ID from parent neighborhood
     * @param serviceId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If service is not associated
     */
    public ServiceEntity getService(Long residentId, Long serviceId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding service with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{serviceId, residentId, neighId});
        List<ServiceEntity> services = residentPersistence.find(residentId, neighId).getServices();
        ServiceEntity ServiceEntity = servicePersistence.find(serviceId, neighId);
        int index = services.indexOf(ServiceEntity);
        LOGGER.log(Level.INFO, "Found service with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{serviceId, residentId, neighId});
        if (index >= 0) {
            return services.get(index);
        }
        throw new BusinessLogicException("Service is not associated with resident");
    }

    /**
     * Replaces services associated with  resident
     *
     * @param neighId ID from parent neighborhood
     * @param residentId Id from resident
     * @param services Collection of service to associate with resident
     * @return A new collection associated to resident
     */
    public List<ServiceEntity> replaceServices(Long residentId, List<ServiceEntity> services, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace services related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        ResidentProfileEntity resident = residentPersistence.find(residentId, neighId);
        List<ServiceEntity> serviceList = servicePersistence.findAll(neighId);
        for (ServiceEntity service : serviceList) {
            if (services.contains(service)) {
                service.setAuthor(resident);
            } else if (service.getAuthor() != null && service.getAuthor().equals(resident)) {
                service.setAuthor(null);
            }
        }
        LOGGER.log(Level.INFO, "Replaced services related to resident with id {0} from neighborhood {1}", new Object[]{residentId, neighId});
        return services;
    }

    /**
     * Removes a service from resident. Service is no longer in DB
     *
     * @param residentID Id from resident
     * @param neighId ID from parent neighborhood
     * @param serviceId Id from service
     */
    public void removeService(Long residentID, Long serviceId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Deleting service with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{serviceId, residentID, neighId});

        servicePersistence.delete(getService(residentID, serviceId, neighId).getId(), neighId);

        LOGGER.log(Level.INFO, "Deleted service with id {0} associated to resident with id {1}, from neighbothood {2}", new Object[]{serviceId, residentID, neighId});
    }
}
