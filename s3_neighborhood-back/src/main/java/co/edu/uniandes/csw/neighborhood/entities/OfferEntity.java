/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 *
 * @author Carlos Figueredo
 */
@Entity
public class OfferEntity extends BaseEntity implements Serializable{
    
//===================================================
// Attributes
//===================================================
    
    /**
     * Indicates how long this offer will be on
     */   
    private String duration;
    
    /**
     * Indicates the discint applied to products in this offer
     */
    private double discountFactor;
    
     /**
     * Represents the date this offer will begin
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date startDate;
    
//===================================================
// Relations
//===================================================
        
    /**
     * Represents the business that made this offer
     */
    @PodamExclude
    @ManyToOne
    private BusinessEntity business;
    
    /**
     * Represents the products included in this offer
     */
    @PodamExclude
    @ManyToMany
    private List<ProductEntity> products = new ArrayList<>();
    
    
    
//===================================================
// Getters & Setters
//===================================================  
    /**
     * Gets duration.
     *
     * @return value of duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Gets discountFactor.
     *
     * @return value of discountFactor
     */
    public double getDiscountFactor() {
        return discountFactor;
    }

    /**
     * Gets startDate.
     *
     * @return value of startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets business
     * @return business
     */
    public BusinessEntity getBusiness() {
        return business;
    }
    /**
     * Sets products
     * @return products
     */
    public List<ProductEntity> getProducts() {
        return products;
    }
    
     /**
     * Sets duration.
     *
     * @param duration value of duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Sets discountFactor.
     *
     * @param discountFactor value of discountFactor
     */
    public void setDiscountFactor(double discountFactor) {
        this.discountFactor = discountFactor;
    }

    /**
     * Sets startDate.
     *
     * @param startDate value of startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets business
     * @param business new business
     */
    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    /**
     * Sets products
     * @param products new products 
     */
    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
    
    
    

}
