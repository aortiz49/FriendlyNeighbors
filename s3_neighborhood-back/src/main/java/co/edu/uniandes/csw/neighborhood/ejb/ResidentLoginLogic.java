/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentLoginPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author v.cardonac1
 */
@Stateless
public class ResidentLoginLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentLoginLogic.class.getName());
    @Inject
    private ResidentLoginPersistence persistence;

    @Inject
    private ResidentProfilePersistence residentProfilePersistence;

    public ResidentLoginEntity createResidentLogin(Long pNeighborhoodId, Long pResidentProfileId, ResidentLoginEntity pResidentLoginEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for resident login has started");

        ResidentProfileEntity r = residentProfilePersistence.find(pResidentProfileId, pNeighborhoodId);

        // 1. The service must have a resident
        if (r == null) {
            throw new BusinessLogicException("The login must have a resident!");
        }

        //must have a username
        if (pResidentLoginEntity.getUserName() == null) {
            throw new BusinessLogicException("A username has to be specified");
        }

        for (ResidentLoginEntity resident : persistence.findAll(pNeighborhoodId)) {
            // No two residents with the same userame
            if (resident.getUserName().equals(pResidentLoginEntity.getUserName())) {
                throw new BusinessLogicException("The username already exists");
            }
            // No two residents with the same government Id
            if (resident.getGovernmentId().equals(pResidentLoginEntity.getGovernmentId())) {
                throw new BusinessLogicException("There is an user with that government Id");
            }
        }

        //must have a password
        if (pResidentLoginEntity.getPassword() == null) {
            throw new BusinessLogicException("A password has to be specified");
        }
        // Password must be at least 6 characters long
        if (pResidentLoginEntity.getPassword().length() < 6) {
            throw new BusinessLogicException("Password must be at least 6 characters long");
        }
        // Password must be at most 12 characters long
        if (pResidentLoginEntity.getPassword().length() > 12) {
            throw new BusinessLogicException("Password must be at most 12 characters long");
        }
        String passValue = String.valueOf(pResidentLoginEntity.getPassword());

        // At least one capital letter
        if (!passValue.matches(".*[A-Z].*")) {
            throw new BusinessLogicException("Password must contain at least one capital letter.");
        }
        // At least one number
        if (!passValue.matches(".*[0-9].*")) {
            throw new BusinessLogicException("Password must contain at least one number.");
        }
        // At least one special character
        if (!passValue.matches(".*[@#$%^&+=].*")) {
            throw new BusinessLogicException("Password must contain at least one special character.");
        }

        //must have a governmentId
        if (pResidentLoginEntity.getGovernmentId() == null) {
            throw new BusinessLogicException("A governmentId has to be specified");
        }

        pResidentLoginEntity.setResident(r);
        persistence.create(pResidentLoginEntity);
        LOGGER.log(Level.INFO, "Creation process for resident login eneded");

        return pResidentLoginEntity;
    }

    public void deleteResidentLogin(Long pNeighborhoodId, Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for resident login with id = {0}", id);
        persistence.delete(id, pNeighborhoodId);
        LOGGER.log(Level.INFO, "Ended deleting process for resident login with id = {0}", id);
    }

    public List<ResidentLoginEntity> getResidentLogins(Long pNeighborhoodId) {

        LOGGER.log(Level.INFO, "Starting querying process for all resident logins");
        List<ResidentLoginEntity> logins = persistence.findAll(pNeighborhoodId);
        LOGGER.log(Level.INFO, "Ended querying process for all resident logins");
        return logins;
    }

    public ResidentLoginEntity getResidentLogin(Long pNeighborhoodId, Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for resident login with id {0}", id);
        ResidentLoginEntity login = persistence.find(id,pNeighborhoodId);
        LOGGER.log(Level.INFO, "Ended querying process for resident login with id {0}", id);
        return login;
    }

    public ResidentLoginEntity updateResidentLogin(Long pNeighborhoodId, ResidentLoginEntity pResidentLoginEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for resident login with id {0}", pResidentLoginEntity.getId());

        //must have a username
        if (pResidentLoginEntity.getUserName() == null) {
            throw new BusinessLogicException("A username has to be specified");
        }
        for (ResidentLoginEntity resident : persistence.findAll(pNeighborhoodId)) {
            // No two residents with the same userame
            if (resident.getUserName().equals(pResidentLoginEntity.getUserName())) {
                throw new BusinessLogicException("The username already exists");
            }
            // No two residents with the same government Id
            if (resident.getGovernmentId().equals(pResidentLoginEntity.getGovernmentId())) {
                throw new BusinessLogicException("There is an user with that government Id");
            }
        }
        //must have a password
        if (pResidentLoginEntity.getPassword() == null) {
            throw new BusinessLogicException("A password has to be specified");
        }
        // Password must be at least 6 characters long
        if (pResidentLoginEntity.getPassword().length() < 6) {
            throw new BusinessLogicException("Password must be at least 6 characters long");
        }
        // Password must be at most 12 characters long
        if (pResidentLoginEntity.getPassword().length() > 12) {
            throw new BusinessLogicException("Password must be at most 12 characters long");
        }
        String passValue = String.valueOf(pResidentLoginEntity.getPassword());

        // At least one capital letter
        if (!passValue.matches(".*[A-Z].*")) {
            throw new BusinessLogicException("Password must contain at least one capital letter.");
        }
        // At least one number
        if (!passValue.matches(".*[0-9].*")) {
            throw new BusinessLogicException("Password must contain at least one number.");
        }
        // At least one special character
        if (!passValue.matches(".*[@#$%^&+=].*")) {
            throw new BusinessLogicException("Password must contain at least one special character.");
        }

        //must have a governmentId
        if (pResidentLoginEntity.getGovernmentId() == null) {
            throw new BusinessLogicException("A governmentId has to be specified");
        }

        ResidentLoginEntity modified = persistence.update(pResidentLoginEntity,pNeighborhoodId);
        LOGGER.log(Level.INFO, "Ended update process for resident login with id {0}", pResidentLoginEntity.getId());
        return modified;

    }
}
