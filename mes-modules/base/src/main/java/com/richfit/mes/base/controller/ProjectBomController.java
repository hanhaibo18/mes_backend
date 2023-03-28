package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.entity.DeleteBomDto;
import com.richfit.mes.base.entity.DeleteProjectBomDto;
import com.richfit.mes.base.service.ProjectBomService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProjectBomController.java
 * @Author: Hou XinYu
 * @Description: 项目BOM
 * @CreateTime: 2022年06月01日 07:33:00
 */

@Slf4j
@Api(value = "项目Bom管理", tags = {"项目Bom管理"})
@RestController
@RequestMapping("/api/base/project_bom")
public class ProjectBomController {

    @Resource
    private ProjectBomService projectBomService;

    @DeleteMapping("/deleteBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "删除BOM")
    public CommonResult<Boolean> deleteBom(String id, String workPlanNo, String branchCode, String drawingNo) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        //处理逻辑判断问题   zhiqiang.lu   2023.1.4
        return CommonResult.success(projectBomService.deleteBom(id, workPlanNo, tenantId, branchCode, drawingNo));
    }

    @DeleteMapping("/deleteBomList")
    @ApiOperation(value = "删除多条BOM")
    public CommonResult<Boolean> deleteBom(@RequestBody DeleteProjectBomDto deleteProjectBomDto) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        boolean deleteBom = false;
        for (DeleteBomDto deleteBomDto : deleteProjectBomDto.getBomList()) {
            //处理逻辑判断问题   zhiqiang.lu   2023.1.4
            deleteBom = projectBomService.deleteBom(deleteBomDto.getId(), deleteBomDto.getWorkPlanNo(), tenantId, deleteProjectBomDto.getBranchCode(), deleteBomDto.getDrawingNo());
        }
        return CommonResult.success(deleteBom);
    }


    @ApiOperation(value = "分页查询项目Bom", notes = "根据图号、状态等分页查询项目Bom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "prodDesc", value = "零部件名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "order", value = "排序方式", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/project_bom")
    public CommonResult<IPage<ProjectBom>> getProductionBomPage(String drawingNo, String projectName, String prodDesc, String state, String branchCode, String order, String orderCol, String publishState, int page, int limit) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(projectBomService.getProjectBomPage(drawingNo, projectName, prodDesc, state, tenantId, branchCode, order, orderCol, publishState, page, limit));
    }

    @PutMapping("/updateBom")
    @ApiOperation(value = "修改Bom")
    public CommonResult<Boolean> updateBom(@RequestBody ProjectBom projectBom) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        projectBom.setTenantId(tenantId);
        return CommonResult.success(projectBomService.updateBom(projectBom));
    }

    @ApiOperation(value = "查询项目列表", notes = "根据产品BOM图号查询项目BOM列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/project_bom_list")
    public CommonResult<List<ProjectBom>> getProjectBomList(String drawingNo, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(projectBomService.getProjectBomList(drawingNo, tenantId, branchCode));
    }

    @ApiOperation(value = "根据工作号查询项目BOM零件", notes = "点击项目BOM进入零件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/getProjectBomPartList")
    public CommonResult<List<ProjectBom>> getProjectBomPartList(String workPlanNo, String drawingNo, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(projectBomService.getProjectBomPartList(workPlanNo, drawingNo, tenantId, branchCode));
    }

    @ApiOperation(value = "根据项目BOM的ID查询项目BOM零件", notes = "提供给其他服务使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "项目Id", paramType = "query", dataType = "String"),
    })

    @GetMapping("/getProjectBomPartByIdList")
    public List<ProjectBom> getProjectBomPartByIdList(String id) {
        return projectBomService.getProjectBomPartByIdList(id);
    }

    @ApiOperation(value = "根据工作号和项目名称模糊查询项目BOM", notes = "新增零件时查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/getPartList")
    public CommonResult<List<ProjectBom>> getPartList(String workPlanNo, String projectName, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
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
    public CommonResult<Boolean> saveBom(@RequestBody ProjectBom projectBom) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        projectBom.setTenantId(tenantId);
        return CommonResult.success(projectBomService.saveBom(projectBom));
    }

    @ApiOperation(value = "关联零件分解项", notes = "关联是否分解零件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "partId", value = "零件Id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "bomId", value = "关联项目ID", paramType = "query", dataType = "String")
    })
    @GetMapping("/relevancePart")
    public CommonResult<Boolean> relevancePart(String partId, String bomId) {
        return CommonResult.success(projectBomService.relevancePart(partId, bomId));
    }

    @ApiOperation(value = "查询关联分解项名称", notes = "查询关联分解项名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "partId", value = "零件Id", paramType = "query", dataType = "String"),
    })
    @GetMapping("/getPartName")
    public CommonResult<Map<String, String>> getPartName(String partId) {
        return projectBomService.getPartName(partId);
    }

    @ApiOperation(value = "装配验证是否H级别图号", notes = "查询关联分解项名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "level", value = "级别", paramType = "query", dataType = "String"),
    })
    @GetMapping("/queryBom")
    public ProjectBom queryBom(String workPlanNo, String branchCode) {
        return projectBomService.queryBom(workPlanNo, branchCode);
    }


    @ApiOperation(value = "导出BOM到Excel", notes = "导出BOM到Excel")
    @ApiImplicitParam(name = "idList", value = "idList", paramType = "query", allowMultiple = true, dataType = "String")
    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody List<String> idList, HttpServletResponse rsp) throws IOException {
        projectBomService.exportExcel(idList, rsp);
    }
}
