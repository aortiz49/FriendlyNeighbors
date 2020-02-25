/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.test.logic;

import co.edu.uniandes.csw.neighborhood.ejb.LocationLogic;
import co.edu.uniandes.csw.neighborhood.ejb.LocationNeighborhoodLogic;
import co.edu.uniandes.csw.neighborhood.entities.LocationEntity;
import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;
import co.edu.uniandes.csw.neighborhood.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.neighborhood.persistence.NeighborhoodPersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author v.cardonac1
 */
@RunWith(Arquillian.class)
public class LocationNeighborhoodLogicTest {
    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private LocationNeighborhoodLogic locationNeighborhoodLogic;
    
    @Inject
    private LocationLogic locationLogic;
    
    @Inject
    private NeighborhoodPersistence neighPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;
    
    private NeighborhoodEntity neighborhood = new NeighborhoodEntity();
    private List<LocationEntity> data = new ArrayList<>();
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(NeighborhoodEntity.class.getPackage())
                .addPackage(LocationEntity.class.getPackage())
                .addPackage(LocationLogic.class.getPackage())
                .addPackage(NeighborhoodPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }
    
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private void clearData() {
        em.createQuery("delete from NeighborhoodEntity").executeUpdate();
        em.createQuery("delete from LocationEntity").executeUpdate();
    }

    private void insertData() {
        neighborhood = factory.manufacturePojo(NeighborhoodEntity.class);
        neighborhood.setId(1L);
        neighborhood.setPlaces(new ArrayList<>());
        em.persist(neighborhood);

        for (int i = 0; i < 3; i++) {
            LocationEntity entity = factory.manufacturePojo(LocationEntity.class);
               
            entity.setNeighborhood(neighborhood);
            em.persist(entity);
            data.add(entity);
            neighborhood.getPlaces().add(entity);
        }
    }
    /**
    @Test
    public void addLocationTest() throws BusinessLogicException{
        LocationEntity newLocation = factory.manufacturePojo(LocationEntity.class);
        
        locationLogic.createLocation(newLocation);
        
        LocationEntity locationEntity = locationNeighborhoodLogic.associateLocationToNeighborhood(newLocation.getId(), neighborhood.getId());
        Assert.assertNotNull(locationEntity);
        
        Assert.assertEquals(locationEntity.getId(), newLocation.getId());
        Assert.assertEquals(locationEntity.getAddress(), newLocation.getAddress());
        
        LocationEntity lastLocation = locationNeighborhoodLogic.getLocation(newLocation.getId(), neighborhood.getId());
        Assert.assertEquals(lastLocation.getId(), newLocation.getId());
    }
    */
    
    @Test
    public void getLocationsTest(){
        List<LocationEntity> locationEntities = locationNeighborhoodLogic.getLocations(neighborhood.getId());
        
        Assert.assertEquals(data.size(), locationEntities.size());
        
        for( int i = 0; i < data.size(); i++){
            Assert.assertTrue(locationEntities.contains(data.get(0)));
        }
    }
    
    /**
    @Test
    public void getLocationTest() throws BusinessLogicException{
        LocationEntity locationEntity = data.get(0);
        LocationEntity location = locationNeighborhoodLogic.getLocation(locationEntity.getId(), neighborhood.getId());
        Assert.assertNotNull(location);
        
        Assert.assertEquals(locationEntity.getId(), location.getId());
        Assert.assertEquals(locationEntity.getAddress(), location.getAddress());
    }
    */
    /**
    @Test
    public void replaceLocationsTest() throws BusinessLogicException{
        List<LocationEntity> newCollection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LocationEntity entity = factory.manufacturePojo(LocationEntity.class);
                   
            locationLogic.createLocation(entity);
            entity.setNeighborhood(neighborhood);
            newCollection.add(entity);
        }
        locationNeighborhoodLogic.replaceLocations(newCollection, neighborhood.getId());
        List<LocationEntity> locationsEntities = locationNeighborhoodLogic.getLocations(neighborhood.getId());
        for (LocationEntity aNuevaLista : newCollection) {
            Assert.assertTrue(locationsEntities.contains(aNuevaLista));
        }
    }
    */
    @Test
    public void removeLocationTest(){
        for(LocationEntity location : data){
            locationNeighborhoodLogic.removeLocation(location.getId(), neighborhood.getId());
        }
        Assert.assertTrue(locationNeighborhoodLogic.getLocations(neighborhood.getId()).isEmpty());
    }
}
