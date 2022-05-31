package com.richfit.mes.base.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProductionBomMapper;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.ProjectBom;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 产品BOM服务
 */
@Service
public class ProductionBomServiceImpl extends ServiceImpl<ProductionBomMapper, ProductionBom> implements ProductionBomService {

    @Autowired
    private ProductionBomMapper productionBomMapper;

    @Resource
    private ProjectBomService projectBomService;

    @Override
    public IPage<ProductionBom> getProductionBomByPage(Page<ProductionBom> page, QueryWrapper<ProductionBom> query) {
        return productionBomMapper.getProductionBomByPage(page, query);
    }

    @Override
    public IPage<ProductionBom> getProductionBomHistory(Page<ProductionBom> page, QueryWrapper<ProductionBom> query) {
        return productionBomMapper.getProductionBomHistory(page, query);
    }

    @Override
    public boolean saveByList(List<ProductionBom> list) {

        List<ProductionBom> addList = new ArrayList<>();

        for (ProductionBom bom : list) {
            for (ProductionBom bom2 : list) {
                if (bom.getMainDrawingNo() != null && bom.getMainDrawingNo() != "") {
                    if (bom2.getDrawingNo().equals(bom.getMainDrawingNo())) {
                        bom.setMainDrawingNo(bom2.getId());
                        break;
                    }
                }
            }
            int count = productionBomMapper.insert(bom);
            if (count > 0) {
                addList.add(bom);
            }
        }

        if (addList.size() == list.size()) {
            return true;
        }
        return false;
    }

    @Override
    public List<ProductionBom> getProductionBomList(@Param(Constants.WRAPPER) Wrapper<ProductionBom> query) {
        return productionBomMapper.getProductionBomList(query);
    }

    @Override
    @Transactional
    public boolean updateStatus(ProductionBom bom) {
        UpdateWrapper<ProductionBom> update = new UpdateWrapper<ProductionBom>();
        update.apply("(drawing_no = {0} or main_drawing_no = {0} ) and bom_key = {1}", bom.getDrawingNo(), bom.getBomKey());
        return SqlHelper.retBool(productionBomMapper.update(null, update));
    }

    @Override
    public List<ProductionBom> getProductionBomByDrawingNoList(String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_drawing_no", drawingNo);
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("branch_code", branchCode);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean issueBom(String id, String workPlanNo, String projectName, String tenantId, String branchCode) {
        //先处理H级别的数据
        ProductionBom productionBom = this.getById(id);
        ProjectBom projectBom = projectBomEntity(productionBom);
        projectBom.setTenantId(tenantId)
                .setBranchCode(branchCode)
                .setProjectName(projectName)
                .setWorkPlanNo(workPlanNo);
        projectBomService.save(projectBom);
        //在处理L级别零件数据
        QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_drawing_no", productionBom.getDrawingNo());
        List<ProductionBom> productionBomList = this.list(queryWrapper);
//        List<ProjectBom> projectBomList1 = productionBomList.stream().map(this::projectBomEntity).collect(Collectors.toList());
        List<ProjectBom> projectBomList = productionBomList.stream().map(production -> {
            ProjectBom project = projectBomEntity(production);
            project.setTenantId(tenantId)
                    .setBranchCode(branchCode)
                    .setProjectName(projectName)
                    .setWorkPlanNo(workPlanNo);
            return project;
        }).collect(Collectors.toList());
        return projectBomService.saveBatch(projectBomList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBom(String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.and(wrapper ->
                wrapper.eq("main_drawing_no", drawingNo)
                        .or()
                        .eq("drawing_no", drawingNo)
        );
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBom(ProductionBom productionBom) {
        return this.updateById(productionBom);
    }

    @Override
    public IPage<ProductionBom> getProductionBomPage(String drawingNo, String tenantId, String branchCode, String order, String orderCol, int page, int limit) {
        QueryWrapper<ProductionBom> query = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            query.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            query.eq("branch_code", branchCode);
        }
        query.eq("grade", "H");
//        List<GrantedAuthority> authorities = new ArrayList<>(SecurityUtils.getCurrentUser().getAuthorities());
//        boolean isAdmin = false;
//        for (GrantedAuthority authority : authorities) {
//            //超级管理员 ROLE_12345678901234567890000000000000
//            if ("ROLE_12345678901234567890000000000000".equals(authority.getAuthority())) {
//                isAdmin = true;
//                break;
//            }
//        }
//        if (!isAdmin) {
//            query.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
//        }
        query.eq("tenant_id", tenantId);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    query.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    query.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                query.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            query.orderByDesc("modify_time");
        }
        return this.page(new Page<>(page, limit), query);
    }

    private ProjectBom projectBomEntity(ProductionBom productionBom) {
        ProjectBom projectBom = new ProjectBom();
        projectBom.setDrawingNo(productionBom.getDrawingNo())
                .setMaterialNo(productionBom.getMaterialNo())
                .setTexture(productionBom.getTexture())
                .setWeight(productionBom.getWeight())
                .setUnit(productionBom.getUnit())
                .setIsKeyPart(productionBom.getIsKeyPart())
                .setIsNeedPicking(productionBom.getIsNeedPicking())
                .setIsEdgeStore(productionBom.getIsEdgeStore())
                .setIsCheck(productionBom.getIsCheck())
                .setGrade(productionBom.getGrade())
                .setTrackType(productionBom.getTrackType())
                .setNumber(productionBom.getNumber())
                .setBomKey(productionBom.getBomKey())
                .setSourceType(productionBom.getSourceType())
                .setOrderNo(productionBom.getOrderNo())
                .setProdDesc(productionBom.getProdDesc());
        return projectBom;
    }

}
