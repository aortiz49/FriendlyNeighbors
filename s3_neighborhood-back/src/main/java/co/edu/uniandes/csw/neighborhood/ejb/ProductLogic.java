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

import co.edu.uniandes.csw.neighborhood.persistence.ProductPersistence; 
import co.edu.uniandes.csw.neighborhood.entities.ProductEntity; 
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException; 


/**
 *
 * @author kromero1
 */
@Stateless
public class ProductLogic {


    private static final Logger LOGGER = Logger.getLogger(ProductLogic.class.getName());

    @Inject
    private ProductPersistence persistence;


      public ProductEntity createProduct(ProductEntity productEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for product has started");

        
        if(productEntity.getName()== null){
            throw new BusinessLogicException("A Title has to be specified");
        }

         
        if(productEntity.getPrice()<= 0){
            throw new BusinessLogicException(" the price cannot be 0");
        }

        if(productEntity.getMaxSaleQuantity()<0){
            throw new BusinessLogicException(" the amount cannot be less than 0");
        }

        persistence.create(productEntity);
        LOGGER.log(Level.INFO, "Creation process for product eneded");

        return productEntity;
    }



    public void deleteProduct(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for product with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for product with id = {0}", id);
    }


    public List<ProductEntity> getProducts() {

        LOGGER.log(Level.INFO, "Starting querying process for all products");
        List<ProductEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all products");
        return residents;
    }

    public ProductEntity getProduct(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for product with id ", id);
        ProductEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  product with id", id);
        return resident;
    }




    public ProductEntity updateProduct(ProductEntity productEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for product with id ", productEntity.getId());

        ProductEntity original  = persistence.find(productEntity.getId());

         
        if(productEntity.getName()== null){
            throw new BusinessLogicException("A Title has to be specified");
        }

         
        if(productEntity.getPrice()<= 0){
            throw new BusinessLogicException(" the price cannot be 0");
        }

        if(productEntity.getMaxSaleQuantity()<0){
            throw new BusinessLogicException(" the amount cannot be less than 0");
        }

        ProductEntity modified = persistence.update(productEntity);
        LOGGER.log(Level.INFO, "Ended update process for product with id ", productEntity.getId());
        return modified;
    }




}