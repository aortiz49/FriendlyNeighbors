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
 *
 * @author albayona
 */

@Entity
public class CommentEntity extends BaseEntity implements Serializable{
    
    
    /**
     * Represents the date this comment was posted
     */
   @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date date;

    /**
     * Represents the  text body shown in this post
     */
    private String text;
   
    /**
    * Represents the author of this comment
    */
    
    @PodamExclude
   @ManyToOne
   ResidentProfileEntity author;

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

  
   
}
