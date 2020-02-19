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
import javax.persistence.OneToMany;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entity that represents a neighborhood.
 *
 * @author aortiz49
 */
@Entity
public class NeighborhoodEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//===================================================

    /**
     * The businesses in the neighborhood.
     */
    @PodamExclude
    @OneToMany(mappedBy = "neighborhood", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BusinessEntity> businesses = new ArrayList<>(); 
    
    /**
     * The residents in the neighborhood.
     */
    @PodamExclude
    @OneToMany(mappedBy = "neighborhood", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ResidentProfileEntity> residents = new ArrayList<>();
     
    /**
     * The places in the neighborhood.
     */
    @PodamExclude
    @OneToMany(mappedBy = "neighborhood", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<LocationEntity> places = new ArrayList<>();
    
     /**
     * The groups in the neighborhood.
     */
    @PodamExclude
    @OneToMany(mappedBy = "neighborhood", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<GroupEntity> groups = new ArrayList<>();
    
//===================================================
// Attributes
//===================================================  
    /**
     * The name of the neighborhood.
     */
    private String name;

    /**
     * The name of the municipality.
     */
    private String municipality;

    /**
     * The number of residents in the neighborhood.
     */
    private int numberOfResidents;

//===================================================
// Getters & Setters
//===================================================      

    /**
     * Returns the list of businesses in the neighborhood.
     * 
     * @return the list of businesses in the neighborhood 
     */
    public List<BusinessEntity> getBusinesses() {
        return businesses;
    }

    /**
     * Sets the list of businesses in the neighborhood.
     * 
     * @param pBusinesses the new list of businesses
     */
    public void setBusinesses(List<BusinessEntity> pBusinesses) {
        businesses = pBusinesses;
    }

    /**
     * Returns a list of residents in the neighborhood.
     * 
     * @return the list of residents in the neighborhood
     */
    public List<ResidentProfileEntity> getResidents() {
        return residents;
    }

    /**
     * Sets the list of residents in the neighborhood.
     * 
     * @param pResidents the new list of residents
     */
    public void setResidents(List<ResidentProfileEntity> pResidents) {
        residents = pResidents;
    }

    /**
     * Returns the list of places of interest in the neighborhood.
     * 
     * @return the list of places of interest
     */
    public List<LocationEntity> getPlaces() {
        return places;
    }

    /**
     * Sets the list of places of interest int he neighborhood
     * 
     * @param pPlaces the new list of places of interest 
     */
    public void setPlaces(List<LocationEntity> pPlaces) {
        places = pPlaces;
    }
       
    /**
     * Returns the name of the neighborhood.
     *
     * @return the name of the neighborhood
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the neighborhood.
     *
     * @param pName the new name of the neighborhood
     */
    public void setName(String pName) {
        name = pName;
    }

    /**
     * Returns the name of the municipality the neighborhood is in.
     *
     * @return
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * Sets the name of the municipality the neighborhood is in.
     *
     * @param pMunicipality the new municipality
     */
    public void setMunicipality(String pMunicipality) {
        municipality = pMunicipality;
    }

    /**
     * Returns the number of residents living the neighborhood.
     *
     * @return the number of residents
     */
    public int getNumberOfResidents() {
        return numberOfResidents;
    }

    /**
     * Sets the number of residents living in the neighborhood.
     *
     * @param pNumberOfResidents the new number of residents
     */
    public void setNumberOfResidents(int pNumberOfResidents) {
        numberOfResidents = pNumberOfResidents;
    }

}
