package com.richfit.mes.base.controller;

import cn.hutool.core.io.resource.ClassPathResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmBomService;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.base.service.PdmProcessService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmBom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rzw
 * @date 2022-01-12 13:27
 */
@Slf4j
@Api("图纸")
@RestController
@RequestMapping("/api/base/pdmBom")
public class PdmBomController {

    @Autowired
    private PdmProcessService pdmProcessService;

    @Autowired
    private PdmBomService pdmBomService;

    @GetMapping(value = "/getBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getList(String id, String ver){
        ArrayList<PdmBom> pdmBoms = new ArrayList<>();
        pdmBoms.add(pdmBomService.getBomByProcessIdAndRev(id, ver));
        return CommonResult.success(pdmBoms);
    }

    @PostMapping(value = "/getChildBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getChildBomList(String id, String ver){
        String opId = id + "@" + ver;
        QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("p_id",opId)
        .orderByAsc("order_no+1");
        return CommonResult.success(pdmBomService.list(queryWrapper));
    }

    @Value("${excelTemp.pdmBomUrl}")
    private String bomUrl;

    @GetMapping("/exportBom")
    @ApiOperation(value = "导出产品BOM", notes = "根据模板导出产品BOM")
    public void exportBom(String drawingNo, String ver, String dataGroup,  HttpServletResponse rsp) {
        try {
            ClassPathResource resource = new ClassPathResource(bomUrl);
            HSSFWorkbook wb = new HSSFWorkbook(resource.getStream());
            HSSFSheet sheet = wb.getSheet("泵业制造BOM");
            QueryWrapper<PdmBom> query = new QueryWrapper<>();
            String pId = drawingNo + "@" + ver;
            query.and(wrapper -> wrapper.and(w -> w.eq("id", drawingNo).eq("ver", ver)).or().eq("p_id", pId));
            query.eq("datagroup", dataGroup);
            query.orderByAsc("order_no");
            List<PdmBom> result = pdmBomService.list(query);
            HSSFRow drawTitle = sheet.getRow(1);
            drawTitle.getCell(7).setCellValue(drawingNo);
            int index = 4;
            for (PdmBom bom : result) {
                HSSFRow row = sheet.getRow(index);
                row.getCell(1).setCellValue(bom.getOrderNo());
                row.getCell(2).setCellValue(bom.getDataGroup());
                if(bom.getId().equals(drawingNo)) {
                    row.getCell(3).setCellValue("H");
                    drawTitle.getCell(9).setCellValue(bom.getMateriaNo());
                    HSSFRow nameTitle = sheet.getRow(2);
                    nameTitle.getCell(7).setCellValue(bom.getName());
                } else {
                    row.getCell(3).setCellValue("L");
                    row.getCell(4).setCellValue(drawingNo);
                }
                row.getCell(5).setCellValue(bom.getId());
                row.getCell(6).setCellValue(bom.getMateriaNo());
                row.getCell(7).setCellValue(bom.getName());
                row.getCell(8).setCellValue(bom.getObjectType());
                row.getCell(9).setCellValue(bom.getMateriaWeight());
                row.getCell(10).setCellValue(bom.getMateriaName());
                row.getCell(11).setCellValue(bom.getQuantity());
                row.getCell(14).setCellValue("无");
                row.getCell(15).setCellValue("否");
                row.getCell(16).setCellValue("是");
                row.getCell(17).setCellValue("是");
                row.getCell(18).setCellValue("是");
                row.getCell(19).setCellValue("是");
                index++;
            }

            rsp.setCharacterEncoding("UTF-8");
            rsp.setContentType("application/octet-stream");
            //默认Excel名称
            rsp.setHeader("Content-disposition",
                    String.format("attachment;filename=%s", URLEncoder.encode("产品BOM.xls", "UTF-8")));
            rsp.flushBuffer();
            wb.write(rsp.getOutputStream());

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
