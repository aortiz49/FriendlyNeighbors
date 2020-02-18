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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity representing a business owner.
 *
 * @author aortiz49
 */
@Entity
public class BusinessOwnerEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//=================================================== 

    /**
     * The businesses owned by a business owner.
     */
    @PodamExclude
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BusinessEntity> businesses = new ArrayList<>();

    /**
     * The business owner's login.
     */
    @PodamExclude
    @OneToOne(mappedBy = "businessOwner", fetch = FetchType.LAZY)
    private BusinessOwnerLoginEntity login;

    /**
     * The business owner's dashboard.
     */
    @PodamExclude
    @OneToOne
    private DashboardEntity dashboard;

//===================================================
// Attributes
//===================================================

  
    public DashboardEntity getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardEntity dashboard) {
        this.dashboard = dashboard;
    }
    
    
    
    /**
     * The owner's email address.
     */
    private String email;

    /**
     * The owner's phone number.
     */
    private String phoneNumber;

    /**
     * The owner's name.
     */
    private String name;

    /**
     * The owner's address.
     */
    private String address;

    /**
     * The owner's profile preferences.
     */
    private String preferences;
    
    /**
     * The owner's nickname.
     */
    private String nickname;

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the list of businesses owned by the owner.
     *
     * @return the list of owned businesses
     */
    public List<BusinessEntity> getBusinesses() {
        return businesses;
    }

    /**
     * Sets the list of businesses owned by the owner.
     *
     * @param pBusinesses the new list of owdned businesses
     */
    public void setBusinesses(List<BusinessEntity> pBusinesses) {
        businesses = pBusinesses;
    }

    /**
     * Returns the business owner's login.
     *
     * @return business owner's login
     */
    public BusinessOwnerLoginEntity getLogin() {
        return login;
    }

    /**
     * Sets the business owner's login.
     *
     * @param pLogin the new business owner's login
     */
    public void setLogin(BusinessOwnerLoginEntity pLogin) {
        login = pLogin;
    }

    /**
     * Returns the owner's email address.
     *
     * @return owner's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the owner's email address.
     *
     * @param pEmail the new email address
     */
    public void setEmail(String pEmail) {
        email = pEmail;
    }

    /**
     * Returns the owner's phone number.
     *
     * @return the owner's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the owner's phone number.
     *
     * @param pPhoneNumber the new phone number
     */
    public void setPhoneNumber(String pPhoneNumber) {
        phoneNumber = pPhoneNumber;
    }

    /**
     * Returns the owner's name.
     *
     * @return the owner's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the owner's name.
     *
     * @param pName the new owner's name
     */
    public void setName(String pName) {
        name = pName;
    }
    
    
}
