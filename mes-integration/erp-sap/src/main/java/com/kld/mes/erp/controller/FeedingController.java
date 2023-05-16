package com.kld.mes.erp.controller;

import com.kld.mes.erp.entity.feeding.FeedingResult;
import com.kld.mes.erp.service.FeedingService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * @program: mes-backend
 * @description: ERP生产订单接口
 * @author: 王瑞
 * @create: 2022-08-01 15:02
 */
@Slf4j
@Api(value = "ERP接口封装", tags = {"生产订单接口"})
@RestController
@RequestMapping("/api/integration/erp/feeding")
public class FeedingController {

    @Autowired
    private FeedingService feedingService;

    @ApiOperation(value = "生产投料", notes = "生产投料")
    @GetMapping("/send")
    public CommonResult<FeedingResult> send(@ApiParam(value = "erp代号") @RequestParam String erpCode,
                                            @ApiParam(value = "生产订单") @RequestParam String orderCode,
                                            @ApiParam(value = "物料编码") @RequestParam String materialNo,
                                            @ApiParam(value = "图号") @RequestParam String drawingNo,
                                            @ApiParam(value = "数量") @RequestParam String prodQty,
                                            @ApiParam(value = "单位") @RequestParam String unit,
                                            @ApiParam(value = "lgort") @RequestParam String lgort,
                                            @ApiParam(value = "日期") @RequestParam Date date) throws Exception {
        return CommonResult.success(feedingService.sendFeeding(erpCode, orderCode, materialNo, drawingNo,
                prodQty, unit, lgort, date));
    }

    @ApiOperation(value = "毛胚入库生产投料", notes = "毛胚入库生产投料")
    @PostMapping("/store/send")
    public CommonResult<LineStore> storeSend(@ApiParam(value = "erp代号") @RequestBody LineStore lineStore) throws Exception {
        FeedingResult feedingResult = feedingService.sendFeeding(SecurityUtils.getCurrentUser().getTenantErpCode(), lineStore.getProductionOrder(), lineStore.getMaterialNo(), lineStore.getWorkblankNo(),
                lineStore.getNumber() + "", lineStore.getUnit(), lineStore.getBranchCode(), lineStore.getCreateTime());
        try {
            lineStore.setIsFeedErp("1");
            lineStore.setFeedErpMessage(feedingResult.getMsg());
            if ("S".equals(feedingResult.getCode())) {
                lineStore.setFeedErpStatus("1");
                lineStore.setFeedErpNumber(feedingResult.getFeedingCode());
            } else {
                lineStore.setFeedErpStatus("2");
            }
            return CommonResult.success(lineStore);
        } catch (Exception e) {
            lineStore.setFeedErpStatus("2");
            lineStore.setFeedErpMessage(e.getMessage());
            return CommonResult.success(lineStore);
        }
    }
}
