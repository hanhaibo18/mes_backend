package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: ProducePurchaseOrderMapper.java
 * @Author: Hou XinYu
 * @Description: 采购订单同步接口
 * @CreateTime: 2022年01月07日 14:59:00
 */
@Mapper
public interface ProducePurchaseOrderMapper extends BaseMapper<ProducePurchaseOrder> {

}
