package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;


/**
* @k.romero
*/
public class PostDTO implements Serializable{
    /**
     * Represents the date post was made
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date publishDate;
    /**
     * Represents the title of the post
     */
    private String title;

    /**
     * Represents the main text body shown in the post
     */
    private String description;
    
    /**
     * Represents the number of numberOfLikes of the post
     */
    private Integer numberOfLikes;

    
    /**
     * The neighborhood to which the group belongs to.
     */
    
     /**
     * Represents the author of this post 
     */
    
    private ResidentProfileDTO author;

    /**
     * Represents the business author of this post 
     */
    
    private BusinessDTO business;
    
    public Date getPublishDate() {
        return publishDate;
    }

      /**
     * Represents the group this post is shared with
     */
  
   private GroupDTO group;
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public GroupDTO getGroup() {
        return group;
    }

    public void setGroup(GroupDTO group) {
        this.group = group;
    }

    public ResidentProfileDTO getAuthor() {
        return author;
    }

    public void setAuthor(ResidentProfileDTO author) {
        this.author = author;
    }

    public BusinessDTO getBusiness() {
        return business;
    }

    public void setBusiness(BusinessDTO business) {
        this.business = business;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
    public PostDTO(){super();}
  
    public PostDTO(PostEntity entityPost) {
        
        this.description = entityPost.getDescription();
        
        this.numberOfLikes = entityPost.getNumberOfLikes();
        this.title =  entityPost.getTitle();
        if (entityPost.getAuthor()!= null) {
            this.author = new ResidentProfileDTO(entityPost.getAuthor());
        }
        if (entityPost.getBusiness()!= null) {
            this.business = new BusinessDTO(entityPost.getBusiness());
        }
        if (entityPost.getGroup()!= null) {
            this.group = new GroupDTO(entityPost.getGroup());
        }
    }

    /**
     * Converts a post DTO to a post entity.
     *
     * @return new post entity
     *
     */
    public PostEntity toEntity() {
        PostEntity postEntity = new PostEntity();

        
        postEntity.setDescription(getDescription());
       
        postEntity.setNumberOfLikes(getNumberOfLikes());
        postEntity.setTitle(getTitle());  
        if (author != null) {
            postEntity.setAuthor(getAuthor().toEntity());
        }
        if (business != null) {
            postEntity.setBusiness(getBusiness().toEntity());
        }
        if (group != null) {
            postEntity.setGroup(getGroup().toEntity());
        }

        return postEntity;
    }
    
    
    
}
