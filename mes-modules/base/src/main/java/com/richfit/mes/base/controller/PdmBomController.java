package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.base.service.PdmBomService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmBom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private PdmBomService pdmBomService;

    @GetMapping(value = "/getBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getList(String id, String ver) {
        ArrayList<PdmBom> pdmBoms = new ArrayList<>();
        PdmBom pdmBom = pdmBomService.getBomByProcessIdAndRev(id, ver);
        if (pdmBom != null) {
            pdmBoms.add(pdmBom);
        }
        return CommonResult.success(pdmBoms);
    }

    @PostMapping(value = "/getChildBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getChildBomList(String id, String ver) {
        String opId = id + "@" + ver;
        QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("p_id", opId).orderByAsc("order_no+1");
        return CommonResult.success(pdmBomService.list(queryWrapper));
    }

    @Value("${excelTemp.pdmBomUrl}")
    private String bomUrl;

    @GetMapping("/exportBom")
    @ApiOperation(value = "导出产品BOM", notes = "根据模板导出产品BOM")
    public void exportBom(String drawingNo, String ver, String dataGroup, HttpServletResponse rsp) {
        try {
            ClassPathResource resource = new ClassPathResource(bomUrl);
            HSSFWorkbook wb = new HSSFWorkbook(resource.getInputStream());
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
                if (bom.getId().equals(drawingNo)) {
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
            rsp.setHeader("Content-disposition", String.format("attachment;filename=%s", URLEncoder.encode("产品BOM.xls", "UTF-8")));
            rsp.flushBuffer();
            wb.write(rsp.getOutputStream());

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


//    @GetMapping("download")
//    @ApiOperation(value = "导出产品BOM", notes = "导出产品BOM模板")
//    public static void main(String[] args, HttpServletResponse response) {
//        //获取根目录
//        String path = null;
//        try {
//            path = ResourceUtils.getURL("classpath:").getPath();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println("path:" + path);
//
//        File file = null;
//        try {
//            file = new File(ResourceUtils.getURL("classpath:").getPath());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println("file:" + file.getAbsolutePath());
//        //相对路径-封装文件
//        File upload = new File(file.getAbsolutePath(), "src/main/resources/excel/产品BOM导出模板2.xls");
//        System.out.println("upload url:" + upload.getAbsolutePath());
//        try {
//            //如果出现中文的问题可以进行如下的转码操作
//            System.out.println(URLDecoder.decode(upload.getAbsolutePath(), "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    @GetMapping("download")
    @ApiOperation(value = "文件下载")
    public void downloadArticle(HttpServletResponse response, @ApiParam(value = "模板名称", required = true) @RequestParam String fileName) {

        try {
            File file = ResourceUtils.getFile("classpath:excel/" + fileName);
            InputStream inputStream = new FileInputStream(file);
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            // 从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
