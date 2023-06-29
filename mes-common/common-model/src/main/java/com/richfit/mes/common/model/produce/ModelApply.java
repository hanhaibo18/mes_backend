package com.richfit.mes.common.model.produce;

import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * (ModelApply)表实体类
 *
 * @author makejava
 * @since 2023-04-23 14:45:46
 */
@Data
public class ModelApply extends BaseEntity<ModelApply> {
    //租户ID
    private String tenantId;
    //模型id
    private String modelId;
    //车间编码
    private String branchCode;
    //模型名称
    private String modelName;
    //模型图号
    private String modelDrawingNo;
    //申请数量
    private Integer applyNum;
    //派送数量
    private Integer assignNum;
    //模型类型（0：一次性(气化膜)，1：重复性(木制)）
    private Integer modelType;
    //模型版本号
    private String modelVersion;
    //申请状态 0未配送 1已配送 2已退库
    private Integer applyStatus;
    //申请时间
    private Date applyTime;
    //配送时间
    private Date deliveryTime;
    //工序id
    private String itemId;
    //计划完成时间
    private Date planFinishTime;
    //退库时间
    private Date backTime;
}