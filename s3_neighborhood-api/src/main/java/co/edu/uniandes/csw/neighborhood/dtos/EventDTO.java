package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import uk.co.jemos.podam.common.PodamExclude;


/**
* @k.romero
*/
public class EventDTO implements Serializable{
/**
     * The title of the event.
     */
   private String title;

   /**
     * Represents the date  was created
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datePosted;
    
    
    
    
    /**
     * Represents the date group was created
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date dateOfEvent;
    
    
    
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
    /**
     * The host of the event.
     */
     private ResidentProfileDTO host;
     /**
     * The location of the event.
     */
    
    private LocationDTO location;
//===================================================
// Getters & Setters
//===================================================
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public ResidentProfileDTO getHost() {
        return host;
    }

    public void setHost(ResidentProfileDTO host) {
        this.host = host;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public EventDTO(){super();}
    
   
    public EventDTO(EventEntity entityEvent) {
        this.availability = entityEvent.getAvailability();
        this.description = entityEvent.getDescription();
        this.endTime = entityEvent.getEndTime();
        this.startTime = entityEvent.getStartTime();
        this.title =  entityEvent.getTitle();
        if (entityEvent.getLocation()!= null) {
            this.location = new LocationDTO(entityEvent.getLocation());
        }
        if (entityEvent.getHost()!= null) {
            this.host = new ResidentProfileDTO(entityEvent.getHost());
        }
    }

    /**
     * Converts a event DTO to a event entity.
     *
     * @return new event entity
     *
     */
    public EventEntity toEntity() {
        EventEntity eventEntity = new EventEntity();

        eventEntity.setAvailability(getAvailability());
        eventEntity.setDescription(getDescription());
        eventEntity.setEndTime(getEndTime());
        eventEntity.setStartTime(getStartTime());
        eventEntity.setTitle(getTitle());    

         if (location != null) {
            eventEntity.setLocation(getLocation().toEntity());
        }
         
          if (host != null) {
            eventEntity.setHost(getHost().toEntity());
        }
        return eventEntity;
    }
    
    
}
