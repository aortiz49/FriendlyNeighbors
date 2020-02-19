/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.stream.events.Comment;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * This class represent a resident in a neighborhood
 *
 * @author albayona
 */
@Entity
public class ResidentProfileEntity extends BaseEntity implements Serializable {

//===================================================
// Attributes
//===================================================  
    /**
     * Represents phone number of this resident
     */
    private String phoneNumber;

    /**
     * Represents email of this resident
     */
    private String email;

    /**
     * Represents the name of this resident
     */
    private String name;

    /**
     * Represents nickname of this resident
     */
    private String nickname;

    /**
     * The resident's address.
     */
    private String address;

    /**
     * Represents preferences of this resident
     */
    private String preferences;


    /**
     * Represents a link to a proof of residence file of this resident
     */
    private String proofOfResidence;


//===================================================
// Relations
//===================================================
    
    /**
     * Events a resident is signed up for.
     */
    @PodamExclude
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.EAGER)
    private List<EventEntity> eventsToAttend = new ArrayList();
    
    /**
     * Favors a resident is signed up to complete.
     */
    @ManyToMany(mappedBy = "candidates", fetch = FetchType.EAGER)
    private List<FavorEntity> favorsToHelp = new ArrayList() ;

   
    /**
     * Represents favors requested by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true
    )

    private List<FavorEntity> favorsRequested = new ArrayList<>();

    /**
     * Represents services offered by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<ServiceEntity> services = new ArrayList<>();

    /**
     * Represents notifications posted by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<NotificationEntity> notifications = new ArrayList<>();

    /**
     * Represents posts made by this resident
     */
    @PodamExclude
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    /**
     * Represents events posted by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "host",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<EventEntity> events = new ArrayList<>();

    /**
     * Represents events posted by this resident
     */
    @PodamExclude
    @OneToOne(mappedBy = "resident", fetch = FetchType.EAGER)
    private ResidentLoginEntity login;

    /**
     * Represents groups this resident is part of
     */
    @PodamExclude
    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    private List<GroupEntity> groups = new ArrayList<>();
    
      /**
     * Represents comments posted by this resident
     */
    @PodamExclude
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private List<CommentEntity> comments = new ArrayList<>();
    

    /**
     * Represents the neighborhood of this resident
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    public String getPreferences() {
        return preferences;
    }

    public String getProofOfResidence() {
        return proofOfResidence;
    }

    public List<EventEntity> getEventsToAttend() {
        return eventsToAttend;
    }

    public List<FavorEntity> getFavorsToHelp() {
        return favorsToHelp;
    }

    public List<FavorEntity> getFavorsRequested() {
        return favorsRequested;
    }

    public List<ServiceEntity> getServices() {
        return services;
    }

    public List<NotificationEntity> getNotifications() {
        return notifications;
    }

    public List<PostEntity> getPosts() {
        return posts;
    }

    public List<EventEntity> getEvents() {
        return events;
    }

    public ResidentLoginEntity getLogin() {
        return login;
    }

    public List<GroupEntity> getGroups() {
        return groups;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void setProofOfResidence(String proofOfResidence) {
        this.proofOfResidence = proofOfResidence;
    }

    public void setEventsToAttend(List<EventEntity> eventsToAttend) {
        this.eventsToAttend = eventsToAttend;
    }

    public void setFavorsToHelp(List<FavorEntity> favorsToHelp) {
        this.favorsToHelp = favorsToHelp;
    }

    public void setFavorsRequested(List<FavorEntity> favorsRequested) {
        this.favorsRequested = favorsRequested;
    }

    public void setServices(List<ServiceEntity> services) {
        this.services = services;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
    }

    public void setPosts(List<PostEntity> posts) {
        this.posts = posts;
    }

    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }

    public void setLogin(ResidentLoginEntity login) {
        this.login = login;
    }

    public void setGroups(List<GroupEntity> groups) {
        this.groups = groups;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public void setNeighborhood(NeighborhoodEntity neighborhood) {
        this.neighborhood = neighborhood;
    }

   
}
