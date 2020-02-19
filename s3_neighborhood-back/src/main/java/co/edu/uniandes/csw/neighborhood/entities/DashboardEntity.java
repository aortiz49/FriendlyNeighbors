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
     * The businesses associated with the dashboard.
     */
    @PodamExclude
    @OneToMany(mappedBy = "dashboard", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BusinessEntity> businesses = new ArrayList<>();

//===================================================
// Attributes
//===================================================
    /**
     * The total revenue from the sold products.
     */
    private Double totalRevenue;

//===================================================
// Getters & Setters
//===================================================
   
    /**
     * Returns the businesses associated with the dashboard.
     *
     * @return the associated business
     */
    public List<BusinessEntity> getBusiness() {
        return businesses;
    }

    /**
     * Sets the new businesses associated with the dashboard.
     *
     * @param pBusiness the new associated business
     */
    public void setBusiness(List<BusinessEntity> pBusinesses) {
        businesses = pBusinesses;
    }

    /**
     * Returns the total revenue.
     *
     * @return the total revenue
     */
    public Double getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Sets the total revenue.
     *
     * @param pTotalRevenue the new total revenue
     */
    public void setTotalRevenue(Double pTotalRevenue) {
        totalRevenue = pTotalRevenue;
    }

}
