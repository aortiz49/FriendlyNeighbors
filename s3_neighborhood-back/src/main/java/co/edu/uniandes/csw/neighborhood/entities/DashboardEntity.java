/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.vecindario.entities;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author Carlos Figueredo
 */
@Entity
public class DashboardEntity extends BaseEntity implements Serializable{
    
    private int id;

    public int getIdDashboard() {
        return id;
    }

    public void setIdDashboard(int id) {
        this.id = id;
    }

    public int getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(int totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    private int totalRevenue;
    
}
