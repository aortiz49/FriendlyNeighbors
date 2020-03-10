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
package co.edu.uniandes.csw.neighborhood.dtos;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data transfer object for ResidentLogins. This DTO contains the JSON representation of a
 * ResidentLogin that gets transferred between the client and server.
 *
 * @author aortiz49
 */
public class ResidentLoginDTO implements Serializable {
//===================================================
// Dependencies
//===================================================

    /**
     * The resident who owns the login
     */
    private ResidentProfileDTO resident;

//===================================================
// Attributes
//===================================================
    /**
     * The resident's user name.
     */
    private String userName;

    /**
     * The resident's password.
     */
    private String password;

    /**
     * The resident's government Id.
     */
    private String governmentId;

    /**
     * Indicates if a resident is active or not.
     */
    private boolean isActive;

//===================================================
// Constructors
//===================================================
    /**
     * Empty ResidentLogin constructor.
     */
    public ResidentLoginDTO() {
    }

    /**
     * Creates a ResidentLoginDTO object from a ResidentLoginEntity object.
     *
     * @param pResidentLogin resident login entity from which to create the new ResidentLoginDTO
     *
     */
    public ResidentLoginDTO(ResidentLoginEntity pResidentLogin) {
        if (pResidentLogin != null) {
            this.userName = pResidentLogin.getPassword();
            this.password = pResidentLogin.getPassword();
            this.governmentId = pResidentLogin.getGovernmentId();
            this.isActive = pResidentLogin.isActive();

            this.resident = new ResidentProfileDTO(pResidentLogin.getResident());

        }
    }
//===================================================
// Conversion
//===================================================

    /**
     * Converts a ResidentLoginDTO object into a ResidentLoginEntity.
     *
     * @return the new ResidentLoginEntity object
     *
     */
    public ResidentLoginEntity toEntity() {
        ResidentLoginEntity login = new ResidentLoginEntity();
        login.setUserName(this.getUserName());
        login.setPassword(this.getPassword());
        login.setGovernmentId(this.getGovernmentId());
        login.setIsActive(this.isActive());

        if (resident != null) {
            login.setResident(getResident().toEntity());
        }

        return login;
    }
//===================================================
// Getters & Setters
//===================================================

    /**
     * Returns the resident associated to the login.
     * 
     * @return the resident
     */
    public ResidentProfileDTO getResident() {
        return resident;
    }

    /**
     * Sets the resident associated to the login.
     * 
     * @param pResident the new resident 
     */
    public void setResident(ResidentProfileDTO pResident) {
        resident = pResident;
    }

    /**
     * Returns the resident's user name.
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the resident's user name.
     * 
     * @param pUserName the new user name
     */
    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    /**
     * Returns the resident's password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the resident's password.
     * 
     * @param pPassword the new password
     */
    public void setPassword(String pPassword) {
        password = pPassword;
    }

    /**
     * Returns the resident's government Id.
     * 
     * @return the government Id
     */
    public String getGovernmentId() {
        return governmentId;
    }

    /**
     * Sets the resident's government Id.
     * 
     * @param pGovernmentId the new government Id 
     */
    public void setGovernmentId(String pGovernmentId) {
        governmentId = pGovernmentId;
    }

    /**
     * Checks if the resident has an active account or not.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active status of the resident.
     * 
     * @param pIsActive the new status
     */
    public void setIsActive(boolean pIsActive) {
        isActive = pIsActive;
    }

    /**
     * Returns the string representation of the Business object.
     *
     * @return the object string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
