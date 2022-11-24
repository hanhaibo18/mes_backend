package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: DisqualificationVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年10月24日 17:20:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionPowerVo{
    @ApiModelProperty(value = "委托单号", dataType = "String")
    private String orderNo;
    @ApiModelProperty(value = "委托单位", dataType = "String")
    private String inspectionDepart;
    @ApiModelProperty(value = "委托单状态", dataType = "String")
    private String status;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawNo;
    @ApiModelProperty(value = "样品名称", dataType = "String")
    private String sampleName;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    private String branchCode;
    private String order;
    private String orderCol;
    private int page;
    private int limit;
}
