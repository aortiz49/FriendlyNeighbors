/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.BusinessEntity;

/**
 *
 * @author estudiante
 */
class BusinessDTO {

    BusinessDTO(BusinessEntity business) {
    }

    BusinessEntity toEntity() {
        return new BusinessEntity();
    }
    
}
