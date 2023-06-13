package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import com.richfit.mes.common.model.base.BaseProductReceiptDetail;
import org.apache.ibatis.annotations.Mapper;

/**
* @author wangchenyu
* @description 针对表【base_product_connect(产品交接单据)】的数据库操作Mapper
* @createDate 2023-06-05 09:23:01
* @Entity generator.domain.BaseProductConnect
*/
@Mapper
public interface BaseProductReceiptDetailMapper extends BaseMapper<BaseProductReceiptDetail> {

}




