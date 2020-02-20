/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

import co.edu.uniandes.csw.neighborhood.persistence.EventPersistence; 
import co.edu.uniandes.csw.neighborhood.entities.EventEntity; 
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException; 


/**
 *
 * @author kromero1
 */
@Stateless
public class EventLogic {


    private static final Logger LOGGER = Logger.getLogger(EventLogic.class.getName());

    @Inject
    private EventPersistence persistence;


      public EventEntity createEvent(EventEntity eventEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Creation process for event has started");

        
        if(eventEntity.getTitle()== null){
            throw new BusinessLogicException("A Title has to be specified");
        }

         
        if(eventEntity.getLocation()== null){
            throw new BusinessLogicException("A venue has to be specified");
        }
        if(Integer.parseInt(eventEntity.getStartTime())> Integer.parseInt(eventEntity.getEndTime())){
            throw new BusinessLogicException("cannot finish before starting the event");
        }

        persistence.create(eventEntity);
        LOGGER.log(Level.INFO, "Creation process for event eneded");

        return eventEntity;
    }



    public void deleteEvent(Long id) {

        LOGGER.log(Level.INFO, "Starting deleting process for event with id = {0}", id);
        persistence.delete(id);
        LOGGER.log(Level.INFO, "Ended deleting process for event with id = {0}", id);
    }


    public List<EventEntity> getEvents() {

        LOGGER.log(Level.INFO, "Starting querying process for all events");
        List<EventEntity> residents = persistence.findAll();
        LOGGER.log(Level.INFO, "Ended querying process for all events");
        return residents;
    }

    public EventEntity getEvent(Long id) {
        LOGGER.log(Level.INFO, "Starting querying process for event with id ", id);
        EventEntity resident = persistence.find(id);
        LOGGER.log(Level.INFO, "Ended querying process for  event with id", id);
        return resident;
    }




    public EventEntity updateEvent(EventEntity eventEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Starting update process for event with id ", eventEntity.getId());

        EventEntity original  = persistence.find(eventEntity.getId());

         
        if(eventEntity.getTitle()== null){
            throw new BusinessLogicException("A text has to be specified");
        }

         //must have a date
        if(eventEntity.getLocation()== null){
            throw new BusinessLogicException("A venue has to be specified");
        }
        
        if(Integer.parseInt(eventEntity.getStartTime())> Integer.parseInt(eventEntity.getEndTime())){
            throw new BusinessLogicException("cannot finish before starting the event");
        }

        EventEntity modified = persistence.update(eventEntity);
        LOGGER.log(Level.INFO, "Ended update process for event with id ", eventEntity.getId());
        return modified;
    }




}