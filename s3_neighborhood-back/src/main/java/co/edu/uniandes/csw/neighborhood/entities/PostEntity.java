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
        cascade = CascadeType.PERSIST, orphanRemoval = true)
     
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
       /**
     * Gets publishDate.
     *
     * @return value of publishDate
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * Gets title.
     *
     * @return value of title
     */
    public String getTitle() {
        return title;
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
     * Gets numberOfLikes.
     *
     * @return value of numberOfLikes
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
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
     * Sets title.
     *
     * @param title value of title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * Sets numberOfLikes.
     *
     * @param numberOfLikes value of numberOfLikes
     */
    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    /**
     * Gets the author of this post
     * @return the author of this post
     */
    public ResidentProfileEntity getAuthor() {
        return author;
    }

    /**
     * Gets the users this post is visible to
     * @return the users this post is visible to
     */
    public List<ResidentProfileEntity> getViewers() {
        return viewers;
    }

    /**
     * Gets the comments of this post
     * @return the comments of this post
     */
    public List<CommentEntity> getComments() {
        return comments;
    }

    
    /**
     * Represents the group this post is shared with
     * @return the group this post is shared with
     */
    public GroupEntity getGroup() {
        return group;
    }
    
    
    
}
