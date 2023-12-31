package com.richfit.mes.base.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProjectBomMapper;
import com.richfit.mes.base.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.richfit.mes.base.service.ProductionBomServiceImpl.projectBomEntity;

/**
 * @author 侯欣雨
 * @Description 项目BOM服务
 */
@Slf4j
@Service
public class ProjectBomServiceImpl extends ServiceImpl<ProjectBomMapper, ProjectBom> implements ProjectBomService {

    @Resource
    private ProduceServiceClient produceServiceClient;
    @Autowired
    private ProjectBomMapper projectBomMapper;
    @Autowired
    private ProductionBomService productionBomService;

    @Override
    public boolean deleteBom(String id, String workPlanNo, String tenantId, String branchCode, String drawingNo) {
        //处理逻辑 重写接口   zhiqiang.lu   2023.1.4
        int count = produceServiceClient.queryCountByWorkNo(id);
        if (count > 0) {
            throw new GlobalException("BOM已被使用", ResultCode.FAILED);
        }
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        //处理删除逻辑  zhiqiang.lu   2023.1.4
        queryWrapper
                .and(wrapper -> DrawingNoUtil.queryReturn(wrapper, "drawing_no", drawingNo).eq("grade", "H").or(wrapper2 -> DrawingNoUtil.queryReturn(wrapper2, "main_drawing_no", drawingNo).eq("grade", "L")))
                .eq("work_plan_no", workPlanNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean updateBom(ProjectBom projectBom) {
        return this.updateById(projectBom);
    }

    @Override
    public IPage<ProjectBom> getProjectBomPage(String drawingNo, String projectName, String prodDesc, String workPlanNo, String state, String tenantId, String branchCode, String order, String orderCol, String publishState, int page, int limit) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(projectName)) {
            queryWrapper.like("project_name", projectName);
        }
        if (!StringUtils.isNullOrEmpty(prodDesc)) {
            queryWrapper.like("prod_desc", prodDesc);
        }
        if (!StringUtils.isNullOrEmpty(workPlanNo)) {
            queryWrapper.like("work_plan_no", workPlanNo);
        }
        if (!StringUtils.isNullOrEmpty(state)) {
            queryWrapper.eq("state", state);
        }
        if (!StringUtils.isNullOrEmpty(publishState)) {
            queryWrapper.eq("publish_state", publishState);
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
            queryWrapper.orderByDesc("modify_time");
        }
        return this.page(new Page<>(page, limit), queryWrapper);
    }

