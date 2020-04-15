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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity that represents a Login.
 *
 * @author cefigueredo
 */
@Entity
public class ResidentLoginEntity extends BaseEntity implements Serializable {

    /**
     * @param resident the resident to set
     */
    public void setResident(ResidentProfileEntity resident) {
        this.resident = resident;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param governmentId the governmentId to set
     */
    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
//===================================================
// Relations
//===================================================   

    /**
     * The resident profile login.
     */
    @PodamExclude
    @OneToOne
    private ResidentProfileEntity resident;

    /**
     * Indicates the neighborhood this location belongs to
     */
    @PodamExclude
    @ManyToOne
    private NeighborhoodEntity neighborhood;

    private String userName;

    private String password;

    private String governmentId;

    private Boolean isActive;

    public ResidentProfileEntity getResident() {
        return resident;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    public Boolean isActive() {
        return isActive;
    }

    public NeighborhoodEntity getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(NeighborhoodEntity neighborhood) {
        this.neighborhood = neighborhood;
    }

}
