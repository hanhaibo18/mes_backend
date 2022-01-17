package com.bsjx.mes.pdm.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "base_i_pdm_task")
public class PdmTask {
    @Id
    private String taskId;
    //图号
    //private String drawNo;
    //pdm 图号
    private String pdmDrawNo;
    //是否获取工艺，0/1
    private String reqProcess;
    //是否获取图纸，0/1
    private String reqDraw;
    //是否获取bom，0/1
    private String reqBom;
    //状态，0/1,未开始/已完成
    private String status;
    //处理结果 0/1,失败/成功
    private String result;
    //开始时间
    private Date recordOn;
    //完成时间
    private Date changeOn;

    @Column(name = "datagroup")
    private String dataGroup;
    //发起人
    private String recordBy;
}
