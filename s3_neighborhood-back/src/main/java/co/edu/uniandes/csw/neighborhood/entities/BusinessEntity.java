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
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity that represents a business.
 *
 * @author aortiz49
 */
@Entity
public class BusinessEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//===================================================

    /**
     * The neighborhood the business belongs to.
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;
    
     /**
     * The resident the business belongs to.
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity owner;


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
     * The business's latitude.
     */
    private Double latitude;

    /**
     * The business's longitude.s
     */
    private Double longitude;


//===================================================
// Getters & Setters
//===================================================
   
    /**
     * Returns the name of the business.
     *
     * @return business's name
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
     * @return the business's address
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
     * Sets the opening time of the business.
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
     * Returns the business's rating.
     *
     * @return the business's rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the business's rating.
     * <p>
     * The rating is on a scale from 1 to 5, set by a resident of the
     * neighborhood.
     * </p>
     *
     * @param pRating the new business's rating
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
     * Returns the longitude of the business.
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
  
    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(NeighborhoodEntity neighborhood) {
        this.neighborhood = neighborhood;
    }

    public ResidentProfileEntity getOwner() {
        return owner;
    }

    public void setOwner(ResidentProfileEntity owner) {
        this.owner = owner;
    }
    
    

}
