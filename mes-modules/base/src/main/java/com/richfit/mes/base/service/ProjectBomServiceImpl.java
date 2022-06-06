package com.richfit.mes.base.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProjectBomMapper;
import com.richfit.mes.common.model.base.ProjectBom;
import org.springframework.stereotype.Service;

import java.util.List;

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
        list.forEach(bom -> {
            if (bom.getIsResolution()) {
                QueryWrapper<ProjectBom> queryWrapperPart = new QueryWrapper<>();
                queryWrapperPart.eq("bomKey", bom.getBomKey());
                list.addAll(this.list(queryWrapperPart));
            }
        });
        return list;
    }

    @Override
    public List<ProjectBom> getProjectBomPartByIdList(String id, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        List<ProjectBom> list = this.list(queryWrapper);
        String drawingNo = null;
        for (ProjectBom bom : list) {
            bom.setLevel("2");
            bom.setByDrawingNo(bom.getMainDrawingNo());
            if (bom.getIsResolution()) {
                bom.setLevel("3");
                bom.setByDrawingNo(bom.getDrawingNo());
                drawingNo = bom.getDrawingNo();
                QueryWrapper<ProjectBom> queryWrapperPart = new QueryWrapper<>();
                queryWrapperPart.eq("bomKey", bom.getBomKey());
                list.addAll(this.list(queryWrapperPart));
            }
        }

        for (ProjectBom projectBom : list) {
            if (id.equals(projectBom.getId())) {
                list.remove(projectBom);
                continue;
            }
            if (null != drawingNo) {
                list.remove(projectBom);
            }
        }
        return list;
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


}
