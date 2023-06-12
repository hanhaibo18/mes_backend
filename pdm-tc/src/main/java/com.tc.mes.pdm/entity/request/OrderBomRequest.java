package com.tc.mes.pdm.entity.request;

import lombok.Data;

/**
 *  pdm 订单bom
 */
@Data
public class OrderBomRequest {

    /**
     *  工作号
     */
    private String work_no;
    /**
     *  父项目id
     */
    private String parent_item_id;
    /**
     *  父项目名字
     */
    private String parent_name;
    /**
     *  父版本id
     */
    private String parent_rev_id;
    /**
     *  父订单id
     */
    private String parent_order_id;
    /**
     *  子项目id
     */
    private String child_item_id;
    /**
     *  子项目名字
     */
    private String child_name;
    /**
     *  子版本id
     */
    private String child_rev_id;
    /**
     *  子订单id
     */
    private String child_order_id;
    /**
     *  子数量
     */
    private String child_num;
    /**
     *  生产用途
     */
    private String production_spe;
    /**
     *  技术描述
     */
    private String tech_desc;

}
