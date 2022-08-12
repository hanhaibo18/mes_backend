package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: assembleKittingVo.java
 * @Author: Hou XinYu
 * @Description: 装配报工齐套性检查
 * @CreateTime: 2022年07月22日 16:46:00
 */
@Data
public class AssembleKittingVo {
    /**
     * 图号
     **/
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    /**
     * 物料号
     **/
    @ApiModelProperty(value = "物料号", dataType = "String")
    private String materialNo;
    /**
     * 物料名称
     **/
    @ApiModelProperty(value = "物料名称", dataType = "String")
    private String materialName;
    /**
     * 需要数量
     **/
    @ApiModelProperty(value = "需要数量", dataType = "Integer")
    private Integer needNumber;
    /**
     * 安装数量
     **/
    @ApiModelProperty(value = "安装数量", dataType = "Integer")
    private Integer installNumber;
    /**
     * 线边库
     **/
    @ApiModelProperty(value = "线边库", dataType = "Integer")
    private Integer repertoryNumber;
    /**
     * 可配送数量
     **/
    @ApiModelProperty(value = "可配送数量", dataType = "Integer")
    private Integer deliverableQuantity;
    /**
     * 已领数量
     **/
    @ApiModelProperty(value = "已领数量", dataType = "Integer")
    private Integer acquireNumber;
    /**
     * 缺件数量
     **/
    @ApiModelProperty(value = "缺件数量", dataType = "Integer")
    private Integer shortQuantity;
    /**
     * 仓储领料
     **/
    @ApiModelProperty(value = "仓储领料", dataType = "String")
    private String isNeedPicking;
    /**
     * 实物配送
     **/
    @ApiModelProperty(value = "实物配送", dataType = "String")
    private String isEdgeStore;
    /**
     * 关键件
     **/
    @ApiModelProperty(value = "关键件", dataType = "String")
    private String isKeyPart;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成部件级别跟单")
    private String isTrackHead;
}
