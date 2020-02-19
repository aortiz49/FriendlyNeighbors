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
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Represents the logic for the Neighborhood entity.
 *
 * @author aortiz49
 */
@Stateless
public class NeighborhoodLogic {
    //===================================================
    // Attributes
    //===================================================
    /**
     * Logger to log messages for the business persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            BusinessPersistence.class.getName());
    
    @Inject
    private NeighborhoodPersistence persistence;

    public NeighborhoodEntity createNeighborhood(NeighborhoodEntity pNeighborhood) throws BusinessLogicException {

        LOGGER.info("Starts the process of creating a neighborhood.");
        if (neighborhoodExists(pNeighborhood.getId())) {
            throw new BusinessLogicException("Ya existe una ciudad con ese id. No se puede crear. ");
        }
        
        if(pNeighborhood.getName())
        
        persistence.create(pNeighborhood);
        LOGGER.info("Termina proceso de creación de ciudad");
        return pNeighborhood;
    }

    
    
    
    
     /**
      * Checks if there are neighborhoods matching the given id.
      * 
      * @param pNeighborhoodId the neighborhood id
      * @return true if a neighborhood exists with the given id, false if otherwise
      */
    private boolean neighborhoodExists(Long pNeighborhoodId) {
        NeighborhoodEntity hood = persistence.find(pNeighborhoodId);
        if (hood == null) {
            return false;
        }
        return true;
    }

    
    
}
