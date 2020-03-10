/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
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

    public ResidentProfileDTO() {
    }

    /**
     * Creates a resident DTO from a resident entity.
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
            this.login = new ResidentLoginDTO(residentEntity.getLogin());
            this.neighborhood = new NeighborhoodDTO(residentEntity.getNeighborhood());
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

        if (login != null) {
            residentEntity.setLogin(getLogin().toEntity());
        }
        if (neighborhood != null) {
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

}
