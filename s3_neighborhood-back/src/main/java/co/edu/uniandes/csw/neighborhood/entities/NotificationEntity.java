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
public class NotificationEntity extends BaseEntity implements Serializable{

    enum CommentPriority {
    LOW,
    MEDIUM,
    HIGH
    }
    
    /**
     * Represents the date notification was made
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date datePosted;
    
    private String title;
    
    private String description;
            
    private boolean isSeen;
    
    private CommentPriority priority;
    
        @PodamExclude
          @ManyToOne
       private ResidentProfileEntity author;

    
    /**
     * @return the datePosted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * @param datePosted the datePosted to set
     */
    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the isSeen
     */
    public boolean isIsSeen() {
        return isSeen;
    }

    /**
     * @param isSeen the isSeen to set
     */
    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    /**
     * @return the priority
     */
    public CommentPriority getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(CommentPriority priority) {
        this.priority = priority;
    }
    
    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }
       
    
}