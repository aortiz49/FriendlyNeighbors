package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.NeighborhoodEntity;

/**
 * @generated
 */
public class NeighborhoodDTO {

    private long id;

    public NeighborhoodDTO() {

    }

    public NeighborhoodDTO(NeighborhoodEntity neighborhood) {
        this.id = neighborhood.getId();

    }

    public NeighborhoodEntity toEntity() {
        NeighborhoodEntity n = new NeighborhoodEntity();
        n.setId(getId());
        return n;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    
}
