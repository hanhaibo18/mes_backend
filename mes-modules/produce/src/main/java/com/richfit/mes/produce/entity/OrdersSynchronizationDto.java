package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: OrdersSynchronizationDao.java
 * @Author: Hou XinYu
 * @Description: 订单同步传入参数
 * @CreateTime: 2022年01月06日 15:16:00
 */
@Data
public class OrdersSynchronizationDto {
    /**
     * 工厂代码
     */
    private String code;
    /**
     * 日期
     */
    private String date;
    /**
     * 控制者
     */
    private String inChargeOrg;
    /**
     * 订单编号
     */
    private String orderSn;
}
