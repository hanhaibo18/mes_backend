package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.service.RouterService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.base.service.SequenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.OperatiponService;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.ApiImplicitParams;
import java.io.File;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 马峰
 * @Description 工序Controller
 */
@Slf4j
@Api("工序管理")
@RestController
@RequestMapping("/api/base/sequence")
public class SequenceController extends BaseController {

    @Autowired
    private SequenceService sequenceService;
    @Autowired
    public RouterService routerService;
     @Autowired
    private OperatiponService operatiponService;
    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "工艺", notes = "工艺")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "optCode", value = "工序编码", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "optName", value = "工序名称", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Sequence>> page(int page, int limit, String routerId,String routerNo, String optCode, String optName) {
        try {
          
            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
            if (!StringUtils.isNullOrEmpty(routerId)) {
                queryWrapper.eq("router_id", routerId);
            } 
             if (!StringUtils.isNullOrEmpty(routerNo)) {
                queryWrapper.inSql("router_id","select id from base_router where router_no ='"+routerNo+"'");
            }
            if (!StringUtils.isNullOrEmpty(optCode)) {
                queryWrapper.eq("opt_code", optCode);
            }
            if (!StringUtils.isNullOrEmpty(optName)) {
                queryWrapper.eq("opt_name", optName);
            }
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
             queryWrapper.orderByAsc("opt_order");
            IPage<Sequence> routers = sequenceService.page(new Page<Sequence>(page, limit),queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工序", notes = "新增工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/add")
    public CommonResult<Sequence> addSequence(@RequestBody Sequence sequence) {
        if (StringUtils.isNullOrEmpty(sequence.getOptCode())) {
            return CommonResult.failed("编码不能为空！");
        } else {
             if(null!=SecurityUtils.getCurrentUser()) {
              String tenantId = SecurityUtils.getCurrentUser().getTenantId();
            sequence.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            sequence.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
           
            sequence.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
         
              }
               sequence.setCreateTime(new Date());
                  sequence.setModifyTime(new Date());
            boolean bool = sequenceService.save(sequence);
            if (bool) {
                return CommonResult.success(sequence, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改工序", notes = "修改工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/update")
    public CommonResult<Sequence> updateSequence(@RequestBody Sequence sequence) {
        if (StringUtils.isNullOrEmpty(sequence.getOptCode())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            if(null!=SecurityUtils.getCurrentUser()) {
             
            sequence.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        
           
            sequence.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
         
              }
             
                  sequence.setModifyTime(new Date());
            boolean bool = sequenceService.updateById(sequence);
            if (bool) {
                return CommonResult.success(sequence, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询工序", notes = "根据编码获得工序")
    @ApiImplicitParam(name = "sequenceCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Sequence>> find(String id,String optCode, String optName,String routerId) {
        QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
        if(!StringUtils.isNullOrEmpty(id)){
            queryWrapper.eq("id", id);
        }  
        if (!StringUtils.isNullOrEmpty(optCode)) {
            queryWrapper.like("opt_code", "%" + optCode + "%");
        }
        if(!StringUtils.isNullOrEmpty(optName)){
            queryWrapper.like("opt_name", "%" + optName + "%");
        }
                if(!StringUtils.isNullOrEmpty(optName)){
            queryWrapper.like("opt_name", "%" + optName + "%");
        }
            if(!StringUtils.isNullOrEmpty(routerId)){
            queryWrapper.like("router_id", "%" + routerId + "%");
        }    
            
         queryWrapper.orderByAsc("opt_order");
        List<Sequence> result = sequenceService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询工序", notes = "根据工艺ID获得工序")
    @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/getByRouterId")
    public CommonResult<List<Sequence>> getByRouterId(String routerId) {
        QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
        queryWrapper.eq("router_id", routerId);
         queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("opt_order");
        List<Sequence> result = sequenceService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

  
    
       @ApiOperation(value = "查询工序", notes = "根据工艺ID获得工序")
    @ApiImplicitParams({           
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, dataType = "String", paramType = "query"),       
           @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query")        
    }) 
    @GetMapping("/getByRouterNo")
    public CommonResult<List<Sequence>> getByRouterNo(String routerNo, String branchCode, String optId) {

       QueryWrapper<Router> query = new QueryWrapper<Router>();

       if (!StringUtils.isNullOrEmpty(routerNo)) {
           query.eq("router_no", routerNo);
       }
       if (!StringUtils.isNullOrEmpty(branchCode)) {
           query.like("branch_code", "%" + branchCode + "%");
       }
        query.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
           query.in("status", "1");
       List<Router> routers = routerService.list(query);
       if(routers.size() > 0){
           Router router = routers.get(0);
           QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
           queryWrapper.eq("router_id", router.getId());
           if (!StringUtils.isNullOrEmpty(optId)) {
           queryWrapper.eq("opt_id", optId);
           }
           queryWrapper.orderByAsc("opt_order");
           List<Sequence> result = sequenceService.list(queryWrapper);
           return CommonResult.success(result, "操作成功！");
       }


           return CommonResult.success(null, "操作成功！");
    }

    @ApiOperation(value = "删除工序", notes = "根据id删除工序")
    @ApiImplicitParam(name = "id", value = "ids", required = true, dataType = "String", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<Sequence> deleteById(@RequestBody String[] ids){
            
            boolean bool = sequenceService.removeByIds(java.util.Arrays.asList(ids));
            if(bool){
                return CommonResult.success(null, "删除成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
       
    }
    
    @ApiOperation(value = "导入工序", notes = "根据Excel文档导入工序")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        java.lang.reflect.Field[] fields = Sequence.class.getDeclaredFields();
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
            List<Sequence> list =  ExcelUtils.importExcel(excelFile, Sequence.class, fieldNames, 1,0,0,tempName.toString());
            FileUtils.delete(excelFile);
            String msg="";
            for (int i = 0; i < list.size(); i++) {
                 
                 if (null != SecurityUtils.getCurrentUser()) {
                    list.get(i).setTenantId(SecurityUtils.getCurrentUser().getTenantId());

                    list.get(i).setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                }
                 list.get(i).setStatus("1");
                List<Router> routers = routerService.list(new QueryWrapper<Router>().eq("router_no", list.get(i).getRouterId()).eq("status", "1").eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()));
                if(routers.size()>0) {
                     list.get(i).setRouterId(routers.get(0).getId());
                }
                else{
                    msg +="第"+(i+1)+"行:"+"找不到图号,";
                     list.get(i).setStatus("0");
                }
                List<Operatipon> opts = operatiponService.list(new QueryWrapper<Operatipon>().eq("opt_name", list.get(i).getOptName()).eq("opt_type",list.get(i).getOptType()));
                if(opts.size()>0) {
                     list.get(i).setOptId(opts.get(0).getId());
                     list.get(i).setOptCode(opts.get(0).getOptCode());
                }
                else{
                     msg +="第"+(i+1)+"行:"+"字典中无此工序名,";
                    list.get(i).setStatus("0");
                }
                if("是".equals( list.get(i).getIsQualityCheck())) {
                     list.get(i).setIsQualityCheck("1");
                }
                if("否".equals( list.get(i).getIsQualityCheck())) {
                      list.get(i).setIsQualityCheck("0");
                }
                if("是".equals( list.get(i).getIsScheduleCheck())) {
                     list.get(i).setIsScheduleCheck("1");
                }
                if("否".equals( list.get(i).getIsScheduleCheck())) {
                      list.get(i).setIsScheduleCheck("0");
                }
                 if("是".equals( list.get(i).getIsParallel())) {
                     list.get(i).setIsParallel("1");
                }
                if("否".equals( list.get(i).getIsParallel())) {
                      list.get(i).setIsParallel("0");
                }
                
            }
            if("".equals(msg)){
            boolean bool = sequenceService.saveBatch(list);
            if(bool){
                return CommonResult.success("");
            } else {
                return CommonResult.failed(msg);
            }
            }
            else{
                return CommonResult.failed(msg);
            }
        }catch (Exception e) {
            return CommonResult.failed();
        }
    }
}
