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
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 *
 * @author andre
 */
@Entity
public class EventEntity extends BaseEntity implements Serializable {

    //===================================================
    // Attributes
    //===================================================
    /**
     * Represents the date the event was published.
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date datePosted;

    /**
     * Represents the date the event took place.
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date dateOfEvent;

    /**
     * The title of the event.
     */
    private String title;

    /**
     * The start time of the event.
     */
    private String startTime;

    /**
     * The end time of the event.
     */
    private String endTime;

    /**
     * The description of the event.
     */
    private String description;

    /**
     * The availability of the event. How long the event will take place.
     */
    private String availability;
    
    
    //===================================================
    // Relations
    //===================================================

    /**
     * The host of the event.
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity host;

    /**
     * The location of the event.
     */
    @PodamExclude
    @ManyToOne
    private LocationEntity location;
   

    /**
     * The residents attending the event.
     */
    @PodamExclude
    @ManyToMany          
    private List<ResidentProfileEntity> attendees = new ArrayList<ResidentProfileEntity>();
  
    
//===================================================
// Getters & Setters
//===================================================

    public Date getDatePosted() {
        return datePosted;
    }

    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public String getAvailability() {
        return availability;
    }

    public ResidentProfileEntity getHost() {
        return host;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public List<ResidentProfileEntity> getAttendees() {
        return attendees;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setHost(ResidentProfileEntity host) {
        this.host = host;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public void setAttendees(List<ResidentProfileEntity> attendees) {
        this.attendees = attendees;
    }

  
    
    
}