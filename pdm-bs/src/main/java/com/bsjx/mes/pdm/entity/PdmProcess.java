package com.bsjx.mes.pdm.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "base_i_pdm_process")
public class PdmProcess {
    @Id
    private String drawIdGroup;
    private String id;
    private String rev;
    private String processUser;
    private String name;
    private String itemStatus;
    private String releaseTime;
    private String processType;
    private String blankType;
    private String substituteMat;
    private String tyItemId;
    private String reserve1;
    private String reserve2;
    private String reserve3;
    //图号
    private String drawNo;
    private Date sycTime;
    @Column(name = "datagroup")
    private String dataGroup;
}
