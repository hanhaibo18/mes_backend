package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.base.service.SequenceSiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import io.swagger.annotations.ApiImplicitParams;
import java.io.File;
import java.text.DateFormat.Field;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 马峰
 * @Description 工序工位关联关系Controller
 */
@Slf4j
@Api("工序工位关联关系管理")
@RestController
@RequestMapping("/api/base/sequencesite")
public class SequenceSiteController extends BaseController {

    @Autowired
    private SequenceSiteService sequenceSiteService;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "工序工位关联关系", notes = "工序工位关联关系")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "sequenceId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "siteId", value = "工位ID", required = true, paramType = "query", dataType = "string")        
    })
    @GetMapping("/page")
    public CommonResult<IPage<SequenceSite>> page(int page, int limit,String siteCode,  String sequenceId, String siteId) {
        try {
            
            QueryWrapper<SequenceSite> queryWrapper = new QueryWrapper<SequenceSite>();
            if (!StringUtils.isNullOrEmpty(sequenceId)) {
                queryWrapper.eq("sequence_id", sequenceId);
            }
            if (!StringUtils.isNullOrEmpty(siteId)) {
                queryWrapper.eq("site_id", siteId);
            }
             if (!StringUtils.isNullOrEmpty(siteCode)) {
                queryWrapper.eq("site_code", siteCode);
            }
             queryWrapper.orderByAsc("order_no");
            IPage<SequenceSite> routers = sequenceSiteService.page(new Page<SequenceSite>(page, limit),queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工序工位关联关系", notes = "新增工序工位关联关系")
    @ApiImplicitParam(name = "sequenceSite", value = "工序工位关联关系", required = true, dataType = "SequenceSite", paramType = "path")
    @PostMapping("/add")
    public CommonResult<SequenceSite> addSequenceSite(@RequestBody SequenceSite sequenceSite) {
        if (StringUtils.isNullOrEmpty(sequenceSite.getSequenceId())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            boolean bool = sequenceSiteService.save(sequenceSite);
            if (bool) {
                return CommonResult.success(sequenceSite, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改工序工位关联关系", notes = "修改工序工位关联关系")
    @ApiImplicitParam(name = "sequenceSite", value = "工序工位关联关系", required = true, dataType = "SequenceSite", paramType = "path")
    @PostMapping("/update")
    public CommonResult<SequenceSite> updateSequenceSite(@RequestBody SequenceSite sequenceSite) {
        if (StringUtils.isNullOrEmpty(sequenceSite.getSequenceId())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            sequenceSite.setModifyBy("test");
            sequenceSite.setModifyTime(new Date());
            boolean bool = sequenceSiteService.updateById(sequenceSite);
            if (bool) {
                return CommonResult.success(sequenceSite, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询工序工位关联关系", notes = "根据编码获得工序工位关联关系")
    @ApiImplicitParam(name = "sequenceId", value = "工序ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<SequenceSite>> find(String sequenceId, String siteId, String siteCode, String branchCode, String isDefault) {
        
            
            QueryWrapper<SequenceSite> queryWrapper = new QueryWrapper<SequenceSite>();
            if (!StringUtils.isNullOrEmpty(sequenceId)) {
                queryWrapper.eq("sequence_id", sequenceId);
            }
            if (!StringUtils.isNullOrEmpty(siteId)) {
                queryWrapper.eq("site_id", siteId);
            }
             if (!StringUtils.isNullOrEmpty(siteCode)) {
                queryWrapper.eq("site_code", siteCode);
            }
              if (!StringUtils.isNullOrEmpty(isDefault)) {
                queryWrapper.eq("is_default", Integer.parseInt(isDefault));
            }
             
              if(!StringUtils.isNullOrEmpty(branchCode)){
            queryWrapper.like("branch_code", "%" + branchCode + "%");
        } 
             queryWrapper.orderByAsc("order_no");
        List<SequenceSite> result = sequenceSiteService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    

    @ApiOperation(value = "删除工序工位关联关系", notes = "根据id删除工序工位关联关系")
    @ApiImplicitParam(name = "id", value = "ids", required = true, dataType = "String", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<SequenceSite> deleteById(@RequestBody String[] ids){
            
            boolean bool = sequenceSiteService.removeByIds(java.util.Arrays.asList(ids));
            if(bool){
                return CommonResult.success(null, "删除成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
       
    }
    
    @ApiOperation(value = "导入工序工位关联关系", notes = "根据Excel文档导入工序工位关联关系")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        java.lang.reflect.Field[] fields = SequenceSite.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for(int i=0;i<fields.length;i++)
        {
            fieldNames[i]= fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"),tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<SequenceSite> list =  ExcelUtils.importExcel(excelFile, SequenceSite.class, fieldNames, 1,0,0,tempName.toString());
            FileUtils.delete(excelFile);

           // list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
          

            boolean bool = sequenceSiteService.saveBatch(list);
            if(bool){
                return CommonResult.success("");
            } else {
                return CommonResult.failed();
            }
        }catch (Exception e) {
            return CommonResult.failed();
        }
    }
}
