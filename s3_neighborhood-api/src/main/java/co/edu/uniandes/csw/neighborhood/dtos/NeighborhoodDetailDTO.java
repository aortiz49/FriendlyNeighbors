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
// Relations
//===================================================
import co.edu.uniandes.csw.neighborhood.entities.*;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class that extends {@link NeighborhoodDTO} to manage the relations between NeighborhoodDTO and
 * other DTOs.
 *
 * To know more about the content of a Neighborhood, visit the {@link NeighborhoodDTO}
 * documentation.
 *
 * @author aortiz49
 */
public class NeighborhoodDetailDTO extends NeighborhoodDTO implements Serializable {

//===================================================
// Relations
//===================================================  
    /**
     * The businesses in the neighborhood.
     */
    private List<BusinessDTO> businesses;

    /**
     * The residents in the neighborhood.
     */
    private List<ResidentProfileDTO> residents;

    /**
     * The places in the neighborhood.
     */
    private List<LocationDTO> places;

    /**
     * The groups in the neighborhood.
     */
    private List<GroupDTO> groups;

    /**
     * The logins in the neighborhood;
     */
    private List<ResidentLoginDTO> logins;

//===================================================
// Constructors
//===================================================
    /**
     * Constructs a new NeighborhoodDTO.
     */
    public NeighborhoodDetailDTO() {
        super();
    }

    /**
     * Creates a detailed neighborhood DTO from neighborhood entity, including its relations.
     *
     * @param pNeighborhood from which new group DTO will be created
     *
     */
    public NeighborhoodDetailDTO(NeighborhoodEntity pNeighborhood) {

        super(pNeighborhood);

        if (pNeighborhood != null) {

            // instantiate lists
            businesses = new ArrayList<>();
            residents = new ArrayList<>();
            places = new ArrayList<>();
            groups = new ArrayList<>();
            logins = new ArrayList<>();

            // adds all businesses to the neighborhood dto
            for (BusinessEntity businessEntity : pNeighborhood.getBusinesses()) {
                businesses.add(new BusinessDTO(businessEntity));
            }

            // adds all residents to the neighborhood dto
            for (ResidentProfileEntity residentEntity : pNeighborhood.getResidents()) {
                residents.add(new ResidentProfileDTO(residentEntity));
            }

            // adds all placs to the neighborhood dto
            for (LocationEntity locationEntity : pNeighborhood.getPlaces()) {
                places.add(new LocationDTO(locationEntity));
            }

            // adds all groups to the neighborhood dto
            for (GroupEntity groupEntity : pNeighborhood.getGroups()) {
                groups.add(new GroupDTO(groupEntity));
            }

            // adds all logins to the neighborhood dto
            for (ResidentLoginEntity loginEntity : pNeighborhood.getLogins()) {
                logins.add(new ResidentLoginDTO(loginEntity));
            }

        }
    }

//===================================================
// Conversion
//===================================================
    /**
     * Converts a detailed neighborhood DTO to a neighborhood entity.
     *
     * @return new neighborhood entity
     *
     */
    @Override
    public NeighborhoodEntity toEntity() {
        NeighborhoodEntity neighborhood = super.toEntity();

        // adds all businesses to the neighborhood entity
        if (businesses != null) {
            List<BusinessEntity> theBusinesses = new ArrayList<>();
            for (BusinessDTO businessDTO : businesses) {
                theBusinesses.add(businessDTO.toEntity());
            }
            neighborhood.setBusinesses(theBusinesses);
        }

        // adds all residents to the neighborhood entity
        if (residents != null) {
            List<ResidentProfileEntity> theResidents = new ArrayList<>();
            for (ResidentProfileDTO residentDTO : residents) {
                theResidents.add(residentDTO.toEntity());
            }
            neighborhood.setResidents(theResidents);
        }

        // adds all places to the neighborhood entity
        if (places != null) {
            List<LocationEntity> thePlaces = new ArrayList<>();
            for (LocationDTO locationDTO : places) {
                thePlaces.add(locationDTO.toEntity());
            }
            neighborhood.setPlaces(thePlaces);
        }

        // adds all groups to the neighborhood entity
        if (groups != null) {
            List<GroupEntity> theGroups = new ArrayList<>();
            for (GroupDTO groupDTO : groups) {
                theGroups.add(groupDTO.toEntity());
            }
            neighborhood.setGroups(theGroups);
        }

        // adds all logins to the neighborhood entity
        if (logins != null) {
            List<ResidentLoginEntity> theLogins = new ArrayList<>();
            for (ResidentLoginDTO loginDTO : logins) {
                theLogins.add(loginDTO.toEntity());
            }
            neighborhood.setLogins(theLogins);
        }
        return neighborhood;
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

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the list of business DTOs in the neighborhood.
     *
     * @return the list of business DTOs in the neighborhood
     */
    public List<BusinessDTO> getBusinesses() {
        return businesses;
    }

    /**
     * Sets the list of business DTOs in the neighborhood.
     *
     * @param pBusinesses the new list of business DTOs
     */
    public void setBusinesses(List<BusinessDTO> pBusinesses) {
        businesses = pBusinesses;
    }

    /**
     * Returns a list of resident DTOs in the neighborhood.
     *
     * @return the list of resident DTOs in the neighborhood
     */
    public List<ResidentProfileDTO> getResidents() {
        return residents;
    }

    /**
     * Sets the list of resident DTOs in the neighborhood.
     *
     * @param pResidents the new list of resident DTOs
     */
    public void setResidents(List<ResidentProfileDTO> pResidents) {
        residents = pResidents;
    }

    /**
     * Returns the list of location DTOs in the neighborhood.
     *
     * @return the list of location DTOs
     */
    public List<LocationDTO> getPlaces() {
        return places;
    }

    /**
     * Sets the list of location DTOs in the neighborhood
     *
     * @param pPlaces the new list of location DTOs
     */
    public void setPlaces(List<LocationDTO> pPlaces) {
        places = pPlaces;
    }

    /**
     * Returns the list of group DTOs in the neighborhood.
     *
     * @return the list of group DTOs
     */
    public List<GroupDTO> getGroups() {
        return groups;
    }

    /**
     * Sets the list of group DTOs in the neighborhood.
     *
     * @param pGroups the new list of group DTOs
     */
    public void setGroups(List<GroupDTO> pGroups) {
        groups = pGroups;
    }

    public List<ResidentLoginDTO> getLogins() {
        return logins;
    }

    public void setLogins(List<ResidentLoginDTO> logins) {
        this.logins = logins;
    }

}
