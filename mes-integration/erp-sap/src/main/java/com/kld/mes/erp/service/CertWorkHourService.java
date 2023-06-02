package com.kld.mes.erp.service;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;

/**
 * 根据完工合格证推送关联跟单 工序的工时数据
 *
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:54
 */
public interface CertWorkHourService {

    public CommonResult sendWorkHour(List<TrackItem> trackItemList, String erpCode, String orderNo, int qty, String unit);

}
