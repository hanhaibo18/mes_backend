package com.richfit.mes.produce.entity.quality;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DisqualificationItemVO.java
 * @Author: Hou XinYu
 * @Description: 不合格品工序参数
 * @CreateTime: 2022年10月13日 14:48:00
 */
@Data

public class DisqualificationItemVo {

    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "申请单编号", dataType = "String")
    private String processSheetNo;
    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;


    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String partName;

    /**
     * 零部件材料
     */
    @ApiModelProperty(value = "零部件材料", dataType = "String")
    private String partMaterials;

    /**
     * 零部件图号
     */
    @ApiModelProperty(value = "零部件图号", dataType = "String")
    private String partDrawingNo;

    /**
     * 跟单号
     */
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;

    /**
     * 不合格数量
     */
    @ApiModelProperty(value = "不合格数量", dataType = "String")
    private Integer disqualificationNum;

    private String classes;
}
