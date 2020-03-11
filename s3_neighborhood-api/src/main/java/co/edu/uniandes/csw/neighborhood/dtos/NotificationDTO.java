/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author v.cardonac1
 */
public class NotificationDTO implements Serializable{
    
    
    private long id;

    /**
     * Represents the date group was created
     */
     @XmlJavaTypeAdapter(DateAdapter.class)
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
    private NotificationEntity.Priority priority;
    
    private ResidentProfileDTO author;
    private BusinessDTO business;
    
    public NotificationDTO(){
        super();
    }
    
    public NotificationDTO(NotificationEntity entityNotification) {
        this.author = new ResidentProfileDTO(entityNotification.getAuthor());
        this.business = new BusinessDTO(entityNotification.getBusiness());
        this.description = entityNotification.getDescription();
        this.header = entityNotification.getHeader();
        this.id = entityNotification.getId();
        this.priority = entityNotification.getPriority();
        this.publishDate = entityNotification.getPublishDate();
        this.seen = entityNotification.isSeen();
    }

    public NotificationEntity toEntity() {
        NotificationEntity notificationEntity = new NotificationEntity();
        
        if(author!=null) notificationEntity.setAuthor(getAuthor().toEntity());
        if(business!=null) notificationEntity.setBusiness(getBusiness().toEntity());
        notificationEntity.setDescription(getDescription());
        notificationEntity.setHeader(getHeader());
        notificationEntity.setId(getId());
        notificationEntity.setPublishDate(getPublishDate());
        notificationEntity.setSeen(isSeen());
        notificationEntity.setPriority(getPriority());
        return notificationEntity;
    }
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the publishDate
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * @param publishDate the publishDate to set
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
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
     * @return the seen
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * @return the priority
     */
    public NotificationEntity.Priority getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(NotificationEntity.Priority priority) {
        this.priority = priority;
    }

    /**
     * @return the author
     */
    public ResidentProfileDTO getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(ResidentProfileDTO author) {
        this.author = author;
    }

    /**
     * @return the business
     */
    public BusinessDTO getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(BusinessDTO business) {
        this.business = business;
    }
    
}
