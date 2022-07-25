package com.richfit.mes.base.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
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
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.ProjectBom;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
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
    public CommonResult<Boolean> issueBom(String id, String workPlanNo, String projectName, String tenantId, String branchCode) {
        //检查是否有当前项目号
        QueryWrapper<ProjectBom> queryWrapperProject = new QueryWrapper<>();
        queryWrapperProject.eq("work_plan_no", workPlanNo);
        if (!projectBomService.list(queryWrapperProject).isEmpty()) {
            return CommonResult.failed("当前工作号已存在");
        }
        //先处理H级别的数据
        ProductionBom productionBom = this.getById(id);
        ProjectBom projectBom = projectBomEntity(productionBom);
        projectBom.setTenantId(tenantId).setBranchCode(branchCode).setProjectName(projectName).setWorkPlanNo(workPlanNo);
        projectBomService.save(projectBom);
        //在处理L级别零件数据
        QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_drawing_no", productionBom.getDrawingNo());
        List<ProductionBom> productionBomList = this.list(queryWrapper);
        List<ProjectBom> projectBomList = productionBomList.stream().map(production -> {
            ProjectBom project = projectBomEntity(production);
            project.setTenantId(tenantId).setBranchCode(branchCode).setProjectName(projectName).setWorkPlanNo(workPlanNo);
            return project;
        }).collect(Collectors.toList());
        return CommonResult.success(projectBomService.saveBatch(projectBomList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBom(String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.and(wrapper -> wrapper.eq("main_drawing_no", drawingNo).or().eq("drawing_no", drawingNo));
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
            query.orderByDesc("order_no");
        }
        return this.page(new Page<>(page, limit), query);
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
                ProductionBom productionBom = this.getById(id);
                writer.renameSheet(productionBom.getDrawingNo());
                writer.writeCellValue(7, 1, productionBom.getDrawingNo());
                writer.writeCellValue(10, 1, productionBom.getMaterialNo());
                writer.writeCellValue(7, 2, productionBom.getProdDesc());
                writer.passRows(4);
                writer.resetRow();
                QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("drawing_no", productionBom.getDrawingNo());
                queryWrapper.or();
                queryWrapper.eq("main_drawing_no", productionBom.getDrawingNo());
                queryWrapper.eq("branch_code", productionBom.getBranchCode());
                queryWrapper.orderByAsc("order_no");
                List<ProductionBom> productionBomList = this.list(queryWrapper);
                int number = 0;
                int currentRow = writer.getCurrentRow();
                for (ProductionBom bom : productionBomList) {
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

    private ProjectBom projectBomEntity(ProductionBom productionBom) {
        ProjectBom projectBom = new ProjectBom();
        projectBom.setDrawingNo(productionBom.getDrawingNo()).setMaterialNo(productionBom.getMaterialNo()).setTexture(productionBom.getTexture()).setWeight(productionBom.getWeight()).setUnit(productionBom.getUnit()).setIsNumFrom(productionBom.getIsNumFrom()).setIsKeyPart(productionBom.getIsKeyPart()).setIsNeedPicking(productionBom.getIsNeedPicking()).setIsEdgeStore(productionBom.getIsEdgeStore()).setIsCheck(productionBom.getIsCheck()).setOptName(productionBom.getOptName()).setGrade(productionBom.getGrade()).setTrackType(productionBom.getTrackType()).setNumber(productionBom.getNumber()).setBomKey(productionBom.getBomKey()).setSourceType(productionBom.getSourceType()).setOrderNo(productionBom.getOrderNo()).setProdDesc(productionBom.getProdDesc());
        return projectBom;
    }

}
