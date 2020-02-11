/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.vecindario.persistence;

import co.edu.uniandes.csw.vecindario.entities.DashboardEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
/**
 *
 * @author Carlos Figueredo
 */
@Stateless
public class DashboardPersistence {
    
    private static final Logger LOGGER = Logger.getLogger(DashboardPersistence.class.getName());

    @PersistenceContext(unitName = "FriendlyNeighborsPU" )
    protected EntityManager em;
    
    /**
     * Creates a dashboard within DB
     *
     * @param dashboard dashboard object to be created in DB
     * @return returns the created entity with an id given by DB.
     */
    public DashboardEntity create(DashboardEntity dashboard){
        
        LOGGER.log(Level.INFO, "Creating a new Dashboard");

        em.persist(dashboard);
        LOGGER.log(Level.INFO, "Dashboard created");
        return dashboard;
    }
    
    /**
     * Returns all logins from DB.
     *
     * @return a list with all logins found in DB.
     */
    public List<DashboardEntity> findAll() {
        LOGGER.log(Level.INFO, "Querying for all dashboards");
        
        TypedQuery query = em.createQuery("select u from DashboardEntity u", DashboardEntity.class);
       
        return query.getResultList();
    }
    /**
     * Looks for a dashboard with the id given by argument
     *
     * @param dashboardId: id from dashboard to be found.
     * @return a dashboard.
     */
     public DashboardEntity find(Long dashboardId) {
        LOGGER.log(Level.INFO, "Querying for dashboard with id={0}", dashboardId);
       
        
        return em.find(DashboardEntity.class, dashboardId);
    }
     
    /**
     * Updates a dashboard with the modified dashboard given by argument.
     *
     * @param dashboardEntity: the modified dashboard. Por
     * @return the updated dashboard
     */
    public DashboardEntity update(DashboardEntity dashboardEntity) {
        LOGGER.log(Level.INFO, "Updating dashboard with id={0}", dashboardEntity.getId());
        return em.merge(dashboardEntity);
    }
    
    /**
     * Deletes from DB a dashboard with the id given by argument
     *
     * @param dashboardId: id from dashboard to be deleted.
     */
    public void delete(Long dashboardId) {

        LOGGER.log(Level.INFO, "Deleting dashboard wit id={0}", dashboardId);
        DashboardEntity dashboardEntity = em.find(DashboardEntity.class, dashboardId);
        em.remove(dashboardEntity);
    }
}
