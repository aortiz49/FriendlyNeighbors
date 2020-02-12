/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author Carlos Figueredo
 */
@Entity
public class LoginEntity extends BaseEntity implements Serializable {
    
    private int id;
    
    private String userName;
    
    private String password;
    
    private String rol;

    
    public int getIdLogin(){
        return id;
    }
    
public void setIdLogin(int pId){
        id = pId;
    }
    
    public String getUserName(){
        return userName;
    }
    
    public void setUserName(String pUserName){
        userName = pUserName;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String pPassword){
        password = pPassword;
    }
    
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
 
    
}
