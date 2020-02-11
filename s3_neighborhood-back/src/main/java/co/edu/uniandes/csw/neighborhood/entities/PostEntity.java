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
    

    
    /**
     * Represents the date post was made
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date datePosted;
    
    /**
     * Represents the title of the post
     */
    private String title;

    /**
     * Represents the main text body shown in the post
     */
    private String description;
    
    /**
     * Represents the number of likes of the post
     */
    private Integer likes;
    
    /**
     *Represents the author of this post 
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity author;

    /**
     * Represents comments received in this post
     */
     @PodamExclude
    @OneToMany(
        fetch = javax.persistence.FetchType.LAZY,
         cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<CommentEntity> comments = new ArrayList();

     
    public Date getDatePosted() {
        return datePosted;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLikes() {
        return likes;
    }

    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

   
    
}
