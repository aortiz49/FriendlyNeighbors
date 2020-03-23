package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;



/**
 * This class represent a set of neighbors with common interests
 *
 * @author k.romero
 */

public class PostDetailDTO extends PostDTO implements Serializable {

    
//===================================================
// Attributes
//===================================================

    /**
     * Represents the posts who are members of this post
     * 
     */
    

    private List<CommentDTO> comments;
    
    private List<ResidentProfileDTO> viewers;
    

    public PostDetailDTO() {
        super();
    }

    
    
    
    /**
     * Creates a detailed post DTO from post entity, including its
     * relations.
     * 
     *
     * @param postEntity from which new post DTO will be created
     *
     */
    public PostDetailDTO(PostEntity postEntity) {
        super(postEntity);
        if (postEntity != null) {

            comments = new ArrayList<>();
            
            viewers = new ArrayList<>();
        for (ResidentProfileEntity entityResidentProfile : postEntity.getViewers()) {
                        viewers.add(new ResidentProfileDTO(entityResidentProfile));
                    }
        for (CommentEntity entityComment : postEntity.getComments()) {
                        comments.add(new CommentDTO(entityComment));
                    }
       

          
            

           

        }
    }

    /**
     * Converts a detailed post DTO to a post entity.
     *
     * @return new post entity
     *
     */
    public PostEntity toEntity() {
        PostEntity postEntity = super.toEntity();

         if (comments!= null) {
            List<CommentEntity> comment = new ArrayList<>();
            for (CommentDTO dtoComment : comments) {
                comment.add(dtoComment.toEntity());
            }
            postEntity.setComments(comment);
        }
          if (viewers  != null) {
            List<ResidentProfileEntity> viwer = new ArrayList<>();
            for (ResidentProfileDTO dtoViewer : viewers) {
                viwer.add(dtoViewer.toEntity());
            }
            postEntity.setViewers(viwer);
        }
        return postEntity;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<ResidentProfileDTO> getViewers() {
        return viewers;
    }

    public void setViewers(List<ResidentProfileDTO> viewers) {
        this.viewers = viewers;
    }

   

   

   
    

 

}
