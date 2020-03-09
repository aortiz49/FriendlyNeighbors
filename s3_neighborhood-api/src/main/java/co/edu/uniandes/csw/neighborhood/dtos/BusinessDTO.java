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
package co.edu.uniandes.csw.neighborhood.dtos;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data transfer object for Businesses. This DTO contains the json representation of a Business that
 * gets transferred between the client and server.
 *
 * Upon JSON serialization, this class uses the following model:  <br>
 * <pre>
 *   {
 *      "name": string,
 *      "address": string,
 *      "openingTime": string,
 *      "closingTime": string,
 *      "rating": number,
 *      "latitude": number,
 *      "longitude": number,
 *
 *   }
 * </pre> For example, a business can represented as follows:<br>
 *
 * <pre>
 *
 *   {
 *      "name": Andy Ortiz,
 *      "address": Calle 24b Bis #69a-50,
 *      "openingTime": 08:45AM,
 *      "closingTime": 09:45APM,
 *      "rating": 1.44,
 *      "latitude": -4.717152,
 *      "longitude": 11.877332
 *   }
 *
 * </pre>
 *
 * @author aortiz49
 */
public class BusinessDTO implements Serializable {
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
// Constructor
//===================================================
    /**
     * Empty Business constructor.
     */
    public BusinessDTO() {
    }

    /**
     * Creates a BusinessDTO object from a BusinessEntity object.
     *
     * @param pBusinessEntity business entity from which to create the new BusinessDTO
     *
     */
    public BusinessDTO(BusinessEntity pBusinessEntity) {
        if (pBusinessEntity != null) {
            this.name = pBusinessEntity.getName();
            this.address = pBusinessEntity.getAddress();
            this.openingTime = pBusinessEntity.getOpeningTime();
            this.closingTime = pBusinessEntity.getClosingTime();
            this.rating = pBusinessEntity.getRating();
            this.latitude = pBusinessEntity.getLatitude();
            this.longitude = pBusinessEntity.getLongitude();

        }
    }

    /**
     * Converts a BusinessDTO object into an AuthorEntity.
     *
     * @return the new BusinessEntity object
     *
     */
    public BusinessEntity toEntity() {
        BusinessEntity businessEntity = new BusinessEntity();
        businessEntity.setName(this.getName());
        businessEntity.setAddress(this.getAddress());
        businessEntity.setOpeningTime(this.getOpeningTime());
        businessEntity.setClosingTime(this.getClosingTime());
        businessEntity.setRating(this.getRating());
        businessEntity.setLatitude(this.getLatitude());
        businessEntity.setLongitude(this.getLongitude());

        return businessEntity;
    }
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
     * The rating is on a scale from 1 to 5, set by a resident of the neighborhood.
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
