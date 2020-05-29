/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import java.io.Serializable;

/**
 *
 * @author v.cardonac1
 */
public class LocationDTO implements Serializable{

//===================================================
// Attributes
//=================================================== 
    /**
     * Represents id for this group
     */
    private Long id;

    /**
     * Represents the time this location is open from
     */
    private String openTime;

    /**
     * Represents the name of this location
     */
    private String name;

    /**
     * Represents the address of this location
     */
    private String address;

    /**
     * Represents the time this location closed from
     */
    private String closeTime;

    /**
     * Indicates if this this location is available
     */
    private Boolean available;

    /**
     * Indicates the latitude of this location
     */
    private Double latitude;

    /**
     * Indicates the longitude of this location
     */
    private Double longitude;

    /**
     * The neighborhood to which the group belongs to.
     */
    private NeighborhoodDTO neighborhood;

    public LocationDTO() {
    }

    public LocationDTO(LocationEntity entityLocation) {

        this.address = entityLocation.getAddress();
        this.id = entityLocation.getId();
        this.closeTime = entityLocation.getCloseTime();
        this.latitude = entityLocation.getLatitude();
        this.longitude = entityLocation.getLongitude();
        this.name = entityLocation.getName();
        this.neighborhood = new NeighborhoodDTO(entityLocation.getNeighborhood());
        this.openTime = entityLocation.getOpenTime();

    }

    public LocationEntity toEntity() {
        LocationEntity locationEntity = new LocationEntity();

        locationEntity.setId(getId());
        locationEntity.setAddress(getAddress());
        locationEntity.setCloseTime(getCloseTime());
        locationEntity.setLatitude(getLatitude());
        locationEntity.setLongitude(getLongitude());
        locationEntity.setName(getName());
        locationEntity.setOpenTime(getOpenTime());

        if (neighborhood != null) {
            locationEntity.setNeighborhood(getNeighborhood().toEntity());
        }

        return locationEntity;
    }

    /**
     * @return the openTime
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * @param openTime the openTime to set
     */
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the closeTime
     */
    public String getCloseTime() {
        return closeTime;
    }

    /**
     * @param closeTime the closeTime to set
     */
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * @return the available
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the neighborhood
     */
    public NeighborhoodDTO getNeighborhood() {
        return neighborhood;
    }

    /**
     * @param neighborhood the neighborhood to set
     */
    public void setNeighborhood(NeighborhoodDTO neighborhood) {
        this.neighborhood = neighborhood;
    }
}
