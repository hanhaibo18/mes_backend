package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.ProjectBomService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: ProjectBomController.java
 * @Author: Hou XinYu
 * @Description: 项目BOM
 * @CreateTime: 2022年06月01日 07:33:00
 */

@Slf4j
@Api(value = "产品Bom管理", tags = {"产品Bom管理"})
@RestController
@RequestMapping("/api/base/project_bom")
public class ProjectBomController {

    @Resource
    private ProjectBomService projectBomService;

    @DeleteMapping("/deleteBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "删除BOM")
    public CommonResult<Boolean> deleteBom(String workPlanNo, String tenantId, String branchCode) {
        return CommonResult.success(projectBomService.deleteBom(workPlanNo, tenantId, branchCode));
    }

    @DeleteMapping("/deleteBomList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNoList", value = "工作号", required = true, paramType = "query", dataType = "List<String>"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "删除多条BOM")
    public CommonResult<Boolean> deleteBom(List<String> workPlanNoList, String tenantId, String branchCode) {
        boolean deleteBom = false;
        for (String workPlanNo : workPlanNoList) {
            deleteBom = projectBomService.deleteBom(workPlanNo, tenantId, branchCode);
        }
        return CommonResult.success(deleteBom);
    }

    @ApiOperation(value = "分页查询项目Bom", notes = "根据图号、状态等分页查询项目Bom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "prodDesc", value = "零部件名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "order", value = "排序方式", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/project_bom")
    public CommonResult<IPage<ProjectBom>> getProductionBomPage(String drawingNo, String projectName, String prodDesc, String state, String tenantId, String branchCode, String order, String orderCol, int page, int limit) {
        return CommonResult.success(projectBomService.getProjectBomPage(drawingNo, projectName, prodDesc, state, tenantId, branchCode, order, orderCol, page, limit));
    }

    @PutMapping("/updateBom")
    @ApiOperation(value = "修改Bom")
    public CommonResult<Boolean> updateBom(@RequestBody ProjectBom projectBom) {
        return CommonResult.success(projectBomService.updateBom(projectBom));
    }

    @ApiOperation(value = "查询项目列表", notes = "根据产品BOM图号查询项目BOM列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/project_bom_list")
    public CommonResult<List<ProjectBom>> getProjectBomList(String drawingNo, String tenantId, String branchCode) {
        return CommonResult.success(projectBomService.getProjectBomList(drawingNo, tenantId, branchCode));
    }

    @ApiOperation(value = "根据工作号查询项目BOM零件", notes = "点击项目BOM进入零件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/getProjectBomPartList")
    public CommonResult<List<ProjectBom>> getProjectBomPartList(String workPlanNo, String tenantId, String branchCode) {
        return CommonResult.success(projectBomService.getProjectBomPartList(workPlanNo, tenantId, branchCode));
    }

    @ApiOperation(value = "根据项目BOM的ID查询项目BOM零件", notes = "提供给其他服务使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "项目Id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/getProjectBomPartByIdList")
    public List<ProjectBom> getProjectBomPartByIdList(String id, String tenantId, String branchCode) {
        return projectBomService.getProjectBomPartByIdList(id, tenantId, branchCode);
    }

    @ApiOperation(value = "根据工作号和项目名称模糊查询项目BOM", notes = "新增零件时查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/getPartList")
    public CommonResult<List<ProjectBom>> getPartList(String workPlanNo, String projectName, String tenantId, String branchCode) {
        return CommonResult.success(projectBomService.getPartList(workPlanNo, projectName, tenantId, branchCode));
    }

    @ApiOperation(value = "根据项目BOM主键ID删除零件", notes = "删除零件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "项目Id", paramType = "query", dataType = "String"),
    })
    @DeleteMapping("/deletePart")
    public CommonResult<Boolean> deletePart(String id) {
        return CommonResult.success(projectBomService.deletePart(id));
    }

    @ApiOperation(value = "新增零件", notes = "项目BOM新增零件")
    @PostMapping("/saveBom")
    public CommonResult<Boolean> saveBom(ProjectBom projectBom) {
        return CommonResult.success(projectBomService.saveBom(projectBom));
    }

    
}
