/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;


import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author albayona
 */
@Stateless
public class MemberGroupLogic {

    private static final Logger LOGGER = Logger.getLogger(MemberGroupLogic.class.getName());

    @Inject
    private GroupPersistence groupPersistence;

    @Inject
    private ResidentProfilePersistence memberPersistence;

    /**
     * Associates an group with a member
     *
     * @param memberId ID from member entity
     * @param groupId ID from group entity
     * @return associated group entity
     */
    public GroupEntity associateGroupToMember(Long memberId, Long groupId) {
       LOGGER.log(Level.INFO, "Trying to associate group with member with id = {0}", memberId);
         ResidentProfileEntity memberEntity = memberPersistence.find(memberId);
        GroupEntity groupEntity = groupPersistence.find(groupId);
        groupEntity.getMembers().add(memberEntity);
        
        LOGGER.log(Level.INFO, "Group is associated with member with id = {0}", memberId);
       return groupPersistence.find(groupId);
    }

    /**
     * Gets a collection of group entities associated with a member 
     * @param memberId ID from member entity
     * @return collection of group entities associated with a member 
     */
    public List<GroupEntity> getGroups(Long memberId) {
       LOGGER.log(Level.INFO, "Gets all groups belonging to member with id = {0}", memberId);
            return memberPersistence.find(memberId).getGroups();
    }

    /**
     * Gets an group entity associated with a a member
     *
     * @param memberId Id from member
     * @param groupId Id from associated entity
     * @return associated entity
     * @throws BusinessLogicException If group is not associated 
     */
    public GroupEntity getGroup(Long memberId, Long groupId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding group with id = {0} from member with = " + memberId, groupId);
   List<GroupEntity> groups = memberPersistence.find(memberId).getGroups();
        GroupEntity groupGroups = groupPersistence.find(groupId);
        int index = groups.indexOf(groupGroups);
       LOGGER.log(Level.INFO, "Finish query about group with id = {0} from member with = " + memberId, groupId);
        if (index >= 0) {
            return groups.get(index);
        }
         throw new BusinessLogicException("There is no association between member and group");
    }

    /**
     * Replaces groups associated with a member
     *
     * @param memberId Id from member
     * @param groups Collection of group to associate with member
     * @return A new collection associated to member
     */
    public List<GroupEntity> replaceGroups(Long memberId, List<GroupEntity> groups) {
        LOGGER.log(Level.INFO, "Trying to replace groups related to member con id = {0}", memberId);
          ResidentProfileEntity memberEntity = memberPersistence.find(memberId);
        List<GroupEntity> groupList = groupPersistence.findAll();
        for (GroupEntity group : groupList) {
            if (groups.contains(group)) {
                if (!group.getMembers().contains(memberEntity)) {
                    group.getMembers().add(memberEntity);
                }
            } else {
                group.getMembers().remove(memberEntity);
            }
        }
        memberEntity.setGroups(groups);
       LOGGER.log(Level.INFO, "Ended trying to replace groups related to member con id = {0}", memberId);
           return memberEntity.getGroups();
    }

    /**
     * Unlinks an group from a member
     *
     * @param memberId Id from member
     * @param groupId Id from group     
     */
    public void removeGroup(Long memberId, Long groupId) {
         LOGGER.log(Level.INFO, "Trying to delete an group from member con id = {0}", memberId);
       ResidentProfileEntity memberEntity = memberPersistence.find(memberId);
        GroupEntity groupEntity = groupPersistence.find(groupId);
        groupEntity.getMembers().remove(memberEntity);
       LOGGER.log(Level.INFO, "Finished removing an group from member con id = {0}", memberId);
        }
}
