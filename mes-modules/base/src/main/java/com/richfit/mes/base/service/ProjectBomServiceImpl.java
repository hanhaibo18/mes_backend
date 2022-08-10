package com.richfit.mes.base.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProjectBomMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 侯欣雨
 * @Description 项目BOM服务
 */
@Service
public class ProjectBomServiceImpl extends ServiceImpl<ProjectBomMapper, ProjectBom> implements ProjectBomService {

    @Override
    public boolean deleteBom(String workPlanNo, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean updateBom(ProjectBom projectBom) {
        return this.updateById(projectBom);
    }

    @Override
    public IPage<ProjectBom> getProjectBomPage(String drawingNo, String projectName, String prodDesc, String state, String tenantId, String branchCode, String order, String orderCol, int page, int limit) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(projectName)) {
            queryWrapper.like("project_name", projectName);
        }
        if (!StringUtils.isNullOrEmpty(prodDesc)) {
            queryWrapper.like("prod_desc", prodDesc);
        }
        if (!StringUtils.isNullOrEmpty(state)) {
            queryWrapper.eq("state", state);
        }
        queryWrapper.eq("grade", "H")
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("order_no");
        }
        return this.page(new Page<>(page, limit), queryWrapper);
    }

    @Override
    public List<ProjectBom> getProjectBomList(String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("drawing_no", drawingNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProjectBom> getProjectBomPartList(String workPlanNo, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode)
                .orderByAsc("order_no");
        List<ProjectBom> list = this.list(queryWrapper);
        List<ProjectBom> projectBomList = new ArrayList<>();
        for (ProjectBom project : list) {
            if (!StringUtils.isNullOrEmpty(project.getMainDrawingNo())) {
                project.setLevel("2");
                project.setByDrawingNo(project.getMainDrawingNo());
            } else {
                project.setLevel("1");
            }
            if ("1".equals(project.getIsResolution())) {
                String drawingNo = project.getDrawingNo();
                ProjectBom projectBom = this.getById(project.getBomKey());
                if (null != projectBom) {
                    QueryWrapper<ProjectBom> queryWrapperPart = new QueryWrapper<>();
                    queryWrapperPart.eq("work_plan_no", projectBom.getWorkPlanNo());
                    List<ProjectBom> projectBoms = this.list(queryWrapperPart);
                    for (ProjectBom boms : projectBoms) {
                        boms.setLevel("3");
                        boms.setByDrawingNo(drawingNo);
                    }
                    projectBomList.addAll(projectBoms);
                }
            }
        }
        list.addAll(projectBomList);
        return list;
    }

    @Override
    public List<ProjectBom> getProjectBomPartByIdList(String id) {
        ProjectBom bom = this.getById(id);
        if (null == bom) {
            return Collections.emptyList();
        }
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", bom.getWorkPlanNo())
//                .notIn("grade", "H")
                .eq("tenant_id", bom.getTenantId())
                .eq("branch_code", bom.getBranchCode())
                .orderByAsc("orderNo");
        List<ProjectBom> list = this.list(queryWrapper);
        List<ProjectBom> projectBomList = new ArrayList<>();
        for (ProjectBom project : list) {
            project.setLevel("2");
            project.setByDrawingNo(project.getMainDrawingNo());
            if ("1".equals(project.getIsResolution())) {
                String drawingNo = project.getDrawingNo();
                ProjectBom projectBom = this.getById(project.getBomKey());
                if (null != projectBom) {
                    QueryWrapper<ProjectBom> queryWrapperPart = new QueryWrapper<>();
                    queryWrapperPart.eq("work_plan_no", projectBom.getWorkPlanNo());
                    queryWrapperPart.notIn("grade", "H");
                    queryWrapperPart.orderByAsc("orderNo");
                    List<ProjectBom> projectBoms = this.list(queryWrapperPart);
                    for (ProjectBom boms : projectBoms) {
                        boms.setLevel("3");
                        boms.setByDrawingNo(drawingNo);
                    }
                    projectBomList.addAll(projectBoms);
                }
            }
        }
        list.addAll(projectBomList);
        return list.stream().filter(itm -> !"1".equals(itm.getIsResolution())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectBom> getPartList(String workPlanNo, String projectName, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(workPlanNo)) {
            queryWrapper.like("work_plan_no", workPlanNo);
        }
        if (!StringUtils.isNullOrEmpty(projectName)) {
            queryWrapper.like("project_name", projectName);
        }
        queryWrapper.eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.list(queryWrapper);
    }

    @Override
    public boolean deletePart(String id) {
        return this.removeById(id);
    }

    @Override
    public boolean saveBom(ProjectBom projectBom) {
        return this.save(projectBom);
    }

    @Override
    public boolean relevancePart(String partId, String bomId) {
        ProjectBom part = this.getById(partId);
        if (!StringUtils.isNullOrEmpty(bomId)) {
            part.setBomKey(bomId);
            part.setIsResolution("1");
        } else {
            part.setIsResolution("0");
        }
        return this.updateById(part);
    }

    @Override
    public CommonResult<Map<String, String>> getPartName(String partId) {
        ProjectBom projectBom = this.getById(partId);
        if (null == projectBom) {
            return CommonResult.failed("未关联项目BOM");
        }
        Map<String, String> map = new HashMap<>(1);
        map.put(projectBom.getId(), projectBom.getProjectName());
        return CommonResult.success(map);
    }

    @Override
    public ProjectBom queryBom(String workPlanNo, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("is_num_from", 1);
        queryWrapper.notIn("is_resolution", 1);
        return this.getOne(queryWrapper);
    }

    @Override
    public void exportExcel(List<String> idList, HttpServletResponse rsp) {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:excel/" + "产品BOM导出模板.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int sheetNum = 0;
        try {
            assert file != null;
            ExcelWriter writer = ExcelUtil.getReader(file).getWriter();
            HSSFWorkbook wk = (HSSFWorkbook) writer.getWorkbook();
            for (String id : idList) {
                if (sheetNum > 0) {
                    writer.setSheet(wk.cloneSheet(0));
                }
                ProjectBom projectBom = this.getById(id);
                writer.renameSheet(projectBom.getDrawingNo());
                writer.writeCellValue(3, 1, projectBom.getWorkPlanNo());
                writer.writeCellValue(7, 1, projectBom.getDrawingNo());
                writer.writeCellValue(10, 1, projectBom.getMaterialNo());
                writer.writeCellValue(3, 2, projectBom.getProjectName());
                writer.writeCellValue(7, 2, projectBom.getProdDesc());
                writer.resetRow();
                writer.passRows(4);
                QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("work_plan_no", projectBom.getWorkPlanNo());
                queryWrapper.eq("branch_code", projectBom.getBranchCode());
                queryWrapper.orderByAsc("order_no");
                List<ProjectBom> productionBomList = this.list(queryWrapper);
                int number = 0;
                int currentRow = writer.getCurrentRow();
                for (ProjectBom bom : productionBomList) {
                    writer.writeCellValue(1, currentRow, number);
                    writer.writeCellValue(2, currentRow, bom.getBranchCode());
                    writer.writeCellValue(3, currentRow, bom.getGrade());
                    writer.writeCellValue(4, currentRow, bom.getMainDrawingNo());
                    writer.writeCellValue(5, currentRow, bom.getDrawingNo());
                    writer.writeCellValue(6, currentRow, bom.getMaterialNo());
                    writer.writeCellValue(7, currentRow, bom.getProdDesc());
                    writer.writeCellValue(8, currentRow, bom.getSourceType());
                    writer.writeCellValue(9, currentRow, bom.getWeight());
                    writer.writeCellValue(10, currentRow, bom.getTexture());
                    writer.writeCellValue(11, currentRow, bom.getNumber());
                    writer.writeCellValue(12, currentRow, bom.getUnit());
                    writer.writeCellValue(13, currentRow, "");
                    if ("0".equals(bom.getTrackType()) && bom.getTrackType() != null) {
                        writer.writeCellValue(14, currentRow, "单件");
                    } else if ("1".equals(bom.getTrackType()) && bom.getTrackType() != null) {
                        writer.writeCellValue(14, currentRow, "批次");
                    }

                    if ("0".equals(bom.getIsNumFrom())) {
                        writer.writeCellValue(15, currentRow, "否");
                    } else if ("1".equals(bom.getIsNumFrom())) {
                        writer.writeCellValue(15, currentRow, "是");
                    }

                    if ("0".equals(bom.getIsNeedPicking())) {
                        writer.writeCellValue(16, currentRow, "否");
                    } else if ("1".equals(bom.getIsNeedPicking())) {
                        writer.writeCellValue(16, currentRow, "是");
                    }

                    if ("0".equals(bom.getIsKeyPart())) {
                        writer.writeCellValue(17, currentRow, "否");
                    } else if ("1".equals(bom.getIsKeyPart())) {
                        writer.writeCellValue(17, currentRow, "是");
                    }

                    if ("0".equals(bom.getIsEdgeStore())) {
                        writer.writeCellValue(18, currentRow, "否");
                    } else if ("1".equals(bom.getIsEdgeStore())) {
                        writer.writeCellValue(18, currentRow, "是");
                    }
                    if ("0".equals(bom.getIsCheck())) {
                        writer.writeCellValue(19, currentRow, "否");
                    } else if ("1".equals(bom.getIsCheck())) {
                        writer.writeCellValue(19, currentRow, "是");
                    }
                    writer.writeCellValue(20, currentRow, bom.getRemark());
                    number++;
                    currentRow++;
                }
                sheetNum++;
            }
            ServletOutputStream outputStream = rsp.getOutputStream();
            rsp.setContentType("application/vnd.ms-excel;charset=utf-8");
            rsp.setHeader("Content-disposition", "attachment; filename=" + new String("产品BOM".getBytes("utf-8"), "ISO-8859-1") + ".xls");
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


}
