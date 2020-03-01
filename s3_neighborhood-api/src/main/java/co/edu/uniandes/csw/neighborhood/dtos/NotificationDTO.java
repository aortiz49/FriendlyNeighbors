/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.NotificationEntity;

/**
 *
 * @author andre
 */
class NotificationDTO {

    NotificationDTO(NotificationEntity entityNotification) {
    
    }

    NotificationEntity toEntity() {
         return new NotificationEntity();
    }
    
}
