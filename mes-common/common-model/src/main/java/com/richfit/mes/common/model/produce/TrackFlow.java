package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 跟单
 */
@Data
@Accessors(chain = true)
@TableName("produce_track_head_flow")
public class TrackFlow extends BaseEntity<TrackFlow> {

    private static final long serialVersionUID = -1044825101675722165L;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 状态 0已生成待派工 1在制 2完工 3作废 4删除
     */
    private String status;

    /**
     * 跟单完工时间
     */
    private Date completeTime;

    /**
     * 描述: 完工资料：Y是
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/21 10:25
     **/
    private String isCompletionData;

    /**
     * 组织机构编号
     */
    private String branchCode;

    /**
     * 跟单id
     */
    private String trackHeadId;
}
