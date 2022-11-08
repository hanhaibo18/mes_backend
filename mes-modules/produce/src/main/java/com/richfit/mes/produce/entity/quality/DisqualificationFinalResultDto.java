package com.richfit.mes.produce.entity.quality;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DisqualificationFinalResultDto.java
 * @Author: Hou XinYu
 * @Description: 最终结果Dto
 * @CreateTime: 2022年11月03日 17:58:00
 */
@Data
public class DisqualificationFinalResultDto {

    /**
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverBranch;

    /**
     * 发现工序
     */
    @ApiModelProperty(value = "发现工序")
    private String discoverItem;

    /**
     * 总重量
     */
    @ApiModelProperty(value = "总重量")
    private Double totalWeight;

    /**
     * 废品损失
     */
    @ApiModelProperty(value = "废品损失")
    private String abandonmentLoss;

    /**
     * 废品工时
     */
    @ApiModelProperty(value = "废品工时")
    private Double discardTime;

    /**
     * 回用工时
     */
    @ApiModelProperty(value = "回用工时")
    private Double reuseTime;

    /**
     * 让步接收数量
     */
    @ApiModelProperty(value = "让步接收数量")
    private Integer acceptDeviation;

    /**
     * 让步接受产品编号
     */
    @ApiModelProperty(value = "让步接受产品编号")
    private List<String> acceptDeviationNoList;

    /**
     * 返修合格数量
     */
    @ApiModelProperty(value = "返修合格数量")
    private Integer repairQualified;

    /**
     * 返修合格产品编号
     */
    @ApiModelProperty(value = "返修合格产品编号")
    private List<String> repairNoList;

    /**
     * 报废数量
     */
    @ApiModelProperty(value = "报废数量")
    private Integer scrap;

    /**
     * 报废产品编号
     */
    @ApiModelProperty(value = "报废产品编号")
    private List<String> scrapNoList;

    /**
     * 退货数量
     */
    @ApiModelProperty(value = "退货数量")
    private Integer salesReturn;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "所属租户")
    private String tenantId;

    @ApiModelProperty(value = "意见Id")
    private String opinionId;
}
