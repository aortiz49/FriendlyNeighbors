package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * This class represent a group in a neighborhood
 *
 * @author albayona
 */

public class GroupDTO {
   

//===================================================
// Attributes
//===================================================
    /**
     * Represents the date group was created
     */
    private Date dateCreated;

    /**
     * Represents the name of this group
     */
    private String name;

    /**
     * Represents a description of this group
     */
    private String description;

    public GroupDTO() {
    }

            
       /**
     * Creates a group DTO from a group entity.
     *
     * @param entityGroup entity from which DTO is to be created
     *
     */
    public GroupDTO(GroupEntity entityGroup) {
        this.dateCreated = entityGroup.getDateCreated();
        this.name = entityGroup.getName();
        this.description = entityGroup.getDescription();
        
    }
    
    /**
     * Converts a group DTO to a group entity.
     *
     * @return new group entity
     *
     */
   public GroupEntity toEntity() {
       GroupEntity  groupEntity = new GroupEntity();
       
       groupEntity.setDateCreated(getDateCreated());
        groupEntity.setName(getName());
        groupEntity.setDescription(getDescription());
        
        return groupEntity;
    }
   
   
    
 

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
     @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
