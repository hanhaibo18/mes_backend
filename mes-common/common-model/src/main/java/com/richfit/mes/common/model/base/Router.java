package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Router extends BaseEntity<Router> {

    private static final long serialVersionUID = -3275178843268370454L;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 工艺图号
     */
    private String routerNo;

    /**
     * 工艺名称
     */
    private String routerName;

    /**
     * 工艺类型
     */
    private String routerType;

    /**
     * 工艺描述
     */
    private String remark;

    /**
     * 工艺版本号
     */
    private String version;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态 0=未激活 1=激活 2=历史
     */
    private String status;

    /**
     * 是否是当前工艺
     */
    private String isActive;

    /**
     * 物料号，必填
     */
    private String materialNo;
    /**
     * 物料号，必填
     */
    private String materialVersion;

    /**
     * G6流程节点
     */
    private String flow;

    /**
     * 图号
     */
    private String drawNo;

    /**
     * 是否推送ERP 1已推送
     */
    private Integer isSendErp;

    /**
     * pdm 工艺id
     */
    private String pdmDrawIdGroup;

    /**
     * 年度章
     */
    private String sealYear;

    /**
     * 钢水重量
     */
    private String weightMolten;
    /**
     * 工艺保温时间
     */
    private String processHoldTime;
    /**
     * 浇筑温度
     */
    private String pourTemp;
    /**
     * 浇筑速度
     */
    private String pourSpeed;
    /**
     * 毛坯调质
     */
    private String blankControl;
    /**
     * 毛坯探伤
     */
    private String blankInspect;
    /**
     * 锻件重量
     */
    private String forgWeight;
    /**
     * 下料重量
     */
    private String blankWeight;
    /**
     * 零件重量
     */
    private String partWeight;
    /**
     * 下料规格
     */
    private String blankSpecifi;
    /**
     * 下料长度
     */
    private String blankLenght;
    /**
     * 锻始温度
     */
    private String forgTempStart;
    /**
     * 锻终温度
     */
    private String forgTempEnd;
    /**
     * 加热时间
     */
    private String tempTime;
    /**
     * 段后处理
     */
    private String forgStand;
    /**
     * 零件名称
     */
    private String partName;
    /**
     * 图纸版本
     */
    private String drawVer;
    /**
     * 材质
     */
    private String texture;
    /**
     * 试棒
     */
    private String testBar;
    /**
     * 单重
     */
    private String pieceWeight;



    @ApiModelProperty(value = "该工艺历史版本")
    @TableField(exist = false)
    private List<Router> children;

    @TableField(exist = false)
    private String productMaterialNo;

    @TableField(exist = false)
    private String unit;
}
