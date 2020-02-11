/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author albayona
 */

@Entity
public class ResidentProfileEntity extends BaseEntity implements Serializable{
    
    /**
     * Represents the name of this resident 
     */
    private String name;
    
        
    /**
     * Represents phone number of this resident 
     */
    private String phoneNumber;
    
     /**
     * Represents email of this resident 
     */
    private String email;
    
    /**
     * Represents nickname of this resident 
     */
    private String nickname;
    
    /**
     * Represents preferences of this resident 
     */
    private String preferences;
    
    /**
     * Represents a link to a proof of residence file of this resident 
     */
    private String proofOfResidence;
    
    /**
     * Represents pictures uploaded by this resident 
     */
    private String[] pictureLinks;
    
    
    /**
     * Represents favors requested by this resident 
     */
        @PodamExclude
    @OneToMany(
        mappedBy = "author",
        fetch = javax.persistence.FetchType.LAZY,
         cascade = CascadeType.PERSIST, orphanRemoval = true
    )
            
    private List<FavorEntity> favors =  new ArrayList<>();
    
    
    /**
     * Represents services offered by this resident 
     */
        @PodamExclude
        @OneToMany(
        mappedBy = "author",
        fetch = javax.persistence.FetchType.LAZY,
         cascade = CascadeType.PERSIST, orphanRemoval = true
    )
   private List<ServiceEntity> services  = new ArrayList<>();;
    
        
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
    @OneToMany(mappedBy = "author",fetch=FetchType.LAZY,
         cascade = CascadeType.PERSIST, orphanRemoval = true)
  private  List<PostEntity> posts = new ArrayList<>();
     
     
    /**
     * Represents events posted by this resident 
     */
    @PodamExclude
    @OneToMany(
        mappedBy = "author",
        fetch = javax.persistence.FetchType.LAZY,
         cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<EventEntity> events  = new ArrayList<>();
    
    
         /**
     * Represents events posted by this resident 
     */
        @PodamExclude
        @OneToOne(mappedBy = "residentProfile", fetch=FetchType.EAGER)
    private LoginEntity login;

        
        
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPreferences() {
        return preferences;
    }

    public String getProofOfResidence() {
        return proofOfResidence;
    }

    public String[] getPictureLinks() {
        return pictureLinks;
    }

    public List<FavorEntity> getFavors() {
        return favors;
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

    public LoginEntity getLogin() {
        return login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void setProofOfResidence(String proofOfResidence) {
        this.proofOfResidence = proofOfResidence;
    }

    public void setPictureLinks(String[] pictureLinks) {
        this.pictureLinks = pictureLinks;
    }

    public void setFavors(List<FavorEntity> favors) {
        this.favors = favors;
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

    public void setLogin(LoginEntity login) {
        this.login = login;
    }

        
        

}  