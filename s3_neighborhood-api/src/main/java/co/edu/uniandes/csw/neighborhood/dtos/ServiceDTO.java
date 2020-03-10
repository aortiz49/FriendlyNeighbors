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

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.ServiceEntity;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data transfer object for Services. This DTO contains the JSON representation of a Service that
 * gets transferred between the client and server.
 *
 * @author aortiz49
 */
public class ServiceDTO implements Serializable {
//===================================================
// Dependencies
//===================================================

    /**
     * The resident who authored the services.
     */
    private ResidentProfileDTO author;

//===================================================
// Attributes
//===================================================
    /**
     * The date the services was posted.
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datePosted;

    /**
     * The title of of the service.
     */
    private String title;

    /**
     * The description of the service.
     */
    private String description;

    /**
     * Indicates if a service is available or not.
     */
    private boolean isAvailable;

//===================================================
// Constructors
//===================================================
    /**
     * Empty ServiceDTO constructor.
     */
    public ServiceDTO() {
    }

    /**
     * Creates a ServiceDTO object from a ServiceEntity object.
     *
     * @param pService resident login entity from which to create the new ResidentLoginDTO
     *
     */
    public ServiceDTO(ServiceEntity pService) {
        if (pService != null) {
            this.datePosted = pService.getDatePosted();
            this.title = pService.getTitle();
            this.description = pService.getDescription();
            this.isAvailable = pService.getIsAvailable();

            this.author = new ResidentProfileDTO(pService.getAuthor());

        }
    }
//===================================================
// Conversion
//===================================================

    /**
     * Converts a ServiceDTO object into a ServiceEntity.
     *
     * @return the new ServiceEntity object
     *
     */
    public ServiceEntity toEntity() {
        ServiceEntity service = new ServiceEntity();
        service.setDatePosted(this.getDatePosted());
        service.setTitle(this.getTitle());
        service.setDescription(this.getDescription());
        service.setAvailability(this.isAvailable());

        if (author != null) {
            service.setAuthor(getAuthor().toEntity());
        }

        return service;
    }
//===================================================
// Getters & Setters
//===================================================

    /**
     * Returns the author of the service.
     * 
     * @return the author
     */
    public ResidentProfileDTO getAuthor() {
        return author;
    }

    /**
     * Sets the new author of the service.
     * 
     * @param pAuthor the new author
     */
    public void setAuthor(ResidentProfileDTO pAuthor) {
        author = pAuthor;
    }

    /**
     * Returns the date the service was posted. 
     * 
     * @return the post date
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * Sets the date the service was posted.
     * 
     * @param pDatePosted the new post date
     */
    public void setDatePosted(Date pDatePosted) {
        datePosted = pDatePosted;
    }

    /**
     * Returns the title of the service. 
     * 
     * @return the titles
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the service. 
     * 
     * @param pTitle the new title
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    /**
     * Returns the description of the service.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the service.
     * 
     * @param pDescription the new description
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    /**
     * Checks the service availability status.
     * 
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets the service availability status.
     * 
     * @param pIsAvailable the new availability
     */
    public void setIsAvailable(boolean pIsAvailable) {
        isAvailable = pIsAvailable;
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
