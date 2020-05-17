/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class represent a resident in a neighborhood
 *
 * @author albayona
 */
public class ResidentProfileDTO implements Serializable {

//===================================================
// Attributes
//===================================================  
    /**
     * Represents id for this resident
     */
    private Long id;

    /**
     * Represents phone number of this resident
     */
    private String phoneNumber;

    /**
     * Represents email of this resident
     */
    private String email;

    /**
     * Represents the name of this resident
     */
    private String name;

    /**
     * Represents nickname of this resident
     */
    private String nickname;

    /**
     * The resident's address.
     */
    private String address;

    /**
     * Represents preferences of this resident
     */
    private String preferences;

    /**
     * Represents events posted by this resident
     */
    private ResidentLoginDTO login;

    /**
     * Represents the neighborhood of this resident
     */
    private NeighborhoodDTO neighborhood;

    /**
     * Represents the proof of residence for this resident
     */
    private String proofOfResidence;

    private String profilePicture;

    private String muralPicture;

    private String livingSince;

    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date birthDate;

    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date joinDate;

    public ResidentProfileDTO() {
    }

    /**
     * Creates a resident DTO from resident entity.
     *
     * @param residentEntity entity from which DTO is to be created
     *
     */
    public ResidentProfileDTO(ResidentProfileEntity residentEntity) {
        if (residentEntity != null) {
            this.id = residentEntity.getId();
            this.phoneNumber = residentEntity.getPhoneNumber();
            this.email = residentEntity.getEmail();
            this.name = residentEntity.getName();
            this.nickname = residentEntity.getNickname();
            this.address = residentEntity.getAddress();
            this.proofOfResidence = residentEntity.getProofOfResidence();
            this.preferences = residentEntity.getPreferences();
            
               this.profilePicture = residentEntity.getProfilePicture();
            this.muralPicture = residentEntity.getMuralPicture();
            this.livingSince = residentEntity.getLivingSince();
            this.birthDate = residentEntity.getBirthDate();
            this.joinDate = residentEntity.getJoinDate();

            if (residentEntity.getLogin() != null) {
                this.login = new ResidentLoginDTO(residentEntity.getLogin());
            } else {
                residentEntity.setLogin(null);
            }

            if (residentEntity.getNeighborhood() != null) {
                this.neighborhood = new NeighborhoodDTO(residentEntity.getNeighborhood());
            }

        }
    }

    /**
     * Converts a resident DTO to a resident entity.
     *
     * @return new resident entity
     *
     */
    public ResidentProfileEntity toEntity() {
        ResidentProfileEntity residentEntity = new ResidentProfileEntity();
        residentEntity.setId(getId());
        residentEntity.setPhoneNumber(getPhoneNumber());
        residentEntity.setEmail(getEmail());
        residentEntity.setName(getName());
        residentEntity.setNickname(getNickname());
        residentEntity.setAddress(getAddress());
        residentEntity.setPreferences(getPreferences());
        residentEntity.setProofOfResidence(getProofOfResidence());

        residentEntity.setProfilePicture(getProfilePicture());
        residentEntity.setMuralPicture(getMuralPicture());
        residentEntity.setLivingSince(getLivingSince());
        residentEntity.setBirthDate(getBirthDate());
        residentEntity.setJoinDate(getJoinDate());

        if (getLogin() != null) {
            residentEntity.setLogin(getLogin().toEntity());
        }
        if (getNeighborhood() != null) {
            residentEntity.setNeighborhood(getNeighborhood().toEntity());
        }

        return residentEntity;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param Id the id to set
     */
    public void setId(Long Id) {
        this.id = Id;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the preferences
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * @param preferences the preferences to set
     */
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    /**
     * @return the login
     */
    public ResidentLoginDTO getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(ResidentLoginDTO login) {
        this.login = login;
    }

    /**
     * @return the neighborhood
     */
    public NeighborhoodDTO getNeighborhood() {
        return neighborhood;
    }

    /**
     * @param neighborhood the neighborhood to set
     */
    public void setNeighborhood(NeighborhoodDTO neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * @return the proofOfResidence
     */
    public String getProofOfResidence() {
        return proofOfResidence;
    }

    /**
     * @param proofOfResidence the proofOfResidence to set
     */
    public void setProofOfResidence(String proofOfResidence) {
        this.proofOfResidence = proofOfResidence;
    }

    /**
     * Returns the string representation of the Business object.
     *
     * @return the object string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * @return the profilePicture
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * @param profilePicture the profilePicture to set
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * @return the muralPicture
     */
    public String getMuralPicture() {
        return muralPicture;
    }

    /**
     * @param muralPicture the muralPicture to set
     */
    public void setMuralPicture(String muralPicture) {
        this.muralPicture = muralPicture;
    }

    /**
     * @return the livingSince
     */
    public String getLivingSince() {
        return livingSince;
    }

    /**
     * @param livingSince the livingSince to set
     */
    public void setLivingSince(String livingSince) {
        this.livingSince = livingSince;
    }

    /**
     * @return the birthDate
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the joinDate
     */
    public Date getJoinDate() {
        return joinDate;
    }

    /**
     * @param joinDate the joinDate to set
     */
       public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }


}
