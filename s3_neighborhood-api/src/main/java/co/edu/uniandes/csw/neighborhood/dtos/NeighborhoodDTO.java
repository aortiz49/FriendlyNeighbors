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

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data transfer object for Neighborhood. This DTO contains the JSON representation of a
 * Neighborhood that gets transferred between the client and server.
 *
 * @author aortiz49
 */
public class NeighborhoodDTO implements Serializable {
//===================================================
// Attributes
//===================================================

    /**
     * The name of the neighborhood.
     */
    private String name;

    /**
     * The name of the locality.
     */
    private String locality;

    /**
     * The max number of residents in the neighborhood.
     */
    private int numberOfResidents;

//===================================================
// Constructor
//===================================================
    /**
     * Empty Neighborhood constructor.
     */
    public NeighborhoodDTO() {
    }

    /**
     * Creates a NeighborhoodDTO object from a NeighborhoodEntity object.
     *
     * @param pNeighborhood neighborhood entity from which to create the new NeighborhoodDTO
     *
     */
    public NeighborhoodDTO(NeighborhoodEntity pNeighborhood) {
        if (pNeighborhood != null) {
            this.name = pNeighborhood.getName();
            this.locality = pNeighborhood.getLocality();
            this.numberOfResidents = pNeighborhood.getNumberOfResidents();
        }
    }
//===================================================
// Conversion
//===================================================

    /**
     * Converts a NeighborhoodDTO object into an AuthorEntity.
     *
     * @return the new NeighborhoodEntity object
     *
     */
    public NeighborhoodEntity toEntity() {
        NeighborhoodEntity neighborhood = new NeighborhoodEntity();
        neighborhood.setName(this.getName());
        neighborhood.setLocality(this.getLocality());
        neighborhood.setNumberOfResidents(this.getNumberOfResidents());

        return neighborhood;
    }
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
     * Returns the name of the locality the neighborhood is in.
     *
     * @return the locality name
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Sets the name of the locality the neighborhood is in.
     *
     * @param pMunicipality the new locality
     */
    public void setLocality(String pMunicipality) {
        locality = pMunicipality;
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

    /**
     * Returns the string representation of the Neighborhood object.
     *
     * @return the object string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
