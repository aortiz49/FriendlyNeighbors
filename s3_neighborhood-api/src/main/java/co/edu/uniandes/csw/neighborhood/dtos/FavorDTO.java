package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
* @author v.cardonac1
*/
public class FavorDTO {

    //===================================================
    // Attributes
    //===================================================
    /**
     * Represents id for this favor
     */
    private long id;
    
    /**
     * Represents the date group was created
     */
     @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datePosted;
    
    private String title;

    private String description;

    private String type;
    
    private Boolean isHelpWanted;

    private ResidentProfileDTO author;
    
    public FavorDTO(){
        super();
    }
    
    public FavorDTO(FavorEntity entityFavor){
        this.id = entityFavor.getId();
        this.datePosted = entityFavor.getDatePosted();
        this.title = entityFavor.getTitle();
        this.description = entityFavor.getDescription();
        this.type = entityFavor.getType();
        this.isHelpWanted = entityFavor.getIsHelpWanted();
        this.author = new ResidentProfileDTO(entityFavor.getAuthor());
    }

    public FavorEntity toEntity() {
        FavorEntity favorEntity = new FavorEntity();
        
        favorEntity.setId(getId());
        favorEntity.setDatePosted(getDatePosted());
        favorEntity.setTitle(getTitle());
        favorEntity.setDescription(getDescription());
        favorEntity.setType(getType());
        favorEntity.setIsHelpWanted(getIsHelpWanted());
        
        if(author != null) favorEntity.setAuthor(getAuthor().toEntity());
        
        return favorEntity;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the datePosted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * @param datePosted the datePosted to set
     */
    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the isHelpWanted
     */
    public Boolean getIsHelpWanted() {
        return isHelpWanted;
    }

    /**
     * @param isHelpWanted the isHelpWanted to set
     */
    public void setIsHelpWanted(Boolean isHelpWanted) {
        this.isHelpWanted = isHelpWanted;
    }

    /**
     * @return the author
     */
    public ResidentProfileDTO getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(ResidentProfileDTO author) {
        this.author = author;
    }
    
}
