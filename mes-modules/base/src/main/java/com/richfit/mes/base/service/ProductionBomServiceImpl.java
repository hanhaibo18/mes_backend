package com.richfit.mes.base.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.richfit.mes.base.dao.ProductionBomMapper;
import com.richfit.mes.base.enmus.ProductBomExportEnum;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 产品BOM服务
 */
@Slf4j
@Service
public class ProductionBomServiceImpl extends ServiceImpl<ProductionBomMapper, ProductionBom> implements ProductionBomService {

    @Autowired
    private ProductionBomMapper productionBomMapper;

    @Resource
    private ProjectBomService projectBomService;

    public static String BOM_FAILED_MESSAGE = "操作失败，请重试！";
    public static String BOM_IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功!";
    public static String BOM_IMPORT_EXCEL_EXCEPTION_MESSAGE = "操作失败：";

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
    @Transactional(rollbackFor = Exception.class)
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
        queryWrapper.eq("branch_code", productionBom.getBranchCode());
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
        QueryWrapper<ProjectBom> query = new QueryWrapper<>();
        query.eq("drawing_no", drawingNo);
        query.eq("branch_code", branchCode);
        int count = projectBomService.count(query);
        if (count > 0) {
            throw new GlobalException("该BOM已被发布,删除失败!", ResultCode.FAILED);
        }
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
        if (!StringUtils.isEmpty(drawingNo)) {
            query.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isEmpty(branchCode)) {
            query.eq("branch_code", branchCode);
        }
        query.eq("grade", "H");
        query.eq("tenant_id", tenantId);
        if (!StringUtils.isEmpty(orderCol)) {
            if (!StringUtils.isEmpty(order)) {
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
        ClassPathResource classPathResource = new ClassPathResource("excel/" + "ProductBomExportTemp.xls");
        int sheetNum = 0;
        try {
            ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
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
                writer.resetRow();
                writer.passRows(4);
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
            rsp.setContentType("application/octet-stream");
            rsp.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("产品BOM", "UTF-8"));
            ServletOutputStream outputStream = rsp.getOutputStream();
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据erp模板导出产品bom
     *
     * @param id
     * @param rsp
     */
    @Override
    public void exportExcelERP(String id, HttpServletResponse rsp) {
        ClassPathResource classPathResource = new ClassPathResource("excel/" + "ProductErpBomExportTemp.xls");
        int sheetNum = 0;
        try {
            ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
            HSSFWorkbook wk = (HSSFWorkbook) writer.getWorkbook();
            if (sheetNum > 0) {
                writer.setSheet(wk.cloneSheet(0));
            }
            //根节点
            ProductionBom productionBom = this.getById(id);
            //sheet名称
            //writer.renameSheet(productionBom.getDrawingNo());
            writer.resetRow();
            //从第三行开始
            writer.passRows(2);
            QueryWrapper<ProductionBom> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("drawing_no", productionBom.getDrawingNo());
            queryWrapper.or();
            queryWrapper.eq("main_drawing_no", productionBom.getDrawingNo());
            queryWrapper.eq("branch_code", productionBom.getBranchCode());
            queryWrapper.orderByAsc("order_no");
            //所以子节点
            List<ProductionBom> productionBomList = this.list(queryWrapper);
            //排除根节点
            productionBomList = productionBomList.stream().filter(t -> t.getOrderNo() != 0).collect(Collectors.toList());

            int currentRow = writer.getCurrentRow();
            for (ProductionBom bom : productionBomList) {
                writer.writeCellValue(0, currentRow, productionBom.getMaterialNo());
                writer.writeCellValue(1, currentRow, "");
                writer.writeCellValue(2, currentRow, "");
                //数量
                writer.writeCellValue(3, currentRow, productionBom.getNumber());
                //停用发布
                writer.writeCellValue(4, currentRow, "");
                writer.writeCellValue(5, currentRow, productionBom.getTexture());
                writer.writeCellValue(6, currentRow, productionBom.getUnit());
                writer.writeCellValue(7, currentRow, bom.getOrderNo());
                writer.writeCellValue(8, currentRow, bom.getGrade());
                writer.writeCellValue(9, currentRow, bom.getMaterialNo());
                writer.writeCellValue(10, currentRow, bom.getNumber());
                writer.writeCellValue(11, currentRow, bom.getUnit());
                writer.writeCellValue(12, currentRow, "");
                writer.writeCellValue(13, currentRow, "");
                writer.writeCellValue(14, currentRow, "");
                writer.writeCellValue(15, currentRow, "");
                currentRow++;
            }
            rsp.setContentType("application/vnd.ms-excel;charset=utf-8");
            rsp.setHeader("Content-disposition", "attachment; filename=" + new String(productionBom.getDrawingNo().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.ISO_8859_1) + ".xls");
            ServletOutputStream outputStream = rsp.getOutputStream();
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private ProjectBom projectBomEntity(ProductionBom productionBom) {
        ProjectBom projectBom = new ProjectBom();
        projectBom.setDrawingNo(productionBom.getDrawingNo())
                .setMaterialNo(productionBom.getMaterialNo())
                .setTexture(productionBom.getTexture())
                .setWeight(productionBom.getWeight())
                .setMainDrawingNo(productionBom.getMainDrawingNo())
                .setUnit(productionBom.getUnit())
                .setIsNumFrom(productionBom.getIsNumFrom())
                .setIsKeyPart(productionBom.getIsKeyPart())
                .setIsNeedPicking(productionBom.getIsNeedPicking())
                .setIsEdgeStore(productionBom.getIsEdgeStore())
                .setIsCheck(productionBom.getIsCheck())
                .setOptName(productionBom.getOptName())
                .setGrade(productionBom.getGrade())
                .setTrackType(productionBom.getTrackType())
                .setNumber(productionBom.getNumber())
                .setBomKey(productionBom.getBomKey())
                .setSourceType(productionBom.getSourceType())
                .setOrderNo(productionBom.getOrderNo())
                .setProdDesc(productionBom.getProdDesc());
        return projectBom;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult newImportExcel(@RequestParam("file") MultipartFile file) throws IOException {

        //封装证件信息实体类
        String[] fieldNames = {"isImport", "orderNo", "branchCode", "grade", "mainDrawingNo", "drawingNo",
                "materialNo", "prodDesc", "sourceType", "weight", "texture", "number", "unit", "optName",
                "trackType", "isNumFrom", "isNeedPicking", "isKeyPart", "isEdgeStore", "isCheck", "remark"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成产品BOM实体类list
            List<ProductionBom> list = ExcelUtils.importExcel(excelFile, ProductionBom.class, fieldNames, 4, 0, 0, tempName.toString());

            //表头产品号(用于校验)
            String drawingNo = String.valueOf(ExcelUtils.getCellValue(excelFile,String.class,0,1,7,tempName.toString()));
            //表头物料编码(用于校验)
            String materialNo = String.valueOf(ExcelUtils.getCellValue(excelFile,String.class,0,1,10,tempName.toString()));
            //导入校验
            if(!StringUtils.isEmpty(checkExportList(list, drawingNo, materialNo))){
                return CommonResult.failed("产品bom导入校验错误如下：</br>"+checkExportList(list, drawingNo, materialNo));
            }


            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> !StringUtils.isEmpty(item.getDrawingNo()) &&
                    !StringUtils.isEmpty(item.getMaterialNo())).collect(Collectors.toList());
            String tenantId = SecurityUtils.getCurrentUser().getTenantId();
            list.forEach(item -> {
                item.setTenantId(tenantId);
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
                if ("单件".equals(item.getTrackType())) {
                    item.setTrackType("0");
                } else if ("批次".equals(item.getTrackType())) {
                    item.setTrackType("1");
                }
                if ("否".equals(item.getIsNumFrom())) {
                    item.setIsNumFrom("0");
                } else if ("是".equals(item.getIsNumFrom())) {
                    item.setIsNumFrom("1");
                }
                if ("否".equals(item.getIsCheck())) {
                    item.setIsCheck("0");
                } else if ("是".equals(item.getIsCheck())) {
                    item.setIsCheck("1");
                }
                if ("否".equals(item.getIsEdgeStore())) {
                    item.setIsEdgeStore("0");
                } else if ("是".equals(item.getIsEdgeStore())) {
                    item.setIsEdgeStore("1");
                }
                if ("否".equals(item.getIsNeedPicking())) {
                    item.setIsNeedPicking("0");
                } else if ("是".equals(item.getIsNeedPicking())) {
                    item.setIsNeedPicking("1");
                }
                if ("否".equals(item.getIsKeyPart())) {
                    item.setIsKeyPart("0");
                } else if ("是".equals(item.getIsKeyPart())) {
                    item.setIsKeyPart("1");
                }
            });
            if (!StringUtils.isEmpty(list.get(0).getMainDrawingNo())) {
                this.deleteBom(list.get(0).getMainDrawingNo(), tenantId, list.get(0).getBranchCode());
            } else if (!StringUtils.isEmpty(list.get(0).getDrawingNo())) {
                this.deleteBom(list.get(0).getDrawingNo(), tenantId, list.get(0).getBranchCode());
            }
            boolean bool = this.saveBatch(list);
            if (bool) {
                return CommonResult.success(null, BOM_IMPORT_EXCEL_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BOM_FAILED_MESSAGE);
            }
        } catch (Exception e) {
            return CommonResult.failed(BOM_IMPORT_EXCEL_EXCEPTION_MESSAGE + e.getMessage());
        }
    }

    @Autowired
    private ProductService productService;

    /**
     * 产品bom导入校验
     * @param list
     * @return
     */
    private String checkExportList(List<ProductionBom> list,String drawingNo,String materialNo){
        StringBuilder message = new StringBuilder();
        //过滤要导入的数据
        list = list.stream().filter(item -> {
            return StringUtils.equals(item.getIsImport(),"X");
        }).collect(Collectors.toList());
        //1、空值校验
        nullValueCheck(list, message);
        if(StringUtils.isEmpty(drawingNo)){
            message.append("产品图号不能为空；</br>");
        }
        if(StringUtils.isEmpty(materialNo)){
            message.append("物料编码不能为空；</br>");
        }
        //2、校验H层行数
        List<ProductionBom> hCheckList = list.stream().filter(item -> {
            return "H".equals(item.getGrade());
        }).collect(Collectors.toList());
        if(hCheckList.size()!=1){
            message.append("级别为H的行数不是一行，导入失败；</br>");
        }
        //3、校验L层行数
        List<ProductionBom> lCheckList = list.stream().filter(item -> {
            return "L".equals(item.getGrade());
        }).collect(Collectors.toList());
        if(lCheckList.size()==0){
            message.append("导入文件没有级别为L的行，导入失败；</br>");
        }
        //4、校验表头信息与H层一致
        if(hCheckList.size()>0){
            if(!StringUtils.equals(drawingNo,hCheckList.get(0).getDrawingNo())){
                message.append("表头产品图号与H层零部件图号不符，导入失败；</br>");
            }
            if(!StringUtils.equals(materialNo,hCheckList.get(0).getMaterialNo())){
                message.append("表头物料编码与H层物料编码不符，导入失败；</br>");
            }
        }
        //5、上级图号取表头产品图号或H层的物料图号校验
        if(hCheckList.size()>0 && lCheckList.size()>0){
            List<ProductionBom> collect = lCheckList.stream().filter(item -> !StringUtils.equals(hCheckList.get(0).getDrawingNo(), hCheckList.get(0).getMainDrawingNo())).collect(Collectors.toList());
            if(collect.size()!=lCheckList.size()){
                message.append("上级图号需要取表头产品图号或H层的物料图号，导入失败；</br>");
            }
        }
        //6、零部件校验
        List<String> drawNoAndMaterNoList = new ArrayList<>(list.stream().map(item -> item.getDrawingNo() + "&" + item.getMaterialNo()).collect(Collectors.toSet()));

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId())
                .in("CONCAT_WS('&',drawing_no,material_no)",drawNoAndMaterNoList);
        //本地存在的物料
        List<Product> materials = productService.list(queryWrapper);
        List<String> localInfo = new ArrayList<>(materials.stream().map(item -> item.getDrawingNo() + "&" + item.getMaterialNo()).collect(Collectors.toSet()));
        //根据图号和物料号校验物料提示信息
        StringBuilder materialExitInfo = new StringBuilder();

        for (String drawNoAndMaterNo : drawNoAndMaterNoList) {
            //本地不存在 提示物料不存在
            if(!localInfo.contains(drawNoAndMaterNo)){
                if(!StringUtils.isEmpty(String.valueOf(materialExitInfo))){
                    materialExitInfo.append("、");
                }
                materialExitInfo.append(drawNoAndMaterNo.split("&")[0]);
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(materialExitInfo))){
            message.append("图号："+materialExitInfo+" 的零部件不存在；");
        }

        return String.valueOf(message);
    }

    /**
     * 导入空值校验
     * @param list
     * @param message
     */
    private void nullValueCheck(List<ProductionBom> list, StringBuilder message) {

        List<String> nullStringList = new ArrayList<>();

        for (ProductionBom productionBom : list) {
            //转换map 便于枚举取值
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(productionBom));

            jsonObject.forEach((key,value)->{
                //判断是不是要校验的字段
                if(!ObjectUtil.isEmpty(ProductBomExportEnum.getName(key))){
                    if(StringUtils.isEmpty(String.valueOf(value))){
                        String name = ProductBomExportEnum.getName(key);
                        //H行不校验上级产品图号
                        if("H".equals(productionBom.getGrade()) && ProductBomExportEnum.mainDrawingNo.getCode().equals(key)){
                            return;
                        }
                        if(!nullStringList.contains(name)){
                            nullStringList.add(name);
                        }
                    }
                }
            });
        }
        if(nullStringList.size()>0){
            message.append(String.join(",", nullStringList)+"；</br>");
        }
    }
}
