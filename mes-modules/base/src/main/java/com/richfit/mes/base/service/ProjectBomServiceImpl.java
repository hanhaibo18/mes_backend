package com.richfit.mes.base.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProjectBomMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;
import org.springframework.stereotype.Service;

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
                .eq("branch_code", branchCode);
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
                .eq("tenant_id", bom.getTenantId())
                .eq("branch_code", bom.getBranchCode())
                .notIn("grade", "H");
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


}
