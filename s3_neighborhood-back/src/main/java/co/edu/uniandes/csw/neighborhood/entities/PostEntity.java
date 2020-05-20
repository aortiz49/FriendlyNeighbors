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
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
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
 * This class represents a post made by a resident
 */
@Entity
public class PostEntity extends BaseEntity implements Serializable {

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
    @ElementCollection
    @CollectionTable(name = "postAlbum")
    private List<String> album;

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
     * @return the numberOfLikes
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * @param numberOfLikes the numberOfLikes to set
     */
    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    /**
     * @return the author
     */
    public ResidentProfileEntity getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    /**
     * @return the business
     */
    public BusinessEntity getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    /**
     * @return the viewers
     */
    public List<ResidentProfileEntity> getViewers() {
        return viewers;
    }

    /**
     * @param viewers the viewers to set
     */
    public void setViewers(List<ResidentProfileEntity> viewers) {
        this.viewers = viewers;
    }

    /**
     * @return the comments
     */
    public List<CommentEntity> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    /**
     * @return the group
     */
    public GroupEntity getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }

    
    
}
