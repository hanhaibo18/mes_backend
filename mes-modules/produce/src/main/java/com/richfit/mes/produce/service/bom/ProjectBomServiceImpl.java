package com.richfit.mes.produce.service.bom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.mysql.cj.util.StringUtils;

import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.bom.ProduceProjectBom;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.produce.dao.bom.ProjectBomMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author zhiqiang.lu
 * @Description 项目BOM服务
 */
@Slf4j
@Service
public class ProjectBomServiceImpl extends ServiceImpl<ProjectBomMapper, ProduceProjectBom> implements ProjectBomService {
    @Autowired
    private ProjectBomMapper projectBomMapper;

    @Override
    public List<ProduceProjectBom> getProjectBomList(String workPlanNo, String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProduceProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo);
        queryWrapper.eq("grade", "H");
        DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        queryWrapper.eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProduceProjectBom> getProjectBomPartList(String workPlanNo, String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProduceProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode)
                .orderByAsc("order_no");
        DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", drawingNo);
        List<ProduceProjectBom> list = this.list(queryWrapper);
        List<ProduceProjectBom> projectBomList = new ArrayList<>();
        for (ProduceProjectBom project : list) {
            if (!StringUtils.isNullOrEmpty(project.getMainDrawingNo())) {
                project.setLevel("2");
                project.setByDrawingNo(project.getMainDrawingNo());
            } else {
                project.setLevel("1");
            }
            if ("1".equals(project.getIsResolution())) {
                ProduceProjectBom projectBom = this.getById(project.getBomKey());
                if (null != projectBom) {
                    QueryWrapper<ProduceProjectBom> queryWrapperPart = new QueryWrapper<>();
                    queryWrapperPart.eq("work_plan_no", projectBom.getWorkPlanNo());
                    DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
                    List<ProduceProjectBom> projectBoms = this.list(queryWrapperPart);
                    for (ProduceProjectBom boms : projectBoms) {
                        boms.setLevel("3");
                        boms.setByDrawingNo(project.getDrawingNo());
                    }
                    projectBomList.addAll(projectBoms);
                }
            }
        }
        list.addAll(projectBomList);
        return list;
    }

    @Override
    public List<ProduceProjectBom> getProjectBomPartByIdList(String id) {
        ProduceProjectBom bom = this.getById(id);
        if (null == bom) {
            return Collections.emptyList();
        }
        QueryWrapper<ProduceProjectBom> queryWrapper = new QueryWrapper<>();
        DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", bom.getDrawingNo());
        queryWrapper.eq("work_plan_no", bom.getWorkPlanNo())
//                .notIn("grade", "H")
                .eq("tenant_id", bom.getTenantId())
                .eq("branch_code", bom.getBranchCode())
                .orderByAsc("order_no");
        List<ProduceProjectBom> list = this.list(queryWrapper);
        List<ProduceProjectBom> projectBomList = new ArrayList<>();
        for (ProduceProjectBom project : list) {
            project.setLevel("2");
            project.setByDrawingNo(project.getMainDrawingNo());
            if ("1".equals(project.getIsResolution())) {
                String drawingNo = project.getDrawingNo();
                ProduceProjectBom projectBom = this.getById(project.getBomKey());
                if (null != projectBom) {
                    QueryWrapper<ProduceProjectBom> queryWrapperPart = new QueryWrapper<>();
                    queryWrapperPart.eq("work_plan_no", projectBom.getWorkPlanNo());
                    DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", bom.getDrawingNo());
                    queryWrapperPart.notIn("grade", "H");
                    queryWrapperPart.orderByAsc("orderNo");
                    List<ProduceProjectBom> projectBoms = this.list(queryWrapperPart);
                    for (ProduceProjectBom boms : projectBoms) {
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
}
