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
import javax.persistence.OneToMany;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author estudiante
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

}

