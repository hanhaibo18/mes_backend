package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 装配绑定记录表
 */
@Data
public class TrackAssemblyBinding extends BaseEntity<TrackAssemblyBinding> {

    @ApiModelProperty(value = "assemblyId", dataType = "String")
    private String assemblyId;

    /**
     * 零部件图号
     */
    @ApiModelProperty(value = "pratDrawingNo", dataType = "String")
    private String partDrawingNo;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号", dataType = "String")
    private String number;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", dataType = "int")
    private Integer quantity;

    /**
     * 是否绑定
     */
    @ApiModelProperty(value = "是否绑定", dataType = "int")
    private Integer isBinding;

    /**
     * 是否绑定
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String itemId;

    private String trackNo;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;


}
