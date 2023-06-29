package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ProduceDrillingRectification;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationDTO;
import com.richfit.mes.produce.entity.ProduceDrillingRectificationVO;
import com.richfit.mes.produce.service.ProduceDrillingRectificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author wangchenyu
 * @Description 钻机整改单据Controller
 */
@Slf4j
@Api(value = "钻机整改单据", tags = {"钻机整改单据"})
@RestController
@RequestMapping("/api/produce/rectification")
public class ProduceDrillingRectificationController extends BaseController {


    @Autowired
    private ProduceDrillingRectificationService produceDrillingRectificationService;

    @ApiOperation(value = "钻机整改列表")
    @PostMapping("/page")
    public CommonResult<Page<ProduceDrillingRectification>> query(@ApiParam(value = "查询条件") @RequestBody ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        return CommonResult.success(produceDrillingRectificationService.queryPageInfo(produceDrillingRectificationDTO));
    }

    @ApiOperation(value = "新增整改信息")
    @PostMapping("/insert")
    public CommonResult insertRectification(@ApiParam(value = "新增整改单据") @RequestBody ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        produceDrillingRectificationService.insertRectification(produceDrillingRectificationDTO);
        return CommonResult.success("添加成功");
    }

    @ApiOperation(value = "编辑整改信息")
    @PostMapping("/edit")
    public CommonResult editReceipt(@ApiParam(value = "编辑整改单据") @RequestBody ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        produceDrillingRectificationService.editReceipt(produceDrillingRectificationDTO);
        return CommonResult.success("修改成功");
    }

    @ApiOperation(value = "撤回整改信息")
    @GetMapping("/returnBack")
    public CommonResult returnBack(@ApiParam(value = "整改单据id") @RequestParam(value = "id") String id,
                                   @ApiParam(value = "操作菜单") @RequestParam(value = "menuType") String menuType) {
        return CommonResult.success(produceDrillingRectificationService.returnBack(id, menuType));
    }

    @ApiOperation(value = "提交整改信息")
    @PostMapping("/commit")
    public CommonResult commit(@ApiParam(value = "提交整改单据") @RequestBody ProduceDrillingRectificationDTO produceDrillingRectificationDTO) {
        produceDrillingRectificationService.commit(produceDrillingRectificationDTO);
        return CommonResult.success("已提交");
    }

    @ApiOperation(value = "查看单据详情 ")
    @GetMapping("/detail")
    public CommonResult<ProduceDrillingRectificationVO> queryDetail(@ApiParam(value = "整改单据id") @RequestParam(value = "id") String id) {
        return CommonResult.success(produceDrillingRectificationService.queryDetail(id));
    }

    @ApiOperation(value = "删除未提交的单据 ")
    @GetMapping("/delete")
    public CommonResult deleteBill(@ApiParam(value = "整改单据id") @RequestParam(value = "id") String id) {
        return produceDrillingRectificationService.deleteBill(id);
    }

    @ApiOperation(value = "关闭处理单据 ")
    @GetMapping("/closeBill")
    public CommonResult closeBill(@ApiParam(value = "整改单据id") @RequestParam(value = "id") String id) {
        return produceDrillingRectificationService.closeBill(id);
    }
}
