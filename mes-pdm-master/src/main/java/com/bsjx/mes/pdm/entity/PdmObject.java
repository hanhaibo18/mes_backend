package com.bsjx.mes.pdm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "base_i_pdm_object")
public class PdmObject {
    @Id
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String obId;
    //工序
    private String opId;
    //子件类型
    private String type;
    //子件
    private String id;
    //子件名称
    private String name;
    //子件版本
    private String rev;
    //子件数量
    private String quantity;

    @Column(name = "datagroup")
    private String dataGroup;
}
