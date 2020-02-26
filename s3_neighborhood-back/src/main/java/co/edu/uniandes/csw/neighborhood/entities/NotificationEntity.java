/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 *
 * @author v.cardonac1
 */
@Entity
public class NotificationEntity extends BaseEntity implements Serializable {

//===================================================
// Enumerations
//===================================================
    enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

//===================================================
// Attributes
//===================================================
    
    /**
     * Represents the date notification was made
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date publishDate;

    /**
     * Represents the header of this notification
     */
    private String header;

    /**
     * Represents the description of this notification
     */
    private String description;

    /**
     * Indicates if this notification has been seen by at least one user
     */
    private boolean seen;

    /**
     * Indicates the  priority of this  notification
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;


//===================================================
// Relations
//===================================================
    
    /**
     * Indicates the  sender of this  notification
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity author;

    @PodamExclude
    @ManyToOne
    private BusinessEntity business;
//===================================================
// Getters & Setters
//=================================================== 
    /**
     * @return the datePosted
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * Gets header.
     *
     * @return value of header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Gets description.
     *
     * @return value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets isSeen.
     *
     * @return value of isSeen
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * Gets priority.
     *
     * @return value of priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets publishDate.
     *
     * @param publishDate value of publishDate
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Sets header.
     *
     * @param header value of header
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Sets description.
     *
     * @param description value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets isSeen.
     *
     * @param seen value of isSeen
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * Sets priority.
     *
     * @param priority value of priority
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Gets the  sender of this  notification
     * @return the  sender of this  notification
     */
    public ResidentProfileEntity getUserProfile() {
        return author;
    }

    /**
     * Sets the  sender of this  notification
     * @param userProfile the  sender of this  notification
     */
     
    public void setUserProfile(ResidentProfileEntity userProfile) {
        this.author = userProfile;
    }

    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    public BusinessEntity getBusiness() {
        return business;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }
    
    
    
    
}
