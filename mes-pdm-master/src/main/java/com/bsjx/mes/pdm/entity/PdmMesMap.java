package com.bsjx.mes.pdm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@Table(name = "base_i_pdm_mes_dn_map")
public class PdmMesMap {
    @Id
    private String id;

    //PDM图号
    private String pdmDrawNo;

    //名称
    private String pdmName;

    private String filterDrawNo;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PdmMesMap pdmMesMap = (PdmMesMap) o;
        return Objects.equals(pdmDrawNo, pdmMesMap.pdmDrawNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pdmDrawNo);
    }
}
