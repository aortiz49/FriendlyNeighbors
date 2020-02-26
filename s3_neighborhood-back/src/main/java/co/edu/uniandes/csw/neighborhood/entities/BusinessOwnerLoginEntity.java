/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.neighborhood.entities;
//===================================================
// Imports
//===================================================

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity that represents a Login.
 *
 * @author aortiz49
 */
@Entity
public class BusinessOwnerLoginEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//===================================================   

    /**
     * The business owner login.
     */
    @PodamExclude
    @OneToOne
    private BusinessOwnerEntity businessOwner;

//===================================================
// Relations
//===================================================   

    /**
     * The business owner's user name
     */
    private String userName;

    /**
     * The business owner's password
     */
    private String password;
    
    /**
     * Indicates if the business owner's account is active or not
     */
    private Boolean isActive;
    
    /**
     * The business owner's identification number
     */
    private String governmentId;

//===================================================
// Getters & Setters
//===================================================   

    /**
     * Returns the business owner. 
     * 
     * @return the business owner. 
     */
    public BusinessOwnerEntity getBusinessOwner() {
        return businessOwner;
    }


    /**
     * Sets the new business owner. 
     * 
     * @param pBusinessOwner the new business owner
     */
    public void setBusinessOwner(BusinessOwnerEntity pBusinessOwner) {
        businessOwner = pBusinessOwner;
    }


    /**
     * Returns the business owner's user name
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the business owner's new user name
     * 
     * @param pUserName the new user name
     */
    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    /**
     * Returns the business owner's password
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the business owner's new password
     * 
     * @param pPassword the new passsword
     */
    public void setPassword(String pPassword) {
        password = pPassword;
    }

    /**
     * Checks if the business owner's account is active.
     * 
     * @return True if the account is active
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets the business owner's new account status  
     * 
     * @param pIsActive sets the account status
     */
    public void setIsActive(Boolean pIsActive) {
        isActive = pIsActive;
    }

    /**
     * Returns the business owner's government id.
     * 
     * @return the government id
     */
    public String getGovernmentId() {
        return governmentId;
    }

    /**
     * Sets the business owner's new government id
     * 
     * @param pGovernmentId the new government id
     */
    public void setGovernmentId(String pGovernmentId) {
        governmentId = pGovernmentId;
    }
    
    
}
