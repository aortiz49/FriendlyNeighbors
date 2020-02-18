/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author kromero1
 */
@Entity
public class ProductEntity extends BaseEntity implements Serializable {

    @PodamExclude
    @ManyToOne
    private OfferEntity offer;

    @PodamExclude
    @ManyToOne
    private DashboardEntity dashboard;

    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity buyer;

    private String description;

    private Double price;

    public DashboardEntity getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardEntity dashboard) {
        this.dashboard = dashboard;
    }

    public OfferEntity getOffer() {
        return offer;
    }

    public void setOffer(OfferEntity offer) {
        this.offer = offer;
    }

    public ResidentProfileEntity getBuyer() {
        return buyer;
    }

    public void setBuyer(ResidentProfileEntity buyer) {
        this.buyer = buyer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
