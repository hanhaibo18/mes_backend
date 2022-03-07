package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.model.produce.AssignPerson;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 派工表
 */
@Data
public class Assign extends BaseEntity<Assign> {


    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 跟单工序项ID
     */
    private String tiId;
    /**
     * 跟单ID
     */
    private String trackId;
       /**
     * 跟单编号
     */
    private String trackNo;
    /**
     * 派工用户ID
     */
    private String userId;
     /**
     * 派工用户ID
     */
    private String emplName;
     /**
     * 派工用户名称
     */
    private String siteId;
     /**
     * 派工工位名称
     */
    private String siteName;
     /**
     * 派工设备ID
     */
    private String deviceId;
     /**
     * 派工设备名称
     */
    private String deviceName;
    /**
     * 派工优先级  3=High、2=Medium、1=Normal、0=Low
     */
    private int priority;
    /**
     * 派工数量
     */
    private int qty;
      /**
     * 可报工数
     */
    private int availQty;
    
     /**
     * 派工状态
     */
    private int state;
    
      /**
     * 派工人
     */
    protected String assignBy;

    /**
     * 派工时间
     */
    protected Date assignTime;
    
      /**
     * 计划开始时间
     */
    protected Date startTime;
    
      /**
     * 计划结束时间
     */
    protected Date endTime;

    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private String trackType;
    @TableField(exist = false)
    private String trackQty;
    @TableField(exist = false)
    private String trackNo2;
    @TableField(exist = false)
    private String productNo;
    @TableField(exist = false)
    private String optName;
    @TableField(exist = false)
    private Integer optType;
    @TableField(exist = false)
    private Integer optSequence;
    @TableField(exist = false)
    private Integer technologySequence;
    @TableField(exist = false)
    private Integer optParallelType;
    @TableField(exist = false)
    private Integer sequenceOrderBy;
    @TableField(exist = false)
    private Double prepareEndHours;
    @TableField(exist = false)
    private Double singlePieceHours;

    @TableField(exist = false)
    private List<AssignPerson> assignPersons;

}
