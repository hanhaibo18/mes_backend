package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @Description 用于存储物料接收日志记录
 * @Author zhiqiang.lu
 * @Date 2022/12/28 09:25
 */
@Data
@TableName(value = "produce_material_receive_log")
public class MaterialReceiveLog extends BaseEntity<MaterialReceiveLog> {


    private static final long serialVersionUID = -5341873939574421458L;
    /**
     * 接收主数据条数
     */
    private Integer receivedNumber = 0;

    /**
     * 接收明细条数
     */
    private Integer receivedNumberDetail = 0;

    /**
     * 状态 0成功 1失败
     */
    private String state = "0";

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;
}
