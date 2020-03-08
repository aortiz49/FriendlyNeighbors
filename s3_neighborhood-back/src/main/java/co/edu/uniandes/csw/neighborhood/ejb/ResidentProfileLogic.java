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
package co.edu.uniandes.csw.neighborhood.ejb;

//===================================================
// Imports
//===================================================
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.*;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

/**
 * Class the implements the connection with the businessPersistence for the
 * Business entity.
 *
 * @author albayona
 */
@Stateless
public class ResidentProfileLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfileLogic.class.getName());

    @Inject
    private ResidentProfilePersistence persistence;
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

    /**
     * Creates a resident in persistence
     *
     * @param residentEntity Entity representing resident to create
     * @return Persisted entity representing resident
     * @throws BusinessLogicException If a business rule si not met
     */
    public ResidentProfileEntity createResident(ResidentProfileEntity residentEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for resident has started");

        //1a. E-mail cannot be null 
        if (residentEntity.getEmail() == null) {
            throw new BusinessLogicException("An e-mail has to be specified");
        }
        //1b. E-mail cannot be duplicated 
        if (persistence.findByEmail(residentEntity.getEmail()) != null) {
            throw new BusinessLogicException("E-mail provided already in use: " + residentEntity.getEmail() + "\"");
        }
        verifyBusinessRules(residentEntity);

        ResidentProfileEntity entity = persistence.create(residentEntity);
        LOGGER.log(Level.INFO, "Creation process for resident eneded");

        return persistence.find(entity.getId());
    }

    private void verifyBusinessRules(ResidentProfileEntity residentEntity) throws BusinessLogicException {
        // 2. Proof of residence must not be null.
        if (residentEntity.getProofOfResidence() == null) {
            throw new BusinessLogicException("A proof of residence has to be specified");
        }

        //3. The user must indicate his name.
        if (residentEntity.getName() == null) {
            throw new BusinessLogicException("A name has to be specified");
        }

        //4. The user must provide a mobile number
        if (residentEntity.getPhoneNumber() == null) {
            throw new BusinessLogicException("A phone has to be specified");
        }

        // the neighborhood the potential resident belongs to 
        NeighborhoodEntity neighborhood = residentEntity.getNeighborhood();

        // 5. The business must have a neighborhood
        if (neighborhood == null) {
            throw new BusinessLogicException("The resident must have a neighborhood!");
        }
        // 6. The business must have an existing neighborhood
        if (neighborhoodPersistence.find(neighborhood.getId()) == null) {
            throw new BusinessLogicException("The resident must have an existing  neighborhood!");
        }
    }

    /**
     * Deletes a resident
     *
     * @param id : id from resident to delete
     */
    public void deleteResident(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for resident with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for resident with id = {0}", id);
    }

    /**
     * Gets all residents
     *
     * @return A list with all residents
     */
    public List<ResidentProfileEntity> getResidents() {

        LOGGER.log(Level.INFO, "Starting querying process for all residents");
        List<ResidentProfileEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all residents");
        return residents;
    }

    /**
     * Returns a resident
     *
     * @param id: id from resident to find
     * @return the entity of the wanted resident
     */
    public ResidentProfileEntity getResident(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for resident with id ", id);
        ResidentProfileEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  resident with id", id);
        return resident;
    }

    /**
     * Retornas a resident
     *
     * @param email : email from wanted resident consultar
     * @return the entity of the wanted resident
     */
    public ResidentProfileEntity getResidentByEmail(String email) {
        LOGGER.log(Level.INFO, "Starting querying process for resident with email ", email);
        ResidentProfileEntity resident = persistence.findByEmail(email);
        LOGGER.log(Level.INFO, "Ended querying process for  resident with id ", email);
        return resident;
    }

    /**
     * Updates a resident
     *
     * @param residentEntity to be updated
     * @return the entity with the updated resident
     * @throws BusinessLogicException exception
     */
    public ResidentProfileEntity updateResident(ResidentProfileEntity residentEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for resident with id ", residentEntity.getId());

        ResidentProfileEntity original = persistence.find(residentEntity.getId());

        //1a. E-mail cannot be null 
        if (residentEntity.getEmail() == null) {
            throw new BusinessLogicException("An e-mail has to be specified");
        }

        if (!original.getEmail().equals(residentEntity.getEmail())) {

            //1b. E-mail cannot be duplicated 
            if (persistence.findByEmail(residentEntity.getEmail()) != null) {
                throw new BusinessLogicException("E-mail provided already in use: " + residentEntity.getEmail() + "\"");
            }

        }

        verifyBusinessRules(residentEntity);
        ResidentProfileEntity modified = persistence.update(residentEntity);
        LOGGER.log(Level.INFO, "Ended update process for resident with id ", residentEntity.getId());
        return modified;
    }

}
