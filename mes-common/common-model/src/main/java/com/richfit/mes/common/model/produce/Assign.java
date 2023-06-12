package com.richfit.mes.common.model.produce;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 派工表
 */
@Data
public class Assign extends BaseEntity<Assign> {

    private static final long serialVersionUID = 3788961298306032177L;
    @ApiModelProperty(value = "id", dataType = "String")
    private String id;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;
    /**
     * 跟单工序项ID
     */
    @ApiModelProperty(value = "跟单工序项ID", dataType = "String")
    private String tiId;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String trackId;
    /**
     * 跟单编号
     */
    @ApiModelProperty(value = "跟单编号", dataType = "String")
    private String trackNo;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String emplName;
    /**
     * 派工工位名称
     */
    @ApiModelProperty(value = "派工工位名称", dataType = "String")
    private String siteId;
    /**
     * 派工工位名称
     */
    @ApiModelProperty(value = "派工工位名称", dataType = "String")
    private String siteName;
    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "派工设备ID", dataType = "String")
    private String deviceId;
    /**
     * 派工设备名称
     */
    @ApiModelProperty(value = "派工设备名称", dataType = "String")
    private String deviceName;

    @ApiModelProperty(value = "派工设备code", dataType = "String")
    private String deviceCode;
    /**
     * 派工优先级  3=High、2=Medium、1=Normal、0=Low
     */
    @ApiModelProperty(value = "派工优先级  3=High、2=Medium、1=Normal、0=Low", dataType = "int")
    private int priority;
    /**
     * 派工数量
     */
    @ApiModelProperty(value = "派工数量", dataType = "int")
    private int qty;
    /**
     * 可报工数
     */
    @ApiModelProperty(value = "可报工数", dataType = "int")
    private int availQty;

    /**
     * 派工状态
     */
    @ApiModelProperty(value = "派工状态(0=未开工,1=以开工,2=已完成)", dataType = "int")
    private int state;

    /**
     * 派工人
     */
    @ApiModelProperty(value = "派工人", dataType = "String")
    protected String assignBy;

    /**
     * 派工人名称
     */
    @ApiModelProperty(value = "派工人名称", dataType = "String")
    protected String assignName;

    /**
     * 派工时间
     */
    @ApiModelProperty(value = "派工时间", dataType = "Date")
    protected Date assignTime;

    /**
     * 计划开始时间
     */
    @ApiModelProperty(value = "计划开始时间", dataType = "Date")
    protected Date startTime;

    /**
     * 计划结束时间
     */
    @ApiModelProperty(value = "计划结束时间", dataType = "Date")
    protected Date endTime;

    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private String trackType;
    @TableField(exist = false)
    private String trackQty;
    @TableField(exist = false)
    private String trackNo2;
    @TableField(exist = false)
    private String productNo;
    @TableField(exist = false)
    private String optName;
    @TableField(exist = false)
    private String optVer;
    @TableField(exist = false)
    private String optType;
    @TableField(exist = false)
    private String optId;
    @TableField(exist = false)
    private String optNo;
    @TableField(exist = false)
    private String assignableQty;
    @TableField(exist = false)
    private Integer optSequence;
    @TableField(exist = false)
    private String technologySequence;
    @TableField(exist = false)
    private Integer optParallelType;
    @TableField(exist = false)
    private Integer sequenceOrderBy;
    @TableField(exist = false)
    private Double prepareEndHours;
    @TableField(exist = false)
    private Double singlePieceHours;

    @TableField(exist = false)
    private List<AssignPerson> assignPersons;


    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty(value = "产品编码全", dataType = "String")
    private String productNoDesc;
    @TableField(exist = false)
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String partsName;
    @TableField(exist = false)
    @ApiModelProperty(value = "总数量", dataType = "String")
    private Integer totalQuantity;
    @TableField(exist = false)
    @ApiModelProperty(value = "可派工数量", dataType = "String")
    private Integer dispatchingNumber;
    @TableField(exist = false)
    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;
    @TableField(exist = false)
    @ApiModelProperty(value = "锻件重量", dataType = "String")
    private String forgWeight;
    @TableField(exist = false)
    @ApiModelProperty(value = "单重", dataType = "String")
    private String pieceWeight;
    @TableField(exist = false)
    @ApiModelProperty(value = "钢水重量", dataType = "String")
    private String weightMolten;
    @TableField(exist = false)
    @ApiModelProperty(value = "总数量", dataType = "Float")
    private Integer number;
    @TableField(exist = false)
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "计划号", dataType = "String")
    private String workPlanNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "工艺Id", dataType = "String")
    private String routerId;

    @ApiModelProperty(value = "跟单分类：1机加  2装配 3热处理 4钢结构 ", dataType = "String")
    private String classes;

    @ApiModelProperty(value = "分流Id")
    private String flowId;

    @ApiModelProperty(value = "保温结束时间")
    @TableField(exist = false)
    private String holdFinishedTime;
    @ApiModelProperty(value = "浇注时间", dataType = "Date")
    @TableField(exist = false)
    private String pourTime;
    /**
     * 探伤委托单id
     */
    @ApiModelProperty(value = "探伤委托单id", dataType = "String")
    private String powerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "探伤工序标识")
    private String isFlawDetection;

    /**
     * 预装炉（起）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "来料日期")
    private Date inTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    @TableField(exist = false)
    @ApiModelProperty(value = "试棒数量")
    private Integer testBarNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "毛胚编码", dataType = "String")
    private String workblankNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备分类编码（热工）", dataType = "String")
    private String typeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备分类名称（热工）", dataType = "String")
    private String typeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "温度下限℃（热工）", dataType = "String")
    private String tempDown;

    @TableField(exist = false)
    @ApiModelProperty(value = "温度上限℃（热工）", dataType = "String")
    private String tempUp;

    @TableField(exist = false)
    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;

    @TableField(exist = false)
    @ApiModelProperty(value = "保温时间h（热工）", dataType = "String")
    private String holdTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "冷却方式（热工）", dataType = "String")
    private String coolType;

    @TableField(exist = false)
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private String prechargeFurnaceId;

    @TableField(exist = false)
    @ApiModelProperty(value = "工艺信息")
    private String routerInfo;
    /**
     * 预装炉（止）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "派工班组list")
    private List<String> siteList;

    @TableField(exist = false)
    @ApiModelProperty(value = "质检人")
    private String qualityCheckBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否质检")
    private String isExistQualityCheck;


    /**
     * 下料规格
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "下料规格", dataType = "String")
    private String blankSpecifi;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否更新", dataType = "Integer")
    private Integer isUpdate;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否当前工序 1是  0 否")
    private Integer isCurrent;

    /**
     * 下料重量
     */

    @TableField(exist = false)
    private String blankWeight;

    /**
     * 锻始温度
     */

    @TableField(exist = false)
    private String forgTempStart;

    /**
     * 锻终温度
     */

    @TableField(exist = false)
    private String forgTempEnd;

    public String getUserId() {
        return userId;
    }

    public String getSiteId() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!ObjectUtil.isEmpty(this.siteList)) {
            for (String siteId : this.siteList) {
                if (!StringUtils.isEmpty(String.valueOf(stringBuilder))) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(siteId);
            }
            siteId = String.valueOf(stringBuilder);
        }
        return siteId;
    }

    public List<String> getSiteList() {
        if (!StringUtils.isEmpty(siteId) && (ObjectUtil.isEmpty(siteList) || siteList.size() == 0)) {
            return Arrays.asList(siteId.split(","));
        }
        return siteList;
    }
}
