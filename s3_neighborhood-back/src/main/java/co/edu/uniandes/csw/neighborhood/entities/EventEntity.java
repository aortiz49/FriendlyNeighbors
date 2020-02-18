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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 * Entity representing an event.
 *
 * @author kromero1
 */
@Entity
public class EventEntity extends BaseEntity implements Serializable {
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
     *  The group hosting the event.
     */
    @PodamExclude
    @ManyToOne
    private GroupEntity group;
    
    
    /**
     * The residents attending the event.
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "event",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    private List<ResidentProfileEntity> attendees = new ArrayList<>();

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
// Getters & Setters
//===================================================
    /**
     * Returns the host of the event.
     *
     * @return the host of the event
     */
    public ResidentProfileEntity getHost() {
        return host;
    }

    /**
     * Sets the host of the event.
     *
     * @param pHost the new host
     */
    public void setHost(ResidentProfileEntity pHost) {
        host = pHost;
    }

    /**
     * Returns the list of attendees.
     *
     * @return the list of attendees
     */
    public List<ResidentProfileEntity> getAttendees() {
        return attendees;
    }

    /**
     * Sets the list of attendees.
     *
     * @param pAttendees the new list of attendees
     */
    public void setAttendees(List<ResidentProfileEntity> pAttendees) {
        attendees = pAttendees;
    }

    /**
     * Returns the date the event was posted.
     *
     * @return the date the event was posted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * Sets the date the evetn was posted.
     *
     * @param pDatePosted the new post date
     */
    public void setDatePosted(Date pDatePosted) {
        datePosted = pDatePosted;
    }

    /**
     * Returns the date the event took place.
     *
     * @return the date the event took place
     */
    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    /**
     * Set sthe date the event took place.
     *
     * @param pDateOfEvent the date the event took place
     */
    public void setDateOfEvent(Date pDateOfEvent) {
        dateOfEvent = pDateOfEvent;
    }

    /**
     * Returns the title of the event.
     *
     * @return the event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event.
     *
     * @param pTitle the new event title
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    /**
     * Returns the description of the event.
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param pDescription the new event description
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    /**
     * Returns the avilability of the event.
     *
     * @return the availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Sets the availability of the event.
     *
     * @param pAvailability the new availability
     */
    public void setAvailability(String pAvailability) {
        availability = pAvailability;
    }

}
