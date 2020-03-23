
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
     * Associates group with member
     *
     * @param neighId parent neighborhood
     * @param memberId id from member entity
     * @param groupId id from group
     * @return associated group
     */
    public GroupEntity associateGroupToMember(Long memberId, Long groupId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating association between group with id {0} and  member with id {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);

        groupEntity.getMembers().add(memberEntity);

        LOGGER.log(Level.INFO, "Association created between group with id {0} and  member with id  {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
        return groupPersistence.find(groupId, neighId);
    }

    /**
     * Gets a collection of group entities associated with member
     *
     * @param neighId parent neighborhood
     * @param memberId id from member entity
     * @return collection of group entities associated with member
     */
    public List<GroupEntity> getGroups(Long memberId, Long neighId) {
        LOGGER.log(Level.INFO, "Gets all groups belonging to member with id {0} from neighborhood {1}", new Object[]{memberId, neighId});
        return memberPersistence.find(memberId, neighId).getGroups();
    }

    /**
     * Gets group associated with member
     *
     * @param neighId parent neighborhood
     * @param memberId id from member
     * @param groupId id from associated entity
     * @return associated group
     * @throws BusinessLogicException If group is not associated
     */
    public GroupEntity getGroup(Long memberId, Long groupId, Long neighId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Initiating query about group with id {0} from member with {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
        List<GroupEntity> groups = memberPersistence.find(memberId, neighId).getGroups();
        GroupEntity groupGroups = groupPersistence.find(groupId, neighId);
        int index = groups.indexOf(groupGroups);
        LOGGER.log(Level.INFO, "Finish query about group with id {0} from member with {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
        if (index >= 0) {
            return groups.get(index);
        }
        throw new BusinessLogicException("There is no association between member and group");
    }

    /**
     * Replaces groups associated with member
     *
     * @param neighId parent neighborhood
     * @param memberId id from member
     * @param groups collection of group to associate with member
     * @return A new collection associated to member
     */
    public List<GroupEntity> replaceGroups(Long memberId, List<GroupEntity> groups, Long neighId) {
        LOGGER.log(Level.INFO, "Trying to replace groups related to member with id {0} from neighborhood {1}", new Object[]{memberId, neighId});
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId, neighId);
        List<GroupEntity> groupList = groupPersistence.findAll(neighId);
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
        LOGGER.log(Level.INFO, "Ended trying to replace groups related to member with id {0} from neighborhood {1}", new Object[]{memberId, neighId});
        return memberEntity.getGroups();
    }

    /**
     * Unlinks group from member
     *
     * @param neighId parent neighborhood
     * @param memberId Id from member
     * @param groupId Id from group
     */
    public void removeGroup(Long memberId, Long groupId, Long neighId) {
        LOGGER.log(Level.INFO, "Deleting association between group with id {0} and  member with id {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId, neighId);
        GroupEntity groupEntity = groupPersistence.find(groupId, neighId);
        groupEntity.getMembers().remove(memberEntity);
        LOGGER.log(Level.INFO, "Association deleted between group with id {0} and  member with id {1}, from neighbothood {2}", new Object[]{groupId, memberId, neighId});
    }
}
