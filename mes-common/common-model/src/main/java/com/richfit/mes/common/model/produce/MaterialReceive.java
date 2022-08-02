package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 14:57
 */
@Data
public class MaterialReceive extends BaseEntity<MaterialReceive> {


    /**
     * 跟单号
     */
    @TableField(exist = false)
    private String trackNo;

    /**
     * 申请单号
     */
    private String aplyNum;

    /**
     * 配送单号
     */
    private String delieveryNo;

    /**
     * 出库时间
     */
    private String outboundDate;



    @TableField(exist = false)
    private List<MaterialReceiveDetail> receiveDetails;

}
