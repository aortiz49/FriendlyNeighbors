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
package co.edu.uniandes.csw.neighborhood.dtos;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data transfer object for Events. This DTO contains the JSON representation of an Event that gets
 * transferred between the client and server.
 *
 * @author aortiz49
 */
public class EventDTO implements Serializable {
//===================================================
// Dependencies
//===================================================

    /**
     * The event's location.
     */
    private LocationDTO location;

    /**
     * The event's host.
     */
    private ResidentProfileDTO host;

//===================================================
// Attributes
//===================================================
    /**
     * The event's id.
     */
    private long id;

    /**
     * The title of the event.
     */
    private String title;

    /**
     * The date the event was posted.
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datePosted;

    /**
     * The date the event will take place.
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date dateOfEvent;

    /**
     * The time at which the event starts.
     */
    private String startTime;

    /**
     * The time at which the event ends.
     */
    private String endTime;

    /**
     * The description of the event.
     */
    private String description;

//===================================================
// Constructors
//===================================================
    /**
     * Empty EventDTO constructor.
     */
    public EventDTO() {
    }

    /**
     * Creates a EventDTO object from NeighborhoodEntity object.
     *
     * @param pEventEntity
     */
    public EventDTO(EventEntity pEventEntity) {

        if (pEventEntity != null) {
            // sets attributes
            this.id = pEventEntity.getId();
            this.title = pEventEntity.getTitle();
            this.datePosted = pEventEntity.getDatePosted();
            this.dateOfEvent = pEventEntity.getDateOfEvent();
            this.startTime = pEventEntity.getStartTime();
            this.endTime = pEventEntity.getEndTime();
            this.description = pEventEntity.getDescription();

            // sets relations
            LocationEntity eventLocation = pEventEntity.getLocation();
            if (eventLocation != null) {
                this.location = new LocationDTO(eventLocation);
            }

            ResidentProfileEntity eventhost = pEventEntity.getHost();
            if (eventhost != null) {
                this.host = new ResidentProfileDTO(eventhost);
            }
        }
    }

//===================================================
// Methods
//===================================================
    /**
     * Converts an EventDTO to an EventEntity.
     *
     * @return the converted event entity
     */
    public EventEntity toEntity() {
        EventEntity eventEntity = new EventEntity();

        // sets attributes
        eventEntity.setId(getId());
        eventEntity.setTitle(getTitle());
        eventEntity.setDatePosted(getDatePosted());
        eventEntity.setDateOfEvent(getDateOfEvent());
        eventEntity.setStartTime(getStartTime());
        eventEntity.setEndTime(getEndTime());
        eventEntity.setDescription(getDescription());

        // sets relations
        if (location != null) {
            eventEntity.setLocation(getLocation().toEntity());
        }
        if (host != null) {
            eventEntity.setHost(getHost().toEntity());
        }

        return eventEntity;
    }

    /**
     * Returns the string representation of the Event object.
     *
     * @return the object string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the event's location.
     *
     * @return the location
     */
    public LocationDTO getLocation() {
        return location;
    }

    /**
     * Sets the event's new location.
     * 
     * @param pLocation the new location
     */
    public void setLocation(LocationDTO pLocation) {
        location = pLocation;
    }

    /**
     * Returns the event's host.
     * 
     * @return the host
     */
    public ResidentProfileDTO getHost() {
        return host;
    }

    /**
     * Sets the event's new host.
     * 
     * @param pHost the new host
     */
    public void setHost(ResidentProfileDTO pHost) {
        host = pHost;
    }

    /**
     * Returns the event's id.
     * 
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the event's new id.
     * 
     * @param pId the new id
     */
    public void setId(long pId) {
        id = pId;
    }

    /**
     * Returns the event's title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the event's new title.
     * 
     * @param pTitle the new title
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    /**
     * Sets the date the event was posted.
     * 
     * @return the post date
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * Sets the new date the event was posted.
     * 
     * @param pDatePosted the new post date
     */
    public void setDatePosted(Date pDatePosted) {
        datePosted = pDatePosted;
    }

    /**
     * Returns the date the event was held.
     * 
     * @return the date of the event
     */
    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    /**
     * Sets the event's new date.
     * 
     * @param pDateOfEvent the new date of the event
     */
    public void setDateOfEvent(Date pDateOfEvent) {
        dateOfEvent = pDateOfEvent;
    }

    /**
     * Returns the event's start time.
     * 
     * @return the start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the event's new start time.
     * 
     * @param pStartTime the new start time
     */
    public void setStartTime(String pStartTime) {
        startTime = pStartTime;
    }

    /**
     * Returns the event's end time.
     * 
     * @return the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the event's new end time.
     * 
     * @param pEndTime the new end time
     */
    public void setEndTime(String pEndTime) {
        endTime = pEndTime;
    }

    /**
     * Returns the event's description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event's new description.
     * 
     * @param pDescription the new description
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

}
