package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * This class represent a set of neighbors with common interests
 *
 * @author k.romero
 */

public class EventDetailDTO extends EventDTO implements Serializable {

    
//===================================================
// Attributes
//===================================================
/**
     * The residents attending the event.
     */
    private List<ResidentProfileDTO> attendees;
    /**
     * The groups attending the event.
     */
    private List<GroupDTO> groups;
    
     /**
     * The posts made by the event.
     */
    private List<GroupDTO> posts = new ArrayList<>();

    public EventDetailDTO() {
        super();
    }

    public List<ResidentProfileDTO> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<ResidentProfileDTO> attendees) {
        this.attendees = attendees;
    }

    public List<GroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDTO> groups) {
        this.groups = groups;
    }

    public List<GroupDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<GroupDTO> posts) {
        this.posts = posts;
    }

    
    
    
    /**
     * Creates a detailed event DTO from event entity, including its
     * relations.
     *
     * @param eventEntity from which new event DTO will be created
     *
     */
    public EventDetailDTO(EventEntity eventEntity) {
      super(eventEntity);
      groups = new ArrayList<>();
      attendees = new ArrayList<>();
      for (GroupEntity entityGroup : eventEntity.getGroups()) {
                groups.add(new GroupDTO(entityGroup));
            }
      for (ResidentProfileEntity entityResidentProfile : eventEntity.getAttendees()) {
                attendees.add(new ResidentProfileDTO(entityResidentProfile));
            }
    }

    /**
     * Converts a detailed event DTO to a event entity.
     *
     * @return new event entity
     *
     */
    public EventEntity toEntity() {
       EventEntity eventEntity = super.toEntity();
       
       if (groups  != null) {
            List<GroupEntity> groupss = new ArrayList<>();
            for (GroupDTO dtoGroup : groups) {
                groupss.add(dtoGroup.toEntity());
            }
            eventEntity.setGroups(groupss);
        }
       return eventEntity;
    }

  

    
    

 

}
