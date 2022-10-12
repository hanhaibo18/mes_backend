package com.richfit.mes.produce.controller.quality;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.produce.entity.QueryInspectorDto;
import com.richfit.mes.produce.service.quality.DisqualificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: DisqualificationController.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年09月30日 14:40:00
 */
@Slf4j
@Api(value = "不合格品处理流程", tags = {"不合格品处理流程"})
@RestController
@RequestMapping("/api/produce/quality/disqualification")
public class DisqualificationController extends BaseController {

    @Resource
    private DisqualificationService disqualificationService;

    @ApiOperation(value = "待处理申请单", notes = "根据查询条件查询待处理申请单")
    @PostMapping("/queryInspector")
    private CommonResult<IPage<Disqualification>> queryInspector(@RequestBody QueryInspectorDto queryInspectorDto) {
        return CommonResult.success(disqualificationService.queryInspector(queryInspectorDto));
    }

    @ApiOperation(value = "创建申请单", notes = "创建不合格品申请单")
    @PostMapping("/saveDisqualification")
    public CommonResult<Boolean> saveDisqualification(@RequestBody Disqualification disqualification) {
        return CommonResult.success(disqualificationService.saveDisqualification(disqualification));
    }

    @ApiOperation(value = "修改申请单", notes = "修改不合格品申请单")
    @PostMapping("/updateDisqualification")
    public CommonResult<Boolean> updateDisqualification(@RequestBody Disqualification disqualification) {
        return CommonResult.success(disqualificationService.updateDisqualification(disqualification));
    }

    @ApiOperation(value = "开单", notes = "发布申请单")
    @ApiImplicitParam(name = "id", value = "申请单Id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/issueApplication")
    public CommonResult<Boolean> issueApplication(String id) {
        return CommonResult.success(disqualificationService.updateIsIssue(id, "1"));
    }

    @ApiOperation(value = "关单", notes = "关闭申请单")
    @ApiImplicitParam(name = "id", value = "申请单Id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/closeApplication")
    public CommonResult<Boolean> closeApplication(String id) {
        return CommonResult.success(disqualificationService.updateIsIssue(id, "2"));
    }
}
