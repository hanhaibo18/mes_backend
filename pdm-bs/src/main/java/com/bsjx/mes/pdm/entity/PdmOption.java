package com.bsjx.mes.pdm.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "base_i_pdm_option")
public class PdmOption {
    //工序
    @Id
    private String id;
    //工艺规程
    private String processId;
    //工序版本
    private String rev;
    //工序序号
    private String opNo;
    //工序类型
    private String type;
    //是否有工序图
    private String drawing;
    //工序名称
    private String name;
    //工序内容
    private String content;
    //关/重/试件
    private String gzs;
    //备注
    private String remark;

    @Column(name = "datagroup")
    private String dataGroup;
}
