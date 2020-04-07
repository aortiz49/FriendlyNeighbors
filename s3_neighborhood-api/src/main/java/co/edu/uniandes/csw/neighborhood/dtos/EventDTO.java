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
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Data transfer object for Events. This DTO contains the JSON representation of an Event that gets
 * transferred between the client and server.
 *
 * @author aortiz49
 */
public class EventDTO implements Serializable {
//===================================================
// Dependencies
//===================================================

    /**
     * The event's location.
     */
    private LocationDTO location;
    
    /**
     * The event's host.
     */
    private ResidentProfileDTO host;

//===================================================
// Attributes
//===================================================
    
    /**
     * The event's id.
     */
    private long id;

    /**
     * The title of the event.
     */
    private String title;

    /**
     * The date the event was posted.
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datePosted;

    /**
     * The date the event will take place.
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date dateOfEvent;

    /**
     * The time at which the event starts.
     */
    private String startTime;

    /**
     * The time at which the event ends.
     */
    private String endTime;

    /**
     * The description of the event.
     */
    private String description;

    /**
     * The 
     */
    private String availability;

//===================================================
// Getters & Setters
//===================================================


}
