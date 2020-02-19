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

    /**
     * Represents the residents who are members of this post
     */
    @PodamExclude
    @ManyToMany
    private List<ResidentProfileEntity> members = new ArrayList();

    /**
     * The neighborhood to which the group belongs to.
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;
    
     /**
     * The posts made by the group.
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "group",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();
    
    /**
     * Represents events organized by the group.
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "group",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<EventEntity> events = new ArrayList();

    
    public Date getDateCreated() {
        return dateCreated;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ResidentProfileEntity> getMembers() {
        return members;
    }

    public List<PostEntity> getPosts() {
        return posts;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMembers(List<ResidentProfileEntity> members) {
        this.members = members;
    }

    public void setPosts(List<PostEntity> posts) {
        this.posts = posts;
    }

}
