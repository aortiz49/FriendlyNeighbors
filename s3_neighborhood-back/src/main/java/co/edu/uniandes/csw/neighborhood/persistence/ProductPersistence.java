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
package co.edu.uniandes.csw.neighborhood.persistence;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.ProductEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Class that manages the persistence for the Product. It connects via the
 * Entity Manager in javax.persistance with a SQL database.
 *
 * @author kromero1
 */
@Stateless
public class ProductPersistence {
//===================================================
// Attributes
//===================================================

    /**
     * Logger to log messages for the product persistence.
     */
    private static final Logger LOGGER = Logger.getLogger(
            ProductPersistence.class.getName());

    /**
     * The entity manager that will access the Product table.
     */
    @PersistenceContext(unitName = "neighborhoodPU")
    protected EntityManager em;

    //===================================================
    // CRUD Methods
    //===================================================
    /**
     * Persists a product in the database.
     *
     * @param pProductEntity product object to be created in the
     * databse
     * @return the created product with an id given by the databse
     */
    public ProductEntity create(ProductEntity pProductEntity) {
        // logs a message
        LOGGER.log(Level.INFO, "Creating a new product");

        // makes the entity instance managed and persistent
        em.persist(pProductEntity);
        LOGGER.log(Level.INFO, "Product created");

        return pProductEntity;
    }

    /**
     * Returns all products in the database.
     *
     * @return a list containing every product in the database. select u
     * from ProductEntity u" is akin to a "SELECT * from
     * ProductEntity" in SQL.
     */
    public List<ProductEntity> findAll() {
        // log the consultation
        LOGGER.log(Level.INFO, "Consulting all products");

        // Create a typed product entity query to find all Products 
        // in the database. 
        TypedQuery<ProductEntity> query = em.createQuery(
                "select u from ProductEntity u", ProductEntity.class);

        return query.getResultList();
    }

    /**
     * Looks for a product with the id given by the parameter.
     *
     * @param pProductId the id corresponding to the product
     * @return the found product
     */
    public ProductEntity find(Long pProductId) {
        LOGGER.log(Level.INFO, "Consulting product with id={0}",
                pProductId);

        return em.find(ProductEntity.class, pProductId);
    }

    /**
     * Updates an product.
     *
     * @param pProductEntity the product with the modifications. For
     * example, the name could have changed. In that case, we must use this
     * update method.
     * @return the product with the updated changes
     */
    public ProductEntity update(ProductEntity pProductEntity) {
        LOGGER.log(Level.INFO, "Updating product with id = {0}",
                pProductEntity.getId());
        return em.merge(pProductEntity);
    }

    /**
     * Deletes an product.
     * <p>
     *
     * Deletes the product with the associated Id.
     *
     * @param pProductId the id of the product to be deleted
     */
    public void delete(Long pProductId) {
        LOGGER.log(Level.INFO, "Deleting product with id = {0}",
                pProductId);
        ProductEntity reviewEntity = em.find(ProductEntity.class,
                pProductId);
        em.remove(reviewEntity);
        LOGGER.log(Level.INFO,
                "Exiting the deletion of product with id = {0}",
                pProductId);
    }

}
