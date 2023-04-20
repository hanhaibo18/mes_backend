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

    private static final long serialVersionUID = 7458828222863575388L;

    @ApiModelProperty(value = "项目bomId",dataType = "String")
    private String projectBomId;
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
     * 级别
     */
    @ApiModelProperty(value = "级别", dataType = "String")
    private String grade;

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
    private String isKeyPart;

    /**
     * 跟踪方式
     */
    @ApiModelProperty(value = "跟踪方式", dataType = "int")
    private String trackType;


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

    /**
     * 是否齐套检查
     */
    @ApiModelProperty(value = "是否齐套检查", dataType = "String")
    private String isCheck;

    /**
     * 是否仓储领料
     */
    @ApiModelProperty(value = "是否仓储领料", dataType = "String")
    private String isNeedPicking;

    /**
     * 实物配送区分
     */
    @ApiModelProperty(value = "实物配送区分", dataType = "String")
    private String isEdgeStore;

    /**
     * 是否是编号来源
     */
    @ApiModelProperty(value = "是否是编号来源", dataType = "String")
    private String isNumFrom;

    /**
     * 跟单号
     */
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;

    /**
     * 配送数量
     */
    @TableField(exist = false)
    private int quantity;

    /**
     * 申请数量
     */
    @TableField(exist = false)
    private int orderQuantity;

    /**
     * 缺件
     */
    @TableField(exist = false)
    private int lackQuantity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 产品类型
     */
    private String sourceType;

    @ApiModelProperty(value = "绑定的入库物料Id")
    private String lineStoreId;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否生成部件级别跟单")
    private String isTrackHead;

    @ApiModelProperty(value = "生产线id")
    private String flowId;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
}
