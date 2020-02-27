package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.*;


/**
 * This class represents a comment made in a post
 *
 * @author albayona
 */
public class CommentDetailDTO extends CommentDTO {
    
    
        /**
     * The author of this comment.
     */
    private ResidentProfileDTO author;

    /**
     * The post the comment belongs to.
     */
    private PostDTO post;
    
   
     
    /**
     * Creates a detailed comment DTO from a comment entity, including its
     * relations.
     *
     * @param commentEntity from which new comment DTO will be created
     *
     */
    public CommentDetailDTO(CommentEntity commentEntity) {
        super(commentEntity);
        if (commentEntity != null) {

            post = new PostDTO(commentEntity.getPost());
            
            author = new ResidentProfileDTO(commentEntity.getAuthor());

        }
    }

    /**
     * Converts a detailed comment DTO to a comment entity.
     *
     * @return new comment entity
     *
     */
    public CommentEntity toEntity() {
        CommentEntity commentEntity = super.toEntity();

        
       commentEntity.setAuthor(author.toEntity());
       commentEntity.setPost(post.toEntity());
    
            
        return commentEntity;
    }

    

    
    
}
