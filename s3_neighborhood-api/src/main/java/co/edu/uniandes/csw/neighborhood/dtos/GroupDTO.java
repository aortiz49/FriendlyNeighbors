package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
     * Represents id for this group
     */
    private long id;
    /**
     * Represents the date group was created
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date dateCreated;

    /**
     * Represents the name of this group
     */
    private String name;

    /**
     * Represents a description of this group
     */
    private String description;

    /**
     * The neighborhood to which the group belongs to.
     */
    private NeighborhoodDTO neighborhood;

    private String muralPicture;


    public GroupDTO() {
        super();
    }

    /**
     * Creates a group DTO from group entity.
     *
     * @param entityGroup entity from which DTO is to be created
     *
     */
    public GroupDTO(GroupEntity entityGroup) {
        this.id = entityGroup.getId();
        this.dateCreated = entityGroup.getDateCreated();
        this.name = entityGroup.getName();
        this.description = entityGroup.getDescription();
        this.muralPicture = entityGroup.getMuralPicture();

        if (entityGroup.getNeighborhood() != null) {
            this.neighborhood = new NeighborhoodDTO(entityGroup.getNeighborhood());
        }
    }

    /**
     * Converts a group DTO to a group entity.
     *
     * @return new group entity
     *
     */
    public GroupEntity toEntity() {
        GroupEntity groupEntity = new GroupEntity();

        groupEntity.setId(getId());
        groupEntity.setDateCreated(getDateCreated());
        groupEntity.setName(getName());
        groupEntity.setDescription(getDescription());
        groupEntity.setMuralPicture(getMuralPicture());

        if (neighborhood != null) {
            groupEntity.setNeighborhood(getNeighborhood().toEntity());
        }

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

    /**
     * @return the neighborhood
     */
    public NeighborhoodDTO getNeighborhood() {
        return neighborhood;
    }

    /**
     * @param neighborhood the neighborhood to set
     */
    public void setNeighborhood(NeighborhoodDTO neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param Id the id to set
     */
    public void setId(long Id) {
        this.id = Id;
    }

    public String getMuralPicture() {
        return muralPicture;
    }

    public void setMuralPicture(String muralPicture) {
        this.muralPicture = muralPicture;
    }

    
    
}
