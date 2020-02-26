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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 *
 * @author albayona
 */

/**
 *This class represents a post made by a resident
 */
@Entity
public class PostEntity extends BaseEntity implements Serializable{
    

//===================================================
// Attributes
//===================================================
    /**
     * Represents the date post was made
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date publishDate;
    
    /**
     * Represents the title of the post
     */
    private String title;

    /**
     * Represents the main text body shown in the post
     */
    private String description;
    
    /**
     * Represents the number of numberOfLikes of the post
     */
    private Integer numberOfLikes;
    
//===================================================
// Relations
//===================================================
        
    /**
     * Represents the author of this post 
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity author;

    /**
     * Represents the business author of this post 
     */
    @PodamExclude
    @ManyToOne
    private BusinessEntity business;
    
    /**
     * Represents the users this post is visible to
     */
    @PodamExclude
    @ManyToMany
   private List<ResidentProfileEntity> viewers = new ArrayList();

    /**
     * Represents comments received in this post
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "post",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )
     
    private List<CommentEntity> comments = new ArrayList();

     
     /**
     * Represents the group this post is shared with
     */
    @PodamExclude
    @ManyToOne
   private GroupEntity group;
     
     
//===================================================
// Getters & Setters
//===================================================  

    public Date getPublishDate() {
        return publishDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public BusinessEntity getBusiness() {
        return business;
    }

    public List<ResidentProfileEntity> getViewers() {
        return viewers;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    public void setViewers(List<ResidentProfileEntity> viewers) {
        this.viewers = viewers;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    
    
    
    
    
    
}
