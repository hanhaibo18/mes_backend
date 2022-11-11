package com.richfit.mes.common.model.produce;

import java.io.Serializable;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_hot_forging_store
 * @author 
 */
@Data
public class HotForgingStore extends BaseEntity<HotForgingStore> implements Serializable {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 仿型名称
     */
    @ApiModelProperty(value = "仿型名称",required = true)
    private String profilingName;

    /**
     * 仿型图号
     */
    @ApiModelProperty(value = "仿型图号" ,required = true)
    private String profilingDrawingNo;

    /**
     * 所属类型（0：轴类，1：饼类，2：圈类，3：套类，4：四方类）
     */
    @ApiModelProperty(value = "所属类型（0：轴类，1：饼类，2：圈类，3：套类，4：四方类）",required = true)
    private int profilingType;

    /**
     * 简图ID，多个用，隔开
     */
    @ApiModelProperty(value = "简图ID，多个用，隔开",required = true)
    private String filesId;



    private static final long serialVersionUID = 1L;
}