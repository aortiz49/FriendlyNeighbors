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
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity that represents the dashboard.
 *
 * @author carlos
 */
@Entity
public class DashboardEntity extends BaseEntity implements Serializable {
//===================================================
// Imports
//===================================================

    /**
     * The business associated with the dashboard.
     */
    @PodamExclude
    @OneToOne
    private BusinessEntity business;

    /**
     * The saleInformation information in the dashboard.
     */
    @PodamExclude
    @OneToOne
    private SaleEntity saleInformation;

    /**
     * The list of products sold.
     */
    @PodamExclude
    @OneToMany(mappedBy = "dashboard", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ProductEntity> productList = new ArrayList<>();

//===================================================
// Attributes
//===================================================
    /**
     * The total revenue from the sold products.
     */
    private int totalRevenue;

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the saleInformation information.
     *
     * @return the saleInformation information
     */
    public SaleEntity getSaleInformation() {
        return saleInformation;
    }

    /**
     * Sets the saleInformation information. 
     * 
     * @param pSaleInformation the new saleInformation information.
     */
    public void setSaleInformation(SaleEntity pSaleInformation) {
        saleInformation = pSaleInformation;
    }

    /**
     * Returns the list of products sold.
     * 
     * @return list of products sold
     */
    public List<ProductEntity> getProductList() {
        return productList;
    }

    /**
     * Sets the list of products sold.
     * 
     * @param pProductList the new list of products sold
     */
    public void setProductList(List<ProductEntity> pProductList) {
        productList = pProductList;
    }

    /**
     * Returns the business associated with the dashboard.
     * 
     * @return the associated business
     */
    public BusinessEntity getBusiness() {
        return business;
    }

    /**
     * Sets the new business associated with the dashboard.
     * 
     * @param pBusiness the new associated business 
     */
    public void setBusiness(BusinessEntity pBusiness) {
        business = pBusiness;
    }

    /**
     * Returns the total revenue.
     * 
     * @return the total revenue
     */
    public int getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Sets the total revenue.
     * 
     * @param pTotalRevenue the new total revenue 
     */
    public void setTotalRevenue(int pTotalRevenue) {
        totalRevenue = pTotalRevenue;
    }

}
