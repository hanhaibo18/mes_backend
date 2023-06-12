package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.entity.ReceiptDTO;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.BaseProductReceiptExtend;

import java.util.List;

/**
 * @author wangchenyu
 * @description 针对表【base_product_connect(产品交接单据)】的数据库操作Service
 * @createDate 2023-06-05 09:23:01
 */
public interface BaseProductReceiptService extends IService<BaseProductReceipt> {

    /**
     * 交接单据列表
     *
     * @param receiptDTO
     * @return
     */
    Page queryReceiptInfo(ReceiptDTO receiptDTO);

    /**
     * 交接单据详情
     *
     * @param connectId
     * @param number
     * @param workNo
     * @param drawNo
     * @param branchCode
     * @param tenantId
     * @param operate::1::编辑；2：：详情
     * @return
     */
    List<BaseProductReceiptExtend> queryReceiptDetailInfo(String connectId, Integer number, String workNo, String drawNo, String branchCode, String tenantId, String operate);

    /**
     * 新增交接单据
     *
     * @param receiptDTO
     * @return
     */
    CommonResult insertReceipt(ReceiptDTO receiptDTO);

    /**
     * 新增交接单据
     *
     * @param receiptDTO
     * @return
     */
    CommonResult editReceipt(ReceiptDTO receiptDTO);

    /**
     * 接收单据
     *
     * @param receiptDTO
     * @return
     */
    CommonResult receive(ReceiptDTO receiptDTO);

    /**
     * 拒收单据
     *
     * @param connectId
     * @return
     */
    CommonResult rejection(String connectId);

    /**
     * 返回接收单据
     *
     * @param connectId
     * @return
     */
    CommonResult returnBack(String connectId);

    /**
     * 物料接收明细
     *
     * @param receiptDTO
     * @return
     */
    Page receiveDetail(ReceiptDTO receiptDTO);
}
