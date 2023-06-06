package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.entity.ConnectDTO;
import com.richfit.mes.base.service.BaseProductConnectService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.BaseProductConnect;
import com.richfit.mes.common.model.base.BaseProductConnectExtend;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangchenyu
 * @Description bom交接Controller
 */
@Slf4j
@Api(value = "产品交接", tags = {"产品交接"})
@RestController
@RequestMapping("/api/base/connect")
public class BaseProductConnectController extends BaseController {


    @Autowired
    private BaseProductConnectService baseProductConnectService;

    @ApiOperation(value = "交接单据列表")
    @PostMapping("/page")
    public CommonResult<Page<BaseProductConnect>> query(@ApiParam(value = "查询条件") @RequestBody ConnectDTO connectDTO) {
        return CommonResult.success(baseProductConnectService.queryConnectInfo(connectDTO));
    }

    @ApiOperation(value = "交接单据详情列表")
    @GetMapping("/page/detail")
    public CommonResult<Page<BaseProductConnectExtend>> queryDetail(@ApiParam(value = "查询条件") @RequestParam(value = "connectId", required = true) String connectId,
                                                                    @RequestParam(defaultValue = "1", required = false) int page,
                                                                    @RequestParam(defaultValue = "10", required = false) int limit) {
        return CommonResult.success(baseProductConnectService.queryConnectDetailInfo(connectId, page, limit));
    }

    @ApiOperation(value = "新增交接单信息")
    @PostMapping("/insert")
    public CommonResult insertConnect(@ApiParam(value = "新增交接单据") @RequestBody ConnectDTO connectDTO) {
        baseProductConnectService.insertConnect(connectDTO);
        return CommonResult.success("添加成功");
    }

    @ApiOperation(value = "编辑交接单信息")
    @PostMapping("/edit")
    public CommonResult editConnect(@ApiParam(value = "编辑交接单据") @RequestBody ConnectDTO connectDTO) {
        baseProductConnectService.editConnect(connectDTO);
        return CommonResult.success("修改成功");
    }
}
