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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author v.cardonac1
 */
@Entity
public class LocationEntity extends BaseEntity implements Serializable {

//===================================================
// Attributes
//===================================================  
    /**
     * Represents the name of this location
     */
    private String name;

    /**
     * Represents the address of this location
     */
    private String address;
    /**
     * Represents the time this location is open from
     */
    private String openTime;

    /**
     * Represents the time this location closed from
     */
    private String closeTime;

    /**
     * Indicates the latitude of this location
     */
    private Double latitude;

    /**
     * Indicates the longitude of this location
     */
    private Double longitude;

//===================================================
// Relations
//===================================================
    /**
     * Indicates the neighborhood this location belongs to
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    /**
     * Indicates the events this location will be used for
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "location",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )

    private List<EventEntity> events = new ArrayList<>();

    /**
     * Gets the time this location is open from
     *
     * @return the time this location is open from
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * Gets the name of this location
     *
     * @return the name of this location
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the address of this location
     *
     * @return the address of this location
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the time this location closed from
     *
     * @return the time this location closed from
     */
    public String getCloseTime() {
        return closeTime;
    }

    /**
     * Gets the latitude of this location
     *
     * @return the latitude of this location
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of this location
     *
     * @return the longitude of this location
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Gets the neighborhood this location belongs to
     *
     * @return the neighborhood
     */
    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    /**
     * Gets the events this location will be used for
     *
     * @return the events this location will be used for
     */
    public List<EventEntity> getEvents() {
        return events;
    }

    /**
     * Sets the time this location is open from
     *
     * @param openTime the time this location is open from
     */
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    /**
     * Sets the name of this location
     *
     * @param name the name of this location
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the address of this location
     *
     * @param address the address of this location
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the time this location closed from
     *
     * @param closeTime the time this location closed from
     */
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * Sets the latitude of this location
     *
     * @param latitude the latitude of this location
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude of this location
     *
     * @param longitude the latitude of this location
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the neighborhood this location belongs to
     *
     * @param neighborhood
     */
    public void setNeighborhood(NeighborhoodEntity neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Sets the events this location will be used for
     *
     * @param events the events this location will be used for
     */
    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }

    //TODO: implement location availablility for events
}
