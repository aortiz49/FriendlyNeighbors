/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author estudiante
 */
@Entity
public class SaleEntity extends BaseEntity implements Serializable{
    @PodamExclude
    @OneToOne
    private DashboardEntity dashboard;

    public DashboardEntity getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardEntity dashboard) {
        this.dashboard = dashboard;
    }
}