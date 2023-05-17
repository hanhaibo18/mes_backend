package com.kld.mes.erp.service;


import com.kld.mes.erp.entity.feeding.FeedingResult;

import java.util.Date;

/**
 * 生产订单投料
 *
 * @author wcy
 * @date 2023/5/15 16:06
 */
public interface FeedingService {

    FeedingResult sendFeeding(String erpCode, String orderCode, String materialNo, String drawingNo,
                              String prodQty, String unit, String lgort, Date date) throws Exception;
}
