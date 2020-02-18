/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;
import co.edu.uniandes.csw.neighborhood.entities.EventEntity;
import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence;
import javax.ejb.Stateless;
import javax.inject.Inject;
/**
 *
 * @author kromero1
 */
@Stateless
public class EventLogic {
    
    
    
    @Inject
    private EventPersistence persistence;
    
    public EventEntity createEvent(EventEntity event){
        persistence.create(event);
        return event;
    }
        
    }

