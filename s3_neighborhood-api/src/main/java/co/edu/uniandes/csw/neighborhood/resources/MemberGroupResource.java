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

import co.edu.uniandes.csw.neighborhood.dtos.GroupDetailDTO;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.ejb.GroupLogic;
import co.edu.uniandes.csw.neighborhood.ejb.MemberGroupLogic;
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
 * Class implementing resource "members/{id}/groups".
 *
 * @author albayona
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MemberGroupResource {

    private static final Logger LOGGER = Logger.getLogger(MemberGroupResource.class.getName());

    @Inject
    private MemberGroupLogic memberGroupLogic;

    @Inject
    private GroupLogic groupLogic;

    /**
     * Associates a group with an existing member
     *
     * @param groupsId id from group to be associated
     * @param membersId id from member
     * @return JSON {@link GroupDetailDTO} -
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Logic error if not found
     */
    @POST
    @Path("{groupsId: \\d+}")
    public GroupDetailDTO associateGroupToResidentProfile(@PathParam("membersId") Long membersId, @PathParam("groupsId") Long groupsId) {
        LOGGER.log(Level.INFO, "Associating group to member from resource: input: membersId {0} , groupsId {1}", new Object[]{membersId, groupsId});
        if (groupLogic.getGroup(groupsId) == null) {
            throw new WebApplicationException("Resource /groups/" + groupsId + " does not exist.", 404);
        }
        GroupDetailDTO detailDTO = new GroupDetailDTO(memberGroupLogic.associateGroupToMember(membersId, groupsId));
        LOGGER.log(Level.INFO, "Ended associating group to member from resource: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Looks for all the groups associated to a member and returns it
     *
     * @param membersId id from member whose groups are wanted
     * @return JSONArray {@link GroupDetailDTO} - groups found in member. An
     * empty list if none is found
     */
    @GET
    public List<GroupDetailDTO> getGroups(@PathParam("membersId") Long membersId) {
        LOGGER.log(Level.INFO, "Looking for groups from resources: input: {0}", membersId);
        List<GroupDetailDTO> list = groupsListEntity2DTO(memberGroupLogic.getGroups(membersId));
        LOGGER.log(Level.INFO, "Ended looking for groups from resources: output: {0}", list);
        return list;
    }

    /**
     * Looks for a group with specified ID by URL which is associated with a
     * member and returns it
     *
     * @param groupsId id from wanted group
     * @param membersId id from member whose group is wanted
     * @return {@link GroupDetailDTO} - group found inside member
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Logic error if group not found
     */
    @GET
    @Path("{groupsId: \\d+}")
    public GroupDetailDTO getGroup(@PathParam("membersId") Long membersId, @PathParam("groupsId") Long groupsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Looking for group: input: membersId {0} , groupsId {1}", new Object[]{membersId, groupsId});
        if (groupLogic.getGroup(groupsId) == null) {
            throw new WebApplicationException("Resource /groups/" + groupsId + " does not exist.", 404);
        }
        GroupDetailDTO detailDTO = new GroupDetailDTO(memberGroupLogic.getGroup(membersId, groupsId));
        LOGGER.log(Level.INFO, "Ended looking for group: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * 
     * Updates a list from groups inside a member which is received in body
     *
     * @param membersId  id from member whose list of groups is to be updated
     * @param groups JSONArray {@link GroupDetailDTO} - modified groups list 
     * @return JSONArray {@link GroupDetailDTO} - updated list
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @PUT
    public List<GroupDetailDTO> replaceGroups(@PathParam("membersId") Long membersId, List<GroupDetailDTO> groups) {
        LOGGER.log(Level.INFO, "Replacing member groups from resource: input: membersId {0} , groups {1}", new Object[]{membersId, groups});
        for (GroupDetailDTO group : groups) {
            if (groupLogic.getGroup(group.getId()) == null) {
                     throw new WebApplicationException("Resource /groups/" + groups + " does not exist.", 404);
            }
        }
        List<GroupDetailDTO> lista = groupsListEntity2DTO(memberGroupLogic.replaceGroups(membersId, groupsListDTO2Entity(groups)));
        LOGGER.log(Level.INFO, "Ended replacing member groups from resource: output:{0}", lista);
        return lista;
    }

    /**
     * Removes a group from a member
     *
     * @param membersId id from member whose group is to be removed
     * @param groupsId id from group to be removed
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error if not found
     */
    @DELETE
    @Path("{groupsId: \\d+}")
    public void removeGroup(@PathParam("membersId") Long membersId, @PathParam("groupsId") Long groupsId) {
        LOGGER.log(Level.INFO, "Removing group from member: input: membersId {0} , groupsId {1}", new Object[]{membersId, groupsId});
        if (groupLogic.getGroup(groupsId) == null) {
                 throw new WebApplicationException("Resource /groups/" + groupsId + " does not exist.", 404);
        }
        memberGroupLogic.removeGroup(membersId, groupsId);
        LOGGER.info("Ended removing group from member: output: void");
    }

    /**
     * Converts an entity list with groups to a DTO list.
     *
     * @param entityList entity list.
     * @return DTO list.
     */
    private List<GroupDetailDTO> groupsListEntity2DTO(List<GroupEntity> entityList) {
        List<GroupDetailDTO> list = new ArrayList<>();
        for (GroupEntity entity : entityList) {
            list.add(new GroupDetailDTO(entity));
        }
        return list;
    }

    /**
     * Converts a DTO list with groups to an entity list.
     *
     * @param dtos DTO list.
     * @return entity list.
     */
    private List<GroupEntity> groupsListDTO2Entity(List<GroupDetailDTO> dtos) {
        List<GroupEntity> list = new ArrayList<>();
        for (GroupDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
