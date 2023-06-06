package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.entity.ConnectDTO;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.BaseProductConnect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Service
 * @createDate 2023-06-05 09:23:01
 */
public interface BaseProductConnectService extends IService<BaseProductConnect> {

    /**
     * 交接单据列表
     *
     * @param connectDTO
     * @return
     */
    Page queryConnectInfo(ConnectDTO connectDTO);

    /**
     * 交接单据详情列表
     *
     * @param connectId
     * @return
     */
    Page queryConnectDetailInfo(String connectId, int page, int limit);

    /**
     * 新增交接单据
     *
     * @param connectDTO
     * @return
     */
    CommonResult insertConnect(ConnectDTO connectDTO);

    /**
     * 新增交接单据
     *
     * @param connectDTO
     * @return
     */
    CommonResult editConnect(ConnectDTO connectDTO);
}
