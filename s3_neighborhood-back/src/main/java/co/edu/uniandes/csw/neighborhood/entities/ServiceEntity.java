/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 * Entity that represents a service
 *
 * @author aortiz49
 */
@Entity
public class ServiceEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//=================================================== 

    /**
     * The resident who published the service offer.
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity resident;

//===================================================
// Attributes
//===================================================
    /**
     * The date on which the service offer was posted.
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date postDate;

    /**
     * The title of the service offered.
     */
    private String title;

    /**
     * The description of the service offered.
     */
    private String description;

    /**
     * The name of the author of the service offer.
     */
    private String author;

    /**
     * The availability of the service offer.
     */
    private boolean isAvailable;

//===================================================
// Getters & Setters
//===================================================
    
    /**
     * Returns the resident who authored the service offer.
     * 
     * @return resident who created service offer
     */
    public ResidentProfileEntity getResident() {
        return resident;
    }

    /**
     * Sets the resident who authored the service offer.
     * 
     * @param pResident the new resident author of the service offer 
     */
    public void setResident(ResidentProfileEntity pResident) {
        resident = pResident;
    }
    
    /**
     * Returns the date on which the service offer was posted.
     *
     * @return the date on which the service was posted
     */
    public Date getPostDate() {
        return postDate;
    }

    /**
     * Sets the date on which the service offer was posted.
     *
     * @param pPostDate new date on which the service offer was posted
     */
    public void setPostDate(Date pPostDate) {
        postDate = pPostDate;
    }

    /**
     * Returns the title of the service offer.
     *
     * @return the service offer title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the service offer.
     *
     * @param pTitle new service offer title
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    /**
     * Returns the description of the service offer.
     *
     * @return the service offer descripion
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the service offer.
     *
     * @param pDescription the new service offer descriotion
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    /**
     * Returns the author of the service offer.
     *
     * @return the author who wrote the service offer
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the service offer
     *
     * @param pAuthor the new author of the service offer
     */
    public void setAuthor(String pAuthor) {
        author = pAuthor;
    }

    /**
     * Returns the availability status of the service offer.
     *
     * @return true if the service is avaiable at the time of consultion
     *
     */
    public boolean isIsAvailable() {
        return isAvailable;
    }

    /**
     * Sets the availability status of the service offer.
     *
     * @param pIsAvailable the new availability status
     */
    public void setIsAvailable(boolean pIsAvailable) {
        isAvailable = pIsAvailable;
    }

}
