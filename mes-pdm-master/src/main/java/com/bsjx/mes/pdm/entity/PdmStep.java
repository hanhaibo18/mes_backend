package com.bsjx.mes.pdm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "base_i_pdm_step")
public class PdmStep {
    //工步
    @Id
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String stepId;
    //工序
    private String opId;
    //工步序号
    private String stepNo;
    //工步名称
    private String name;
    //备注
    private String remark;
    @Column(name = "datagroup")
    private String dataGroup;
}
