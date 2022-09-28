package com.kld.mes.erp.service;

import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;

/**
 * 根据完工合格证推送关联跟单 工序的工时数据
 *
 * @Author: fengxy
 * @Date: 2022年9月26日15:32:10
 */
public interface CertWorkHourService {

    public boolean sendWorkHour(List<TrackItem> trackItemList, String erpCode,String orderNo,String materialNo, int qty, String unit);

}
