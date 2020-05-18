/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import co.edu.uniandes.csw.neighborhood.entities.GroupEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;

import co.edu.uniandes.csw.neighborhood.persistence.ResidentProfilePersistence;
import co.edu.uniandes.csw.neighborhood.persistence.GroupPersistence;
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
public class GroupMemberLogic {

    private static final Logger LOGGER = Logger.getLogger(ResidentProfileLogic.class.getName());

    @Inject
    private ResidentProfilePersistence memberPersistence;

    @Inject
    private GroupPersistence groupPersistence;

    /**
     * Associates member with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @param memberId id from member
     * @return associated member
     */
    public ResidentProfileEntity associateMemberToGroup(Long groupId, Long memberId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between member with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId, neighId);

        groupEntity.getMembers().add(memberEntity);

        LOGGER.log(Level.INFO, "Association created between member with id {0} and group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
        return memberPersistence.find(memberId, neighId);
    }

    /**
     * Gets a collection of member entities associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group entity
     * @return collection of member entities associated with group
     */
    public List<ResidentProfileEntity> getMembers(Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all members belonging to group with id = {0} from neighborhood {1}", new Object[]{groupId, neighId});
        return groupPersistence.find(groupId, neighId).getMembers();
    }

    /**
     * Gets member associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group
     * @param memberId id from associated entity
     * @return associated member
     * @throws BusinessLogicException If member is not associated
     */
    public ResidentProfileEntity getMember(Long groupId, Long memberId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about member with id {0} from group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
        List<ResidentProfileEntity> members = groupPersistence.find(groupId, neighId).getMembers();
        ResidentProfileEntity memberResidentProfiles = memberPersistence.find(memberId, neighId);
        int index = members.indexOf(memberResidentProfiles);
        LOGGER.log(Level.INFO, "Finish query about member with id {0} from group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
        if (index >= 0) {
            return members.get(index);
        }
        throw new BusinessLogicException("There is no association between member and group");
    }

    /**
     * Replaces members associated with group
     *
     * @param neighId parent neighborhood
     * @param groupId id from group
     * @param members collection of member to associate with group
     * @return A new collection associated to group
     */
    public List<ResidentProfileEntity> replaceMembers(Long groupId, List<ResidentProfileEntity> members, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace members related to group with id {0} from neighborhood {1}", new Object[]{groupId, neighId});
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        List<ResidentProfileEntity> memberList = memberPersistence.findAll(neighId);
        for (ResidentProfileEntity member : memberList) {
            if (members.contains(member)) {
                if (!member.getGroups().contains(groupEntity)) {
                    member.getGroups().add(groupEntity);
                }
            } else {
                member.getGroups().remove(groupEntity);
            }
        }
        groupEntity.setMembers(members);
        LOGGER.log(Level.INFO, "Ended trying to replace members related to group with id {0} from neighborhood {1}", new Object[]{groupId, neighId});
        return groupEntity.getMembers();
    }

    /**
     * Unlinks member from group
     *
     * @param neighId parent neighborhood
     * @param groupId Id from group
     * @param memberId Id from member
     */
    public void removeMember(Long groupId, Long memberId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between member with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        groupEntity.getMembers().remove(memberEntity);
        LOGGER.log(Level.INFO, "Association deleted between member with id {0} and  group with id {1}, from neighbothood {2}", new Object[]{memberId, groupId, neighId});
    }

}
