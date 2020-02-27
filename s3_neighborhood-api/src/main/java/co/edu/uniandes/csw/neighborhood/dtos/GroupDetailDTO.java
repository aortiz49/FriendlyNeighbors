package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * This class represent a set of neighbors with common interests
 *
 * @author albayona
 */

public class GroupDetailDTO extends GroupDTO implements Serializable {

    
//===================================================
// Attributes
//===================================================

    /**
     * Represents the groups who are members of this post
     */
    private List<ResidentProfileDTO> members = new ArrayList();

    
     /**
     * The posts made by the group.
     */
    private List<PostDTO> posts = new ArrayList<>();

    
    
    /**
     * Creates a detailed group DTO from a group entity, including its
     * relations.
     *
     * @param groupEntity from which new group DTO will be created
     *
     */
    public GroupDetailDTO(GroupEntity groupEntity) {
        super(groupEntity);
        if (groupEntity != null) {

            posts = new ArrayList<>();
            members = new ArrayList<>();


          
            for (PostEntity entityPost : groupEntity.getPosts()) {
                posts.add(new PostDTO(entityPost));
            }

            for (ResidentProfileEntity entityResident : groupEntity.getMembers()) {
                members.add(new ResidentProfileDTO(entityResident));
            }

        }
    }

    /**
     * Converts a detailed group DTO to a group entity.
     *
     * @return new group entity
     *
     */
    public GroupEntity toEntity() {
        GroupEntity groupEntity = super.toEntity();

        if (getMembers() != null) {
            List<ResidentProfileEntity> membersE = new ArrayList<>();
            for (ResidentProfileDTO dtoMember : getMembers()) {
                membersE.add(dtoMember.toEntity());
            }
            groupEntity.setMembers(membersE);
        }
        
        
        if (getPosts() != null) {
            List<PostEntity> postsE = new ArrayList<>();
            for (PostDTO dtoMember : getPosts()) {
                postsE.add(dtoMember.toEntity());
            }
            groupEntity.setPosts(postsE);
        }
            
            
        return groupEntity;
    }

    /**
     * @return the members
     */
    public List<ResidentProfileDTO> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<ResidentProfileDTO> members) {
        this.members = members;
    }

    /**
     * @return the posts
     */
    public List<PostDTO> getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }
    
    

 

}
