/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;


@Entity
public class LoginEntity  extends BaseEntity implements Serializable{
    
        @PodamExclude
           @OneToOne
       ResidentProfileEntity residentProfile;

    public ResidentProfileEntity getAuthor() {
        return residentProfile;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.residentProfile = author;
    }
       
}
