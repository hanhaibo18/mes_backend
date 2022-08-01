package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.sys.service.QualityInspectionRulesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName: QualityInspectionRulesController.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月28日 11:17:00
 */

@Slf4j
@Api(value = "质量规则", tags = {"质量规则"})
@RequestMapping(value = "/api/sys/qualityInspectionRules")
@RestController
public class QualityInspectionRulesController {

    @Resource
    private QualityInspectionRulesService qualityInspectionRulesService;

    @ApiOperation(value = "创建质量规则", notes = "创建质量规则")
    @PostMapping(value = "/saveQualityInspectionRules")
    public CommonResult<Boolean> saveQualityInspectionRules(@RequestBody QualityInspectionRules qualityInspectionRules) {
        return CommonResult.success(qualityInspectionRulesService.saveQualityInspectionRules(qualityInspectionRules));
    }

    @ApiOperation(value = "修改质量规则", notes = "修改质量规则")
    @PutMapping(value = "/updateQualityInspectionRules")
    public CommonResult<Boolean> updateQualityInspectionRules(@RequestBody QualityInspectionRules qualityInspectionRules) {
        return CommonResult.success(qualityInspectionRulesService.updateQualityInspectionRules(qualityInspectionRules));
    }

    @ApiOperation(value = "删除质量规则", notes = "删除质量规则")
    @ApiImplicitParam(name = "id", value = "规则ID", dataType = "List<String>", paramType = "body")
    @DeleteMapping("/deleteQualityInspectionRules")
    public CommonResult<Boolean> deleteQualityInspectionRules(@RequestBody List<String> idList) {
        boolean rules = false;
        for (String id : idList) {
            rules = qualityInspectionRulesService.deleteQualityInspectionRules(id);
        }
        return CommonResult.success(rules);
    }

    @ApiOperation(value = "分页查询质量规则", notes = "分页查询质量规则")
    @ApiImplicitParam(name = "stateName", value = "状态名称", dataType = "String", paramType = "body")
    @GetMapping("/queryQualityInspectionRulesPage")
    public CommonResult<IPage<QualityInspectionRules>> queryQualityInspectionRulesPage(String stateName, long page, long limit, String order, String orderCol) {
        return CommonResult.success(qualityInspectionRulesService.queryQualityInspectionRulesPage(stateName, page, limit, order, orderCol));
    }

    @ApiOperation(value = "导出质量规则", notes = "导出质量规则")
    @ApiImplicitParam(name = "stateName", value = "状态名称", dataType = "String", paramType = "body")
    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse rsp) {
        qualityInspectionRulesService.exportExcel(rsp);
    }
}
