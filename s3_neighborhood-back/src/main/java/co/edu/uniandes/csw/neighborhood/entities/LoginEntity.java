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
    private BusinessOwnerProfileEntity businessOwner;
    
    @PodamExclude
    @OneToOne
    private ResidentProfileEntity resident;
    

    public BusinessOwnerProfileEntity getBusinessOwner() {
        return businessOwner;
    }

    public ResidentProfileEntity getResident() {
        return resident;
    }

    public void setBusinessOwner(BusinessOwnerProfileEntity businessOwner) {
        this.businessOwner = businessOwner;
    }

    public void setResident(ResidentProfileEntity resident) {
        this.resident = resident;
    }

        
        
        

}
