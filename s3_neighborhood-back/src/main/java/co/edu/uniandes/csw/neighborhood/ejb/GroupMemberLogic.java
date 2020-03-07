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
    private GroupPersistence groupsPersistence;

    /**
     * Associates a member with an groups
     *
     * @param groupsId ID from groups entity
     * @param memberId ID from member entity
     * @return associated member entity
     */
    public ResidentProfileEntity associateMemberToGroup(Long groupsId, Long memberId) {
        LOGGER.log(Level.INFO, "Trying to add member to groups with id = {0}", groupsId);
        GroupEntity groupsEntity = groupsPersistence.find(groupsId);
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId);
        groupsEntity.getMembers().add(memberEntity);

        LOGGER.log(Level.INFO, "Resident is associated with groups with id = {0}", groupsId);
        return memberPersistence.find(memberId);
    }

    /**
     * Gets a collection of member entities associated with an groups
     *
     * @param groupsId ID from groups entity
     * @return collection of member entities associated with an groups
     */
    public List<ResidentProfileEntity> getResidentProfiles(Long groupsId) {
        LOGGER.log(Level.INFO, "Gets all members belonging to groups with id = {0}", groupsId);
        return groupsPersistence.find(groupsId).getMembers();
    }

    /**
     * Gets an member entity associated with an groups
     *
     * @param groupsId Id from groups
     * @param memberId Id from associated entity
     * @return associated member entity
     * @throws BusinessLogicException If member is not associated
     */
    public ResidentProfileEntity getResidentProfile(Long groupsId, Long memberId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Finding member with id = {0} from groups with = " + groupsId, memberId);
        List<ResidentProfileEntity> members = groupsPersistence.find(groupsId).getMembers();
        ResidentProfileEntity memberResidentProfiles = memberPersistence.find(memberId);
        int index = members.indexOf(memberResidentProfiles);
        LOGGER.log(Level.INFO, "Finished query about member with id = {0} from groups with = " + groupsId, memberId);
        if (index >= 0) {
            return members.get(index);
        }
        throw new BusinessLogicException("There is no association between member and groups");
    }

    /**
     * Replaces members associated with a groups
     *
     * @param groupsId Id from groups
     * @param members Collection of member to associate with groups
     * @return A new collection associated to groups
     */
    public List<ResidentProfileEntity> replaceResidentProfiles(Long groupsId, List<ResidentProfileEntity> members) {
        LOGGER.log(Level.INFO, "Trying to replace members related to groups with id = {0}", groupsId);
        GroupEntity groupsEntity = groupsPersistence.find(groupsId);
        List<ResidentProfileEntity> memberList = memberPersistence.findAll();
        for (ResidentProfileEntity member : memberList) {
            if (members.contains(member)) {
                if (!member.getGroups().contains(groupsEntity)) {
                    member.getGroups().add(groupsEntity);
                }
            } else {
                member.getGroups().remove(groupsEntity);
            }
        }
        groupsEntity.setMembers(members);
        LOGGER.log(Level.INFO, "Ended replacing members related to groups with id = {0}", groupsId);
        return groupsEntity.getMembers();
    }

    /**
     * Unlinks a member from a groups
     *
     * @param groupsId Id from groups
     * @param memberId Id from member
     */
    public void removeResidentProfile(Long groupsId, Long memberId) {
        LOGGER.log(Level.INFO, "Trying to delete an member from groups with id = {0}", groupsId);
        ResidentProfileEntity memberEntity = memberPersistence.find(memberId);
        GroupEntity groupsEntity = groupsPersistence.find(groupsId);
        groupsEntity.getMembers().remove(memberEntity);
        LOGGER.log(Level.INFO, "Finished removing an member from groups with id = {0}", groupsId);
    }

}
