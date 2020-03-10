package co.edu.uniandes.csw.neighborhood.dtos;

import co.edu.uniandes.csw.neighborhood.entities.FavorEntity;
import co.edu.uniandes.csw.neighborhood.entities.ResidentProfileEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
* @author v.cardonac1
*/

public class FavorDetailDTO extends FavorDTO implements Serializable{


//===================================================
// Attributes
//===================================================

    private List<ResidentProfileDTO> candidates = new ArrayList<>();

    public FavorDetailDTO(){
        super();
    }
    
    public FavorDetailDTO(FavorEntity entityFavor) {
        super(entityFavor);
        
        if(entityFavor != null){
            candidates = new ArrayList<>();
            
            for(ResidentProfileEntity entityResident : entityFavor.getCandidates()){
                candidates.add(new ResidentProfileDTO(entityResident));
            }
        }
    }
    
    public FavorEntity toEntity(){
        FavorEntity favorEntity = super.toEntity();
        
        if(getCandidates() != null){
            List<ResidentProfileEntity> candidatesE = new ArrayList<>();
            for(ResidentProfileDTO dtoCandidate: getCandidates()){
                candidatesE.add(dtoCandidate.toEntity());
            }
            favorEntity.setCandidates(candidatesE);
        }
        return favorEntity;
    }
    
    /**
     * @return the candidates
     */
    public List<ResidentProfileDTO> getCandidates() {
        return candidates;
    }

    /**
     * @param candidates the candidates to set
     */
    public void setCandidates(List<ResidentProfileDTO> candidates) {
        this.candidates = candidates;
    }
    
   
    
}
