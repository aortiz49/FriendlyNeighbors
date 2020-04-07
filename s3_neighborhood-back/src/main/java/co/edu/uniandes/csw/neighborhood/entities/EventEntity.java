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

import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 * Entity that represents and event.
 * 
 * @author aortiz49
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

    /**
     * The groups attending the event.
     */
    @PodamExclude
    @ManyToMany(mappedBy = "events", fetch = FetchType.EAGER)
    private List<GroupEntity> groups = new ArrayList<>();

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

    public void setHost(ResidentProfileEntity host) {
        this.host = host;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public void setAttendees(List<ResidentProfileEntity> attendees) {
        this.attendees = attendees;
    }

    public List<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupEntity> groups) {
        this.groups = groups;
    }

}
