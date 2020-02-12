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

/**
 * Entity that represents a neighborhood.
 *
 * @author aortiz49
 */
public class NeighborhoodEntity extends BaseEntity implements Serializable {
//===================================================
// Relations
//===================================================

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
