/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author v.cardonac1
 */
public class LocationDetailDTO extends LocationDTO implements Serializable{

    private List<EventDTO> events = new ArrayList<>();
    
    public LocationDetailDTO(){
        super();
    }
    
    public LocationDetailDTO(LocationEntity locationEntity){
        super(locationEntity);
        
        if(locationEntity != null){
            events = new ArrayList<>();
            
            for (EventEntity entityEvent : locationEntity.getEvents()) {
                events.add(new EventDTO(entityEvent));
            }
        }
    }
    
    public LocationEntity toEntity(){
        LocationEntity locationEntity = super.toEntity();
        
        if(getEvents() != null){
            List<EventEntity> eventsE = new ArrayList<>();
            for(EventDTO dtoEvent : events){
                eventsE.add(dtoEvent.toEntity());
            }
            locationEntity.setEvents(eventsE);
        }
        return locationEntity;
    }
    
    
    /**
     * @return the events
     */
    public List<EventDTO> getEvents() {
        return events;
    }

    /**
     * @param events the events to set
     */
    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
    
}
