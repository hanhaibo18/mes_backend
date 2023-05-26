package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface InventoryService extends IService<Certificate> {
    /**
     * 功能描述:上交库存
     *
     * @param certificate 合格证信息
     * @throws Exception 合格证号码:异常信息
     * @Author: zhiqiang.lu
     * @Date: 2023/05/26 16:27
     **/
    void handOver(Certificate certificate) throws Exception;
}
