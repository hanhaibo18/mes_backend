package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * produce_track_assembly
 *
 * @author
 */
@Data
public class TrackAssembly extends BaseEntity<TrackAssembly> {

    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String name;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;

    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号", dataType = "String")
    private String materialNo;

    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String trackHeadId;

    /**
     * 需要安装数量
     */
    @ApiModelProperty(value = "需要安装数", dataType = "int")
    private Integer number;

    /**
     * 是否是关键件
     */
    @ApiModelProperty(value = "是否是关键件", dataType = "int")
    private Integer isKeyPart;

    /**
     * 跟踪方式
     */
    @ApiModelProperty(value = "跟踪方式", dataType = "int")
    private Integer trackType;

    /**
     * 编号来源
     */
    @ApiModelProperty(value = "编号来源", dataType = "String")
    private String sourceNumber;

    /**
     * 重量
     */
    @ApiModelProperty(value = "重量", dataType = "double")
    private Double weight;

    /**
     * 装配人
     */
    @ApiModelProperty(value = "装配人", dataType = "String")
    private String assemblyBy;

    /**
     * 装配时间
     */
    @ApiModelProperty(value = "装配时间", dataType = "Date")
    private Date assemblyTime;

    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备Id", dataType = "String")
    private String deviceId;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;

    /**
     * 上级设备编号? (是否还需要)
     */
    @ApiModelProperty(value = "上级设备编号", dataType = "String")
    private String mainCompNo;

    /**
     * 子设备编号?(是否还需要)
     */
    @ApiModelProperty(value = "子设备编号", dataType = "String")
    private String subCompNo;

    /**
     * 子设备图号
     */
    @ApiModelProperty(value = "子设备图号", dataType = "String")
    private String subCompDrawNo;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构", dataType = "String")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "所属租户", dataType = "String")
    private String tenantId;

    /**
     * 库存数量
     **/
    @TableField(exist = false)
    @ApiModelProperty(value = "库存数量", dataType = "int")
    private int numberInventory;

    /**
     * 已装数量
     **/
    @ApiModelProperty(value = "已装数量", dataType = "int")
    private int numberInstall;

    /**
     * 剩余安装数量
     **/
    @TableField(exist = false)
    @ApiModelProperty(value = "剩余安装数量", dataType = "int")
    private int numberRemaining;

    /**
     * 是否完成
     **/
    @TableField(exist = false)
    @ApiModelProperty(value = "是否完成", dataType = "int")
    private int isComplete;

    @TableField(exist = false)
    @ApiModelProperty(value = "绑定信息", dataType = "List<TrackAssemblyBinding>")
    List<TrackAssemblyBinding> assemblyBinding;
}
