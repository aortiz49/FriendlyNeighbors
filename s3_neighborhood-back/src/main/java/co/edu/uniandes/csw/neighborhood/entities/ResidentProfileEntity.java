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
    @ManyToMany(mappedBy = "attendees")
    private List<EventEntity> eventsToAttend = new ArrayList<>();

    /**
     * Favors a resident is signed up to complete.
     */
    @PodamExclude
    @ManyToMany(mappedBy = "candidates", fetch = FetchType.EAGER)
    private List<FavorEntity> favorsToHelp = new ArrayList();

    /**
     * Posts shared with this resident.
     */
    @PodamExclude
    @ManyToMany(mappedBy = "viewers", fetch = FetchType.EAGER)
    private List<PostEntity> postsToView = new ArrayList();

    /**
     * Represents favors requested by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )

    private List<FavorEntity> favorsRequested = new ArrayList<>();

    /**
     * Represents services offered by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<ServiceEntity> services = new ArrayList<>();

    /**
     * Represents notifications posted by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "author",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<NotificationEntity> notifications = new ArrayList<>();

    /**
     * Represents posts made by this resident
     */
    @PodamExclude
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    /**
     * Represents events posted by this resident
     */
    @PodamExclude
    @OneToMany(
            mappedBy = "host",
            fetch = javax.persistence.FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
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
     * Represents businesses owned by this resident
     */
    @PodamExclude
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<BusinessEntity> businesses = new ArrayList<>();

    /**
     * Represents the neighborhood of this resident
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the preferences
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * @param preferences the preferences to set
     */
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    /**
     * @return the proofOfResidence
     */
    public String getProofOfResidence() {
        return proofOfResidence;
    }

    /**
     * @param proofOfResidence the proofOfResidence to set
     */
    public void setProofOfResidence(String proofOfResidence) {
        this.proofOfResidence = proofOfResidence;
    }

    /**
     * @return the eventsToAttend
     */
    public List<EventEntity> getEventsToAttend() {
        return eventsToAttend;
    }

    /**
     * @param eventsToAttend the eventsToAttend to set
     */
    public void setEventsToAttend(List<EventEntity> eventsToAttend) {
        this.eventsToAttend = eventsToAttend;
    }

    /**
     * @return the favorsToHelp
     */
    public List<FavorEntity> getFavorsToHelp() {
        return favorsToHelp;
    }

    /**
     * @param favorsToHelp the favorsToHelp to set
     */
    public void setFavorsToHelp(List<FavorEntity> favorsToHelp) {
        this.favorsToHelp = favorsToHelp;
    }

    /**
     * @return the postsToView
     */
    public List<PostEntity> getPostsToView() {
        return postsToView;
    }

    /**
     * @param postsToView the postsToView to set
     */
    public void setPostsToView(List<PostEntity> postsToView) {
        this.postsToView = postsToView;
    }

    /**
     * @return the favorsRequested
     */
    public List<FavorEntity> getFavorsRequested() {
        return favorsRequested;
    }

    /**
     * @param favorsRequested the favorsRequested to set
     */
    public void setFavorsRequested(List<FavorEntity> favorsRequested) {
        this.favorsRequested = favorsRequested;
    }

    /**
     * @return the services
     */
    public List<ServiceEntity> getServices() {
        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices(List<ServiceEntity> services) {
        this.services = services;
    }

    /**
     * @return the notifications
     */
    public List<NotificationEntity> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
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
     * @return the login
     */
    public ResidentLoginEntity getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(ResidentLoginEntity login) {
        this.login = login;
    }

    /**
     * @return the groups
     */
    public List<GroupEntity> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<GroupEntity> groups) {
        this.groups = groups;
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
     * @return the businesses
     */
    public List<BusinessEntity> getBusinesses() {
        return businesses;
    }

    /**
     * @param businesses the businesses to set
     */
    public void setBusinesses(List<BusinessEntity> businesses) {
        this.businesses = businesses;
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

}
