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

//===================================================
// Imports
//===================================================
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.BusinessPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Class the implements the connection with the businessPersistence for the
 * Business entity.
 *
 * @author aortiz49
 */
@Stateless
public class BusinessLogic {

//===================================================
// Attributes
//===================================================
    /**
     * The logger used to send activity messages to the user.
     */
    private static final Logger LOGGER = Logger.getLogger(BusinessEntity.class.getName());

    /**
     * The injected business persistence object.
     */
    @Inject
    private BusinessPersistence businessPersistence;

    /**
     * The injected neighborhood persistence object.
     */
    @Inject
    private NeighborhoodPersistence neighborhoodPersistence;

//===================================================
// CRUD Methods
//===================================================
    /**
     * Creates and persists a new business
     *
     * @param pBusinessEntity the entity of type Business of the new business to
     * be persisted.
     * @return the business entity after it is persisted
     * @throws BusinessLogicException if the new business violates the business
     * rules
     */
    public BusinessEntity createBusiness(BusinessEntity pBusinessEntity) throws BusinessLogicException {

        // starts the logger for CREATE
        LOGGER.log(Level.INFO, "Begin business creation process");

        // verify business rules for creating a new business
        verifyBusinessRules(pBusinessEntity);
        
        // create the business
        BusinessEntity createdEntity = businessPersistence.create(pBusinessEntity);

        // ends the logger for CREATE
        LOGGER.log(Level.INFO, "End business creation process");
        return createdEntity;
    }

    /**
     * Devuelve todos los libros que hay en la base de datos.
     *
     * @return Lista de entidades de tipo libro.
     */
    public List<BusinessEntity> getBooks() {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar todos los libros");
        List<BusinessEntity> books = businessPersistence.findAll();
        LOGGER.log(Level.INFO, "Termina proceso de consultar todos los libros");
        return books;
    }

    /**
     * Busca un libro por ID
     *
     * @param booksId El id del libro a buscar
     * @return El libro encontrado, null si no lo encuentra.
     */
    public BusinessEntity getBook(Long booksId) {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar el libro con id = {0}", booksId);
        BusinessEntity bookEntity = businessPersistence.find(booksId);
        if (bookEntity == null) {
            LOGGER.log(Level.SEVERE, "El libro con el id = {0} no existe", booksId);
        }
        LOGGER.log(Level.INFO, "Termina proceso de consultar el libro con id = {0}", booksId);
        return bookEntity;
    }

    /**
     * Actualizar un libro por ID
     *
     * @param booksId El ID del libro a actualizar
     * @param bookEntity La entidad del libro con los cambios deseados
     * @return La entidad del libro luego de actualizarla
     * @throws BusinessLogicException Si el IBN de la actualización es inválido
     */
    public BusinessEntity updateBook(Long booksId, BusinessEntity bookEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de actualizar el libro con id = {0}", booksId);

        BusinessEntity newEntity = businessPersistence.update(bookEntity);
        LOGGER.log(Level.INFO, "Termina proceso de actualizar el libro con id = {0}", bookEntity.getId());
        return newEntity;
    }

    /**
     * Eliminar un libro por ID
     *
     * @param booksId El ID del libro a eliminar
     * @throws BusinessLogicException si el libro tiene autores asociados
     */
    public void deleteBook(Long booksId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de borrar el libro con id = {0}", booksId);

        businessPersistence.delete(booksId);
        LOGGER.log(Level.INFO, "Termina proceso de borrar el libro con id = {0}", booksId);
    }

    /**
     * Verifies that the the business is valid.
     *
     * @param pBusinessEntity business to verify
     * @return true if the business is valid. False otherwise
     * @throws BusinessLogicException if the business doesn't satisfy the
     * business rules
     */
    private boolean verifyBusinessRules(BusinessEntity pBusinessEntity) throws BusinessLogicException {
        boolean valid = true;

        // the neighborhood the potential business belongs to 
        NeighborhoodEntity businessNeighborhood = pBusinessEntity.getNeighborhood();

        // 1. The neighborhood to which the business will be added to must already exist
        if (neighborhoodPersistence.find(businessNeighborhood.getId()) == null) {
            throw new BusinessLogicException("The business's neighborhood doesn't exist!");
        }

        // 2. The neighborhood the potential business belongs to cannot be null
        if (businessNeighborhood == null) {
            throw new BusinessLogicException("The business must belong to a neighborhood!");
        }

        // 3. No two businesses can have the same name
        if (businessPersistence.findByName(pBusinessEntity.getName()) != null) {
            throw new BusinessLogicException("The neighborhood already has a business with that name!");
        }

        // 4. The address of the business cannot be null
        if (pBusinessEntity.getAddress() == null) {
            throw new BusinessLogicException("The business address cannot be null!");
        }

        // 4. The name of the business cannot be null
        if (pBusinessEntity.getName() == null) {
            throw new BusinessLogicException("The business name cannot be null!");
        }
        return valid;
 
    }
}
