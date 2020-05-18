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
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 * This class represent a set of neighbors with common interests
 *
 * @author albayona
 */
/**
 * This class represents a post made by a resident
 */
@Entity
public class GroupEntity extends BaseEntity implements Serializable {

//===================================================
// Attributes
//===================================================
    /**
     * Represents the date group was created
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date dateCreated;

    /**
     * Represents the name of this group
     */
    private String name;

    /**
     * Represents a description of this group
     */
    private String description;

//===================================================
// Relations
//===================================================
    /**
     * Represents the residents who are members of this post.
     */
    @PodamExclude
    @ManyToMany
    private List<ResidentProfileEntity> members = new ArrayList<>();

    /**
     * The neighborhood to which the group belongs to.
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    /**
     * Represents the events attended by the group.
     */
    @PodamExclude
    @ManyToMany
    private List<EventEntity> events = new ArrayList<>();
    
    

    @PodamExclude
    @ManyToMany
    private List<FavorEntity> favors = new ArrayList<>();

    /**
     * The posts made by the group.
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "group",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    
//===================================================
// Getters & Setters
//=================================================== 

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the members
     */
    public List<ResidentProfileEntity> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<ResidentProfileEntity> members) {
        this.members = members;
    }

    /**
     * @return the neighborhood
     */
    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    /**
     * @param neighborhood the neighborhood to set
     */
    public void setNeighborhood(NeighborhoodEntity neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * @return the events
     */
    public List<EventEntity> getEvents() {
        return events;
    }

    /**
     * @param events the events to set
     */
    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }

    /**
     * @return the posts
     */
    public List<PostEntity> getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(List<PostEntity> posts) {
        this.posts = posts;
    }

    public List<FavorEntity> getFavors() {
        return favors;
    }

       public void setFavors(List<FavorEntity> favors) {
        this.favors = favors;
    }
    
   

}
