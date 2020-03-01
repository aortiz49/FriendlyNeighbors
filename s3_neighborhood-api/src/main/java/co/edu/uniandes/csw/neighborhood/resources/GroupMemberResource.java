/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.neighborhood.resources;

import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDetailDTO;
import co.edu.uniandes.csw.neighborhood.ejb.GroupMemberLogic;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.ejb.ResidentProfileLogic;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.mappers.WebApplicationExceptionMapper;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 * Class implementing resource "groups/{id}/members".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupMemberResource {

    private static final Logger LOGGER = Logger.getLogger(GroupMemberResource.class.getName());

    @Inject
    private GroupMemberLogic groupMemberLogic;

    @Inject
    private ResidentProfileLogic memberLogic;

    /**
     * Associates a member with an existing group
     *
     * @param membersId id from member to be associated
     * @param groupsId id from group
     * @return JSON {@link ResidentProfileDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{membersId: \\d+}")
    public ResidentProfileDetailDTO associateMemberToGroup(@PathParam("groupsId") Long groupsId, @PathParam("membersId") Long membersId) {
        LOGGER.log(Level.INFO, "Associating member to group from resource: input: groupsId {0} , membersId {1}", new Object[]{groupsId, membersId});
        if (memberLogic.getResident(membersId) == null) {
            throw new WebApplicationException("Resource /members/" + membersId + " does not exist.", 404);
        }
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(groupMemberLogic.associateResidentProfileToResident(groupsId, membersId));
        LOGGER.log(Level.INFO, "Ended associating member to group from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the members associated to a group and returns it
     *
     * @param groupsId id from group whose members are wanted
     * @return JSONArray {@link ResidentProfileDetailDTO} - members found in group. An
     * empty list if none is found
     */
    @GET
    public List<ResidentProfileDetailDTO> getMembers(@PathParam("groupsId") Long groupsId) {
        LOGGER.log(Level.INFO, "Looking for members from resources: input: {0}", groupsId);
        List<ResidentProfileDetailDTO> list = membersListEntity2DTO(groupMemberLogic.getResidentProfiles(groupsId));
        LOGGER.log(Level.INFO, "Ended looking for members from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a member with specified ID by URL which is associated with a
     * group and returns it
     *
     * @param membersId id from wanted member
     * @param groupsId id from group whose member is wanted
     * @return {@link ResidentProfileDetailDTO} - member found inside group
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if member not found
     */
    @GET
    @Path("{membersId: \\d+}")
    public ResidentProfileDetailDTO getMember(@PathParam("groupsId") Long groupsId, @PathParam("membersId") Long membersId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for member: input: groupsId {0} , membersId {1}", new Object[]{groupsId, membersId});
        if (memberLogic.getResident(membersId) == null) {
            throw new WebApplicationException("Resource /members/" + membersId + " does not exist.", 404);
        }
        ResidentProfileDetailDTO detailDTO = new ResidentProfileDetailDTO(groupMemberLogic.getResidentProfile(groupsId, membersId));
        LOGGER.log(Level.INFO, "Ended looking for member: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * 
     * Updates a list from members inside a group which is received in body
     *
     * @param groupsId  id from group whose list of members is to be updated
     * @param members JSONArray {@link ResidentProfileDetailDTO} - modified members list 
     * @return JSONArray {@link ResidentProfileDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<ResidentProfileDetailDTO> replaceMembers(@PathParam("groupsId") Long groupsId, List<ResidentProfileDetailDTO> members) {
        LOGGER.log(Level.INFO, "Replacing group members from resource: input: groupsId {0} , members {1}", new Object[]{groupsId, members});
        for (ResidentProfileDetailDTO member : members) {
            if (memberLogic.getResident(member.getId()) == null) {
                     throw new WebApplicationException("Resource /members/" + members + " does not exist.", 404);
            }
        }
        List<ResidentProfileDetailDTO> lista = membersListEntity2DTO(groupMemberLogic.replaceResidentProfiles(groupsId, membersListDTO2Entity(members)));
        LOGGER.log(Level.INFO, "Ended replacing group members from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a member from a group
     *
     * @param groupsId id from group whose member is to be removed
     * @param membersId id from member to be removed
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{membersId: \\d+}")
    public void removeMember(@PathParam("groupsId") Long groupsId, @PathParam("membersId") Long membersId) {
        LOGGER.log(Level.INFO, "Removing member from group: input: groupsId {0} , membersId {1}", new Object[]{groupsId, membersId});
        if (memberLogic.getResident(membersId) == null) {
                 throw new WebApplicationException("Resource /members/" + membersId + " does not exist.", 404);
        }
        groupMemberLogic.removeResidentProfile(groupsId, membersId);
        LOGGER.info("Ended removing member from group: output: void");
    }

    /**
     * Converts an entity list with members to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<ResidentProfileDetailDTO> membersListEntity2DTO(List<ResidentProfileEntity> entityList) {
        List<ResidentProfileDetailDTO> list = new ArrayList<>();
        for (ResidentProfileEntity entity : entityList) {
            list.add(new ResidentProfileDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with members to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<ResidentProfileEntity> membersListDTO2Entity(List<ResidentProfileDetailDTO> dtos) {
        List<ResidentProfileEntity> list = new ArrayList<>();
        for (ResidentProfileDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
