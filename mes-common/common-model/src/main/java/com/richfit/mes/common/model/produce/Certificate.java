package com.richfit.mes.common.model.produce;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 合格证表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate extends BaseEntity<Certificate> {

    private static final long serialVersionUID = -5801277389681557358L;

    public static String CERTIFICATE_NO_NULL_MESSAGE = "合格证编号不能为空!";

    public static String CERTIFICATE_NO_EXIST_MESSAGE = "合格证编号已存在,不能重复!";

    public static String CERTIFICATE_HAS_BEEN_ISSUED = "合格证已开具,不能重复开具!";

    public static String TRACK_NO_NULL_MESSAGE = "请选择跟单!";

    public static String SUCCESS_MESSAGE = "操作成功！";

    public static String FAILED_MESSAGE = "操作失败！";

    public static String FAILED_ON_COMPLETE = "工序未完成，不允许开具合格证";
    //下工序车间、生产入库
    public static String NEXT_OPT_WORK_BOMCO_SC = "BOMCO_SC";

    //未推送工时
    public static String IS_SENG_WORK_HOUR_0 = "0";

    //已推送工时
    public static String IS_SENG_WORK_HOUR_1 = "1";

    //推送工时失败
    public static String IS_SENG_WORK_HOUR_2 = "2";

    //未交库
    public static String IS_DELIVERY_TO_WAREHOUSE_0 = "0";

    //已交库
    public static String IS_DELIVERY_TO_WAREHOUSE_1 = "1";

    //交库失败
    public static String IS_DELIVERY_TO_WAREHOUSE_2 = "2";

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "分公司")
    private String branchCode;

    @ApiModelProperty(value = "合格证编号")
    private String certificateNo;

    @ApiModelProperty(value = "类型 0工序合格证 1完工合格证")
    private String type;

    @ApiModelProperty(value = "下道工序车间")
    private String nextOptWork;

    @ApiModelProperty(value = "下工序")
    private String nextOpt;

    @ApiModelProperty(value = "检测员")
    private String checkName;

    @ApiModelProperty(value = "检测日期")
    private Date checkTime;

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品编号")
    private String productNoContinuous;

    @ApiModelProperty(value = "产品全部编号")
    private String productNoDesc;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "物料号")
    private String materialNo;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "工序顺序")
    private Integer optSequence;

    @ApiModelProperty(value = "工序号")
    private String optNo;

    @ApiModelProperty(value = "本工序")
    private String optName;

    @ApiModelProperty(value = "工序Id from跟单工序")
    @TableField(exist = false)
    private String optId;

    @ApiModelProperty(value = "工序版本 from跟单工序")
    @TableField(exist = false)
    private String optVer;

    @ApiModelProperty(value = "工序序号 from跟单工序")
    @TableField(exist = false)
    private String sequenceOrderBy;

    @ApiModelProperty(value = "工序列表 ")
    @TableField(exist = false)
    private List<TrackCertificate> trackCertificates;

    //以下字段2022-06-23 gaol 添加
    @ApiModelProperty(value = "合格证来源 0：开出合格证 1：接收合格证")
    private String certOrigin;

    @ApiModelProperty(value = "是否下车间已接收 0：未接收  1：已接收")
    private String isPush;

    //2022-07-26 gaol 添加
    @ApiModelProperty(value = "是否推送工时给ERP  0：未推送  1：已推送   2:失败")
    private String isSendWorkHour;
    @ApiModelProperty(value = "是否推送工时给ERP返回的信息")
    private String sendWorkHourMessage;

    @ApiModelProperty(value = "是否交库给WMS  0：未推送  1：已推送   2:失败")
    private String isDeliveryToWarehouse;

    @ApiModelProperty(value = "是否交库给WMS返回的信息")
    private String deliveryToWarehouseMessage;

    @ApiModelProperty(value = "试棒数量")
    private Integer testBarNumber;

    @ApiModelProperty(value = "试棒类型")
    private String testBarType;

    @ApiModelProperty(value = "炉批号")
    private String batchNo;

    @ApiModelProperty(value = "材质")
    private String texture;
    @ApiModelProperty(value = "待用材质")
    @TableField(exist = false)
    private String replaceMaterial;

    @ApiModelProperty(value = "跟单单重")
    private Float weight;

    @ApiModelProperty(value = "零件名称")
    private String materialName;

    @ApiModelProperty(value = "订单号")
    private String productionOrder;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "下车间需要完成的工序列表，逗号隔开")
    private String nextWorkOpts;

    @ApiModelProperty(value = "金额")
    private Double money;

    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;

    @ApiModelProperty(value = "本车间名称")
    @TableField(exist = false)
    private String branchCodeName;

    @ApiModelProperty(value = "下工序车间名称")
    @TableField(exist = false)
    private String nextOptWorkName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序产品编码")
    private String itemProductNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序产品数量")
    private String itemNumber;

    public Certificate(TrackHead trackHead, TrackItem item, TenantUserVo tenantUser, String workblankType, String certificateNo) {
        if ("1".equals(workblankType)) {
            this.nextOptWork = "BOMCO_RG_ZG";
        } else if ("2".equals(workblankType)) {
            this.nextOptWork = "BOMCO_SC";
        }
        this.optSequence = (item.getOptSequence());
        this.optNo = (item.getOptNo());
        this.optName = (item.getOptName());
        this.certOrigin = ("0");
        this.certificateNo = (certificateNo);
        this.checkName = (tenantUser.getEmplName());
        this.checkTime = (new Date());
        this.nextOpt = ("/");
        //未接收
        this.isPush = ("0");
        List<TrackCertificate> trackCertificates = new ArrayList<>();
        TrackCertificate trackCertificate = new TrackCertificate();
        trackCertificate.setThId(trackHead.getId());
        trackCertificates.add(trackCertificate);
        this.trackCertificates = (trackCertificates);
        this.type = ("1");
    }
}
