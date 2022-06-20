package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单
 */
@Data
public class TrackHead extends BaseEntity<TrackHead> {

    private static final long serialVersionUID = 6336423092552908350L;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 跟单号
     */
    private String trackNo;

    /**
     * 跟单类型 0单件 1批次
     */
    private String trackType;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 物料编号
     */
    private String materialNo;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 生产订单编号
     */
    private String productionOrder;

    /**
     * 审批人
     */
    private String approvalBy;

    /**
     * 审批时间
     */
    private Date approvalTime;

    /**
     * 审批状态 0待审批 1通过 2打回
     */
    private String approvalStatus;

    /**
     * 状态 0已生成待派工 1在制 2完工 3作废 4删除
     */
    private String status;

    /**
     * 试棒数量
     */
    private Integer testBarNumber;

    /**
     * 试棒类型
     */
    private String testBarType;

    /**
     * 炉批号
     */
    private String batchNo;

    /**
     * 准备工时 0首件 1全部
     */
    private String isFirst;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 签发人
     */
    private String issueBy;

    /**
     * 签发时间
     */
    private Date issueTime;

    /**
     * 来料合格证号
     */
    private String materialCertificateNo;

    /**
     * 试棒跟单编号
     */
    private String testBarTrackNo;

    /**
     * 合格证号
     */
    private String certificateNo;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 计划号
     */
    private String workPlanNo;

    /**
     * 组织机构编号
     */
    private String branchCode;

    /**
     * 工序版本号
     */
    private String routerVer;

    /**
     * 跟单完工时间
     */
    private Date completeTime;

    @TableField(exist = false)
    private List<TrackItem> trackItems;

    private String userMaterialNo;

    @TableField(exist = false)
    private String optName;

    @TableField(exist = false)
    private String optId;

    @TableField(exist = false)
    private String tiId;

    private String userProductNo;

    private Integer startNo;
    private Integer endNo;
    private String suffixNo;

    private String materialName;
    private String productName;
    private String replaceMaterial;
    private Float weight;
    private String texture;

    private String templateCode;

    @TableField(exist = false)
    private String selectedMaterialNo;
    /**
     * 描述: 计划Id
     *
     * @Author: xinYu.hou
     * @Date: 2022/4/19 10:25
     **/
    private String workPlanId;
    /**
     * 描述: 工艺id
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/13 10:25
     **/
    private String routerId;
    /**
     * 描述: 跟单类型 0库存生成  1物料生成  2计划生成
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/13 10:25
     **/
    private String type;

    /**
     * 描述: 是否试棒跟单  0否  1是
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/16 10:25
     **/
    private String isTestBar;

}