    @Override
    public List<ProjectBom> getProjectBomList(String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        queryWrapper.eq("tenant_id", tenantId)
                .eq("branch_code", branchCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProjectBom> getProjectBomPartList(String workPlanNo, String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", workPlanNo)
                .eq("tenant_id", tenantId)
                .eq("branch_code", branchCode)
                .orderByAsc("order_no");
        DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", drawingNo);
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
                ProjectBom projectBom = this.getById(project.getBomKey());
                if (null != projectBom) {
                    QueryWrapper<ProjectBom> queryWrapperPart = new QueryWrapper<>();
                    queryWrapperPart.eq("work_plan_no", projectBom.getWorkPlanNo());
                    DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
                    List<ProjectBom> projectBoms = this.list(queryWrapperPart);
                    for (ProjectBom boms : projectBoms) {
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
    public List<ProjectBom> getProjectBomPartByIdList(String id) {
        ProjectBom bom = this.getById(id);
        if (null == bom) {
            return Collections.emptyList();
        }
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", bom.getDrawingNo());
        queryWrapper.eq("work_plan_no", bom.getWorkPlanNo())
//                .notIn("grade", "H")
                .eq("tenant_id", bom.getTenantId())
                .eq("branch_code", bom.getBranchCode())
                .orderByAsc("order_no");
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
                    DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", bom.getDrawingNo());
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
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePartAndAssembly(String id) {
        ProjectBom projectBom = this.getById(id);
        produceServiceClient.deleteAssemblyByBomId(id, projectBom.getTenantId(), projectBom.getBranchCode());
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBomAndAssembly(ProjectBom projectBom) {
        projectBom.setGrade("L");
        QueryWrapper<ProjectBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_plan_no", projectBom.getWorkPlanNo());
        projectBom.setOrderNo(this.count(queryWrapper));
        boolean result = this.save(projectBom);
        if (!result) {
            throw new GlobalException("保存失败！", ResultCode.FAILED);
        }
        //先找到主项目bom信息
        QueryWrapper<ProjectBom> mainBomWrapper = new QueryWrapper<>();
        mainBomWrapper.eq("drawing_no", projectBom.getMainDrawingNo())
                .eq("work_plan_no", projectBom.getWorkPlanNo())
                .eq("grade", "H").eq("tenant_id", projectBom.getTenantId())
                .eq("branch_code", projectBom.getBranchCode());
        ProjectBom mainBom = this.getOne(mainBomWrapper);
        if (mainBom != null) {
            List<TrackAssembly> addList = new ArrayList<>();
            //查找该bom绑定的跟单
            List<TrackHead> trackHeadList = produceServiceClient.getTrackHeadByProjectBomId(mainBom.getId(), mainBom.getTenantId(), mainBom.getBranchCode());
            if (CollectionUtils.isNotEmpty(trackHeadList)) {
                for (TrackHead trackHead : trackHeadList) {
                    //先根据trackHeadId获取装配信息列表
                    List<TrackAssembly> trackAssemblyList = produceServiceClient.getAssemblyListByTrackHeadId(trackHead.getId(), mainBom.getTenantId(), mainBom.getBranchCode());
                    if (CollectionUtils.isNotEmpty(trackAssemblyList)) {
                        Map<String, List<TrackAssembly>> map = trackAssemblyList.stream().collect(Collectors.groupingBy(TrackAssembly::getFlowId));
                        map.forEach((key, value) -> {
                            if (CollectionUtils.isNotEmpty(value)) {
                                //新增零件的装配记录
                                TrackAssembly trackAssembly = new TrackAssembly();
                                trackAssembly.setGrade(projectBom.getGrade());
                                trackAssembly.setName(projectBom.getProdDesc());
                                trackAssembly.setDrawingNo(projectBom.getDrawingNo());
                                trackAssembly.setMaterialNo(projectBom.getMaterialNo());
                                trackAssembly.setTrackHeadId(trackHead.getId());
                                trackAssembly.setNumber(trackHead.getNumber() == null || projectBom.getNumber() == null ? 0 : trackHead.getNumber() * projectBom.getNumber());
                                trackAssembly.setIsKeyPart(projectBom.getIsKeyPart());
                                trackAssembly.setTrackType(projectBom.getTrackType());
                                if (projectBom.getWeight() != null) {
                                    trackAssembly.setWeight(Double.valueOf(projectBom.getWeight()));
                                }
                                trackAssembly.setIsCheck(projectBom.getIsCheck());
                                trackAssembly.setIsEdgeStore(projectBom.getIsEdgeStore());
                                trackAssembly.setIsNeedPicking(projectBom.getIsNeedPicking());
                                trackAssembly.setUnit(projectBom.getUnit());
                                trackAssembly.setSourceType(projectBom.getSourceType());
                                trackAssembly.setIsNumFrom(projectBom.getIsNumFrom());
                                trackAssembly.setOptName(projectBom.getOptName());
                                trackAssembly.setFlowId(key);
                                trackAssembly.setBranchCode(projectBom.getBranchCode());
                                trackAssembly.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                                trackAssembly.setSourceType(mainBom.getSourceType());
                                trackAssembly.setProjectBomId(projectBom.getId());
                                trackAssembly.setOptName(projectBom.getOptName());
                                trackAssembly.setTrackNo(trackHead.getTrackNo());
                                addList.add(trackAssembly);
                            }
                        });
                    }
                }
                if (CollectionUtils.isNotEmpty(addList)) {
                    produceServiceClient.addAssemblyList(addList);
                }
            }
        }
        return true;
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
        queryWrapper.orderByAsc("create_time");
        List<ProjectBom> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void exportExcel(List<String> idList, HttpServletResponse rsp) {
        ClassPathResource classPathResource = new ClassPathResource("excel/" + "ProductBomExportTemp.xls");
        int sheetNum = 0;
        try {
            ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
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
                DrawingNoUtil.queryEq(queryWrapper, "main_drawing_no", projectBom.getDrawingNo());
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
            rsp.setHeader("Content-disposition", "attachment; filename=" + new String("项目BOM".getBytes("utf-8"),
                    "ISO-8859-1") + ".xls");
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Boolean publishBom(List<String> ids, Integer publishState) {
        List<ProjectBom> projectBoms = this.listByIds(ids);
        for (ProjectBom projectBom : projectBoms) {
            projectBom.setPublishState(publishState);
        }
        this.updateBatchById(projectBoms);
        return true;
    }

    @Override
    public Map<String, Object> bindingBom(List<TrackHead> trackHeads) {
        Map<String, Object> result = new HashMap<>();
        List<TrackHead> trackHeadList = new ArrayList<>();
        List<String> noBomIds = new ArrayList<>();
        Map<String, String> projectBomMap = new HashMap<>();
        for (TrackHead trackHead : trackHeads) {
            ProjectBom bom = projectBomMapper.selectBomByDrawNoAndWorkNo(trackHead.getDrawingNo(), trackHead.getWorkNo(), trackHead.getTenantId(), trackHead.getBranchCode());
            if (bom != null) {
                trackHead.setProjectBomId(bom.getId());
                trackHead.setProjectBomWork(bom.getWorkPlanNo());
                trackHead.setProjectBomName(bom.getProjectName());
                trackHeadList.add(trackHead);
            } else {
                //根据图号找到产品bom并用grade = "H"创建项目bom
                QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("drawing_no", trackHead.getDrawingNo()).eq("tenant_id", trackHead.getTenantId()).eq("grade", "H");
                ProductionBom productionBom = productionBomService.getOne(queryWrapper);
                if (productionBom != null) {
                    ProjectBom projectBom = projectBomEntity(productionBom);
                    projectBom.setTenantId(trackHead.getTenantId()).setBranchCode(trackHead.getBranchCode()).setProjectName(trackHead.getProductName() == null ? trackHead.getDrawingNo() + "_" + trackHead.getWorkNo() : trackHead.getProductName()).setWorkPlanNo(trackHead.getWorkNo()).setIsResolution("0");
                    this.save(projectBom);
                    projectBomMap.put(trackHead.getId(), projectBom.getDrawingNo());

                }
                noBomIds.add(trackHead.getId());
            }
        }
        //绑定已有bom的跟单
        produceServiceClient.updateBatch(trackHeadList);
        result.put("noBomIds", noBomIds);
        result.put("projectBomMap", projectBomMap);
        return result;
    }

    @Override
    public boolean saveBomList(List<ProjectBom> bomList) {
        return this.saveBatch(bomList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBomAndAssembly(ProjectBom projectBom) {
        ProjectBom beforeBom = this.getById(projectBom.getId());
        //根据projectBomId去装配表获取装配信息List
        List<TrackAssembly> assemblyList = produceServiceClient.getAssemblyListByProjectBomId(beforeBom.getId(), beforeBom.getTenantId(), beforeBom.getBranchCode());
        List<TrackAssembly> updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assemblyList)) {
            for (TrackAssembly trackAssembly : assemblyList) {
                //根据followId查询该跟单是否完工
                if (trackAssembly.getFlowId() != null) {
                    TrackFlow flowInfoById = produceServiceClient.getFlowInfoById(trackAssembly.getFlowId());
                    //跟单不为完成状态则修改装配信息
                    if (flowInfoById != null && !"2".equals(flowInfoById.getStatus())) {
                        trackAssembly.setDrawingNo(projectBom.getDrawingNo());
                        trackAssembly.setMaterialNo(projectBom.getMaterialNo());
                        trackAssembly.setName(projectBom.getProdDesc());
                        trackAssembly.setSourceType(projectBom.getSourceType());
                        trackAssembly.setNumber(projectBom.getNumber());
                        trackAssembly.setUnit(projectBom.getUnit());
                        trackAssembly.setWeight(projectBom.getWeight() == null ? 0.0 : Double.parseDouble(projectBom.getWeight().toString()));
                        trackAssembly.setIsKeyPart(projectBom.getIsKeyPart());
                        trackAssembly.setIsNeedPicking(projectBom.getIsNeedPicking());
                        trackAssembly.setIsEdgeStore(projectBom.getIsEdgeStore());
                        trackAssembly.setTrackType(projectBom.getTrackType());
                        trackAssembly.setIsNumFrom(projectBom.getIsNumFrom());
                        trackAssembly.setIsCheck(projectBom.getIsCheck());

                        updateList.add(trackAssembly);
                    }
                }
            }
            produceServiceClient.updateAssembly(updateList);
        }
        return this.updateById(projectBom);
    }

}
