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

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that extends {@link EventDTO} to manage the relations between EventDTO and other DTOs.
 *
 * To know more about the content of an Event, visit the {@link EventDTO} documentation.
 *
 * @author aortiz49
 */
public class EventDetailDTO extends EventDTO implements Serializable {

//===================================================
// Attributes
//===================================================
    /**
     * The list of residents attending the event.
     */
    private List<ResidentProfileDTO> attendees;

//===================================================
// Constructors
//===================================================
    /**
     * Creates a new EventDetailDTO.
     */
    public EventDetailDTO() {
        super();
    }

    /**
     * Creates a new EventDetailDTO from an EventEntity.
     *
     * @param pEventEntity the event entity from which to create the event DTO.
     */
    public EventDetailDTO(EventEntity pEventEntity) {
        super(pEventEntity);

        if (pEventEntity != null) {
            attendees = new ArrayList<>();

            for (ResidentProfileEntity resident : pEventEntity.getAttendees()) {
                attendees.add(new ResidentProfileDTO(resident));
            }
        }
    }

//===================================================
// Methods
//===================================================
    /**
     * Converts the event DTO to an event DTO. Adds the attendees to the event.
     *
     * @return
     */
    public EventEntity toEntity() {
        EventEntity eventEntity = super.toEntity();

        if (getAttendees() != null) {
            List<ResidentProfileEntity> attendeeEntities = new ArrayList<>();

            // add all attendee entities to a list
            for (ResidentProfileDTO attendeeDTO : getAttendees()) {
                attendeeEntities.add(attendeeDTO.toEntity());
            }

            eventEntity.setAttendees(attendeeEntities);
        }

        return eventEntity;
    }

//===================================================
// Getters & Setters
//===================================================
    /**
     * Returns the event's attendees.
     *
     * @return the attendees
     */
    public List<ResidentProfileDTO> getAttendees() {
        return attendees;
    }

    /**
     * Sets the event's new attendees.
     *
     * @param pAttendees the new attendees
     */
    public void setAttendees(List<ResidentProfileDTO> pAttendees) {
        attendees = pAttendees;
    }

}
