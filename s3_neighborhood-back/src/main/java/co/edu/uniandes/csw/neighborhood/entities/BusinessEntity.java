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
//===================================================
// Imports
//===================================================

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Enitity that represents a busuiness.
 *
 * @author aortiz49
 */
@Entity
public class BusinessEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//===================================================

    /**
     * The owner of the business.
     */
    @PodamExclude
    @ManyToOne
    private BusinessOwnerEntity owner;

    /**
     * The business' dashboard
     */
    @PodamExclude
    @ManyToOne
    private DashboardEntity dashboard;

    /**
     * The neighborhood the business belongs to.
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    /**
     * The products the business sells.
     */
    @PodamExclude
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    /**
     * The offers the business has on its products.
     */
    @PodamExclude
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferEntity> offers = new ArrayList<>();

    /**
     * The posts the business publishes.
     */
    @PodamExclude
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

     /**
     * The notifications the business receives.
     */
    @PodamExclude
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications = new ArrayList<>();

//===================================================
// Attributes
//===================================================
    /**
     * The name of the business
     */
    private String name;

    /**
     * The address where the business is located.
     */
    private String address;

    /**
     * The time at which the business opens.
     */
    private String openingTime;

    /**
     * The time at which the business closes.
     */
    private String closingTime;

    /**
     * The rating on a scale of 1 to 5 of the business.
     */
    private double rating;

    /**
     * The business' latitude.
     */
    private Double latitude;

    /**
     * The business' longitude.
     */
    private Double longitude;

    /**
     * The business' total revenue.
     */
    private Double totalRevenue;

    /**
     * The amount of products sold vs the amount of products in inventory.
     */
    private Double percentOfProductsSold;

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the owner of the business.
     *
     * @return the business owner
     */
    public BusinessOwnerEntity getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the business.
     *
     * @param pOwner the new business owner
     */
    public void setOwner(BusinessOwnerEntity pOwner) {
        owner = pOwner;
    }

    /**
     * Returns the business' dashboard.
     *
     * @return the business' dashborad
     */
    public DashboardEntity getDashboard() {
        return dashboard;
    }

    /**
     * Sets the business' dashboard.
     *
     * @param pDashboard the new business dashboard
     */
    public void setDashboard(DashboardEntity pDashboard) {
        dashboard = pDashboard;
    }

    /**
     * Returns the neighborhood the business is in.
     *
     * @return the business' neighborhood
     */
    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood the busness is in.
     *
     * @param pNeighborhood the business' new neighborhood
     */
    public void setNeighborhood(NeighborhoodEntity pNeighborhood) {
        neighborhood = pNeighborhood;
    }

    /**
     * Returns the list of products in the business.
     *
     * @return list of products
     */
    public List<ProductEntity> getProducts() {
        return products;
    }

    /**
     * Sets the list of products in the business.
     *
     * @param pProducts the new list of products
     */
    public void setProducts(List<ProductEntity> pProducts) {
        products = pProducts;
    }

    /**
     * Returns the list of offers the businesss has.
     *
     * @return the list of offers
     */
    public List<OfferEntity> getOffers() {
        return offers;
    }

    /**
     * Sets the list of offers the business has.
     *
     * @param pOffers the new list of offers
     */
    public void setOffers(List<OfferEntity> pOffers) {
        offers = pOffers;
    }

    /**
     * Returns the name of the business.
     *
     * @return business' name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the business.
     *
     * @param pName the new business name
     */
    public void setName(String pName) {
        name = pName;
    }

    /**
     * Returns the address of the business.
     *
     * @return the business' address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the business.
     *
     * @param pAddress the new business address
     */
    public void setAddress(String pAddress) {
        address = pAddress;
    }

    /**
     * Returns the opening time of the business.
     *
     * @return the time the business opens
     */
    public String getOpeningTime() {
        return openingTime;
    }

    /**
     * Sets the opening time of the businss.
     *
     * @param pOpeningTime the new opening time of the business
     */
    public void setOpeningTime(String pOpeningTime) {
        openingTime = pOpeningTime;
    }

    /**
     * Returns the closing time of the business.
     *
     * @return the time the business closes
     */
    public String getClosingTime() {
        return closingTime;
    }

    /**
     * Sets the closing time of the business.
     *
     * @param pClosingTime the new closing time of the business
     */
    public void setClosingTime(String pClosingTime) {
        closingTime = pClosingTime;
    }

    /**
     * Returns the business' rating.
     *
     * @return the business' rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the business' rating.
     * <p>
     * The rating is on a scale from 1 to 5, set by a resident of the
     * neighborhood.
     *
     * @param pRating
     */
    public void setRating(double pRating) {
        rating = pRating;
    }

    /**
     * Returns the latitude of the business.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the business.
     *
     * @param pLatitude the new latitude
     */
    public void setLatitude(Double pLatitude) {
        latitude = pLatitude;
    }

    /**
     * Retuns the longitude of the business.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the business.
     *
     * @param pLongitude the new longitude
     */
    public void setLongitude(Double pLongitude) {
        longitude = pLongitude;
    }

    /**
     * Returns the total revenue of the business.
     *
     * @return the total revenue
     */
    public Double getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Sets the total revenue of the business.
     *
     * @param pTotalRevenue the new total revenue.
     */
    public void setTotalRevenue(Double pTotalRevenue) {
        totalRevenue = pTotalRevenue;
    }

    /**
     * Returns the percent of products sold at the business.
     *
     * @return the percent of products sold
     */
    public Double getPercentOfProductsSold() {
        return percentOfProductsSold;
    }

    /**
     * Sets the percent of products sold at the business.
     *
     * @param pPercentOfProductsSold the new percent of products sold
     */
    public void setPercentOfProductsSold(Double pPercentOfProductsSold) {
        percentOfProductsSold = pPercentOfProductsSold;
    }

    public List<PostEntity> getPosts() {
        return posts;
    }

    public void setPosts(List<PostEntity> posts) {
        this.posts = posts;
    }

    public List<NotificationEntity> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
    }
    
    
    
    
    
    
    
    

}
