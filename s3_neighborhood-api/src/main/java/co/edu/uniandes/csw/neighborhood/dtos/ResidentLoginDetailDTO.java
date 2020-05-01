package co.edu.uniandes.csw.neighborhood.dtos;

/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import co.edu.uniandes.csw.neighborhood.entities.ResidentLoginEntity;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class that represents the detail of Login.
 * 
 * @author aortiz49
 */
public class ResidentLoginDetailDTO extends ResidentLoginDTO implements Serializable {

    private ResidentProfileDTO resident;

    public ResidentLoginDetailDTO() {
        super();

    }

 
    public ResidentLoginDetailDTO(ResidentLoginEntity loginEntity) {
        super(loginEntity);
        if (loginEntity.getResident()!= null) {
            this.resident = new ResidentProfileDTO(loginEntity.getResident());
        }
    }


    @Override
    public ResidentLoginEntity toEntity() {
        ResidentLoginEntity entity = super.toEntity();
        if (getResident() != null) {
            entity.setResident(getResident().toEntity());
        }
        return entity;
    }


    public ResidentProfileDTO getResident() {
        return resident;
    }

    public void setResident(ResidentProfileDTO resident) {
        this.resident = resident;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
