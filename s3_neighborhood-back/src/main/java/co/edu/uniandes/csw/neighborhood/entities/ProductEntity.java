/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * 
 * @author kevin
 */
@Entity
public class ProductEntity extends BaseEntity implements Serializable{
    
//===================================================
// Attributes
//=================================================== 
    /**
     * The name of this product
     */
    private String name; 
    
    /**
     * A descrition to this product
     */
    private String description;
    
    /**
     * The price of this product
     */
    @PodamExclude
    private Double price;

    /**
     * How many units of this product are in stock
     */
    private Integer availableQuantity;
    
     /**
     * The number of times this product can be sold at a time
     */
    @PodamExclude
    private Integer maxSaleQuantity;

        
    /**
     * The number of this product units that have been sold
     */
    private Integer unitsSold;

//===================================================
// Relations
//===================================================
    
    /**
     * Represent the offers this product is included in
     */
    @PodamExclude
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<OfferEntity> offers = new ArrayList<>();
    

    /**
     * Represents the bussunes this product belongs to
     */    
    @PodamExclude
    @ManyToOne BusinessEntity business;
    
//===================================================
// Getters & Setters
//===================================================  
    /**
     * Gets name.
     *
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets description.
     *
     * @return value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets price.
     *
     * @return value of price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Gets availableQuantity.
     *
     * @return value of availableQuantity
     */
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Gets maxSaleQuantity.
     *
     * @return value of maxSaleQuantity
     */
    public Integer getMaxSaleQuantity() {
        return maxSaleQuantity;
    }

    /**
     * Gets unitsSold.
     *
     * @return value of unitsSold
     */
    public Integer getUnitsSold() {
        return unitsSold;
    }

    /**
     * Sets name.
     *
     * @param name value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets description.
     *
     * @param description value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets price.
     *
     * @param price value of price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Sets availableQuantity.
     *
     * @param availableQuantity value of availableQuantity
     */
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Sets maxSaleQuantity.
     *
     * @param maxSaleQuantity value of maxSaleQuantity
     */
    public void setMaxSaleQuantity(Integer maxSaleQuantity) {
        this.maxSaleQuantity = maxSaleQuantity;
    }

    /**
     * Sets unitsSold.
     *
     * @param unitsSold value of unitsSold
     */
    public void setUnitsSold(Integer unitsSold) {
        this.unitsSold = unitsSold;
    }

    /**
     * Gets the offers this product is included in
     * @return the offers this product is included in
     */
    public List<OfferEntity> getOffers() {
        return offers;
    }

    /**
     * Gets the bussunes this product belongs to
     * @return the bussunes this product belongs to
     */
    public BusinessEntity getBusiness() {
        return business;
    }

    /**
     * Sets the offers this product is included in
     * @param offers  the offers this product is included in
     */
    public void setOffers(List<OfferEntity> offers) {
        this.offers = offers;
    }

    /**
     * Sets the bussunes this product belongs to
     * @param business the bussunes this product belongs to
     */
    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }
    
    
    
    

    

}
