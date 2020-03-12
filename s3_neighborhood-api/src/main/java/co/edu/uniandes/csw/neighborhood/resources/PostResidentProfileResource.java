/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.PostDTO;
import co.edu.uniandes.csw.neighborhood.dtos.PostDetailDTO;
import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDTO;
import co.edu.uniandes.csw.neighborhood.ejb.PostLogic;
import co.edu.uniandes.csw.neighborhood.ejb.PostResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author v.cardonac1
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResidentProfileResource {
    
    private static final Logger LOGGER = Logger.getLogger(PostResidentProfileResource.class.getName());

    @Inject
    private PostResidentProfileLogic postResidentLogic;
    
    @Inject
    private PostLogic postLogic;
    
    @POST
    @Path("{postsId: \\d+}")
    public PostDetailDTO associatePostToResidentProfile(@PathParam("residentId") Long residentId, @PathParam("postsId") Long postsId) throws BusinessLogicException {
        if(postLogic.getPost(postsId)==null){
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO dto = new PostDetailDTO(postResidentLogic.associatePostToResident(postsId, residentId));
        return dto;
    }
    
    @GET
    public List<PostDetailDTO> getPosts(@PathParam("residentId") Long residentId){
        List<PostDetailDTO> list = postsListEntity2DTO(postResidentLogic.getPosts(residentId));
        return list;
    }
    
    @GET
    @Path("{postsId: \\d+}")
    public PostDTO getPost(@PathParam("authorsId") Long residentsId, @PathParam("postsId") Long postsId) throws BusinessLogicException {
        if (postLogic.getPost(postsId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        PostDetailDTO dto = new PostDetailDTO(postResidentLogic.getPost(residentsId, postsId));
        return dto;
    }
    
    @PUT
    public List<PostDetailDTO> replacePosts(@PathParam("residentsId") Long residentsId, List<PostDetailDTO> posts) {
        for (PostDetailDTO post : posts) {
            if (postLogic.getPost(post.getId()) == null) {
                throw new WebApplicationException("Resource /posts/" + posts + " does not exist.", 404);
            }
        }
        List<PostDetailDTO> lista = postsListEntity2DTO(postResidentLogic.replacePosts(residentsId, postsListDTO2Entity(posts)));
        return lista;
    }
    
    @DELETE
    @Path("{postsId: \\d+}")
    public void removePost(@PathParam("residentsId") Long residentsId, @PathParam("postsId") Long postsId) throws BusinessLogicException {
        if (postLogic.getPost(postsId) == null) {
            throw new WebApplicationException("Resource /posts/" + postsId + " does not exist.", 404);
        }
        postResidentLogic.removePost(residentsId, postsId);
    }
    
    private List<PostDetailDTO> postsListEntity2DTO(List<PostEntity> entityList) {
        List<PostDetailDTO> list = new ArrayList<>();
        for (PostEntity entity : entityList) {
            list.add(new PostDetailDTO(entity));
        }
        return list;
    }

    private List<PostEntity> postsListDTO2Entity(List<PostDetailDTO> dtos) {

        List<PostEntity> list = new ArrayList<>();
        for (PostDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
        
    }
}
