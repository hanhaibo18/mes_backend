package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 模型库
 *
 * @author 张盘石
 * @since 2022-11-08
 */
@Data
public class HotModelStoreQueryVo extends BaseEntity<HotModelStoreQueryVo> {

    @ApiModelProperty(value = "主键 ", dataType = "String")
    private String id;

    @ApiModelProperty(value = "租户id ", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "模型名称 ", dataType = "String")
    private String modelName;

    @ApiModelProperty(value = "模型类型 ", dataType = "Integer")
    private Integer modelType;

    @ApiModelProperty(value = "模型数量(正常) ", dataType = "Integer")
    private Integer normalNum;

    @ApiModelProperty(value = "模型图号 ", dataType = "String")
    private String ModelDrawingNo;

    @ApiModelProperty(value = "货位号 ", dataType = "String")
    private String locationNo;

    @ApiModelProperty(value = "模型数量(报废) ", dataType = "String")
    private Integer scrapNum;

    @ApiModelProperty(value = "模型备注 ", dataType = "String")
    private String modelRemark;

    @ApiModelProperty(value = "备注 ", dataType = "String")
    private String remark;
    @ApiModelProperty(value = "版本号", dataType = "String")
    private String version;

    @ApiModelProperty(value = "创建者 ", dataType = "String")
    private String createBy;


    @ApiModelProperty(value = "创建日期 ", dataType = "String")
    private Date createTime;

    @ApiModelProperty(value = "更新者 ", dataType = "String")
    private String modifyBy;

    @ApiModelProperty(value = "更新日期 ", dataType = "String")
    private Date modifyTime;
    @ApiModelProperty(value = "页码 ", dataType = "String")
    private int page;
    @ApiModelProperty(value = "条数 ", dataType = "String")
    private int limit;
    @ApiModelProperty(value = "排序方式 ", dataType = "String")
    private String order;
    @ApiModelProperty(value = "排序列 ", dataType = "String")
    private String orderCol;


}

