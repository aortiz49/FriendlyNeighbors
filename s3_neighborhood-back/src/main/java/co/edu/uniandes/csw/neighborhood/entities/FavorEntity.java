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
 *
 * @author v.cardonac1
 */
@Entity
public class FavorEntity extends BaseEntity implements Serializable {

    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity author;
    
     /**
     * The residents who complete the favor.
     */
    @PodamExclude
    @ManyToMany
    private List<ResidentProfileEntity> candidates = new ArrayList<>();


    /**
     * Represents the date favor was made
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date datePosted;

    private String title;

    private String description;

    private String type;
    
    private Boolean isHelpWanted;

      
    
    /**
     * @return the datePosted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * @param datePosted the datePosted to set
     */
    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

  
    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    public List<ResidentProfileEntity> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ResidentProfileEntity> candidates) {
        this.candidates = candidates;
    }

    public Boolean getIsHelpWanted() {
        return isHelpWanted;
    }

    public void setIsHelpWanted(Boolean isHelpWanted) {
        this.isHelpWanted = isHelpWanted;
    }
    

}
