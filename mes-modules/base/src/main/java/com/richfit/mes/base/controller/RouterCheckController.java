package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.entity.RouterCheckDto;
import com.richfit.mes.base.entity.RouterCheckQualityDto;
import com.richfit.mes.base.service.RouterCheckService;
import com.richfit.mes.base.service.SequenceService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 马峰
 * @Description 技术要求Controller
 */
@Slf4j
@Api("工序技术要求")
@RestController
@RequestMapping("/api/base/routerCheck")
public class RouterCheckController extends BaseController {

    @Autowired
    private RouterCheckService routerCheckService;

    @Autowired
    private SequenceService sequenceService;

    /**
     * 功能描述: 列表查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/14 11:37
     **/
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @GetMapping("/list")
    public CommonResult<List<RouterCheck>> list(@ApiParam(value = "工序id", required = true) @RequestParam String sequenceId,
                                                @ApiParam(value = "类型") @RequestParam(required = false) String type,
                                                @ApiParam(value = "状态") @RequestParam(required = false) String status) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            queryWrapper.eq("sequence_id", sequenceId);
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            queryWrapper.orderByAsc("check_order");
            return CommonResult.success(routerCheckService.list(queryWrapper));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 功能描述: 更新质量资料
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/19 11:37
     **/
    @ApiOperation(value = "更新质量资料", notes = "更新质量资料")
    @PostMapping("/update_zlzl")
    public void updateZlzl(@ApiParam(value = "质量资料列表", required = true) @RequestBody List<RouterCheck> routerChecks) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            RouterCheck rc = routerChecks.get(0);
            QueryWrapper<RouterCheck> queryWrapperRouterCheck = new QueryWrapper<>();
            queryWrapperRouterCheck.eq("sequence_id", rc.getSequenceId());
            queryWrapperRouterCheck.eq("type", "质量资料");
            queryWrapperRouterCheck.eq("branch_code", rc.getBranchCode());
            queryWrapperRouterCheck.eq("tenant_id", user.getTenantId());
            routerCheckService.remove(queryWrapperRouterCheck);
            for (RouterCheck routerCheck : routerChecks) {
                routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                routerCheck.setType("质量资料");
                routerCheck.setStatus("1");
                routerCheck.setTenantId(user.getTenantId());
                routerCheck.setCreateTime(new Date());
                routerCheck.setCreateBy(user.getUsername());
                routerCheck.setModifyTime(new Date());
                routerCheck.setModifyBy(user.getUsername());
                routerCheckService.save(routerCheck);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("更新质量资料异常");
        }
    }


    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<RouterCheck>> page(int page, int limit, String sequenceId, String name, String drawingNo, String id, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            if (!StringUtils.isNullOrEmpty(sequenceId)) {
                queryWrapper.eq("sequence_id", sequenceId);
            } else {
                queryWrapper.eq("sequence_id", "-1");
            }
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            if (!StringUtils.isNullOrEmpty(name)) {
                queryWrapper.like("name", name);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                queryWrapper.like("drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("id", id);
            }
            queryWrapper.orderByAsc("check_order");
            IPage<RouterCheck> routerChecks = routerCheckService.page(new Page<RouterCheck>(page, limit), queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    /**
     * ***
     * 分页查询
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/find")
    public CommonResult<List<RouterCheck>> find(String drawingNo, String optId, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            queryWrapper.apply("sequence_id in (select opt_id from base_sequence where id = '" + optId + "') and router_id in (select id from base_router where router_no = '" + drawingNo + "' and status ='1')");

            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            queryWrapper.orderByAsc("check_order");
            List<RouterCheck> routerChecks = routerCheckService.list(queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增技术要求", notes = "新增技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "query")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> addRouterCheck(@RequestBody RouterCheck routerCheck) {

        if (StringUtils.isNullOrEmpty(routerCheck.getName())) {
            return CommonResult.failed("名称不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            routerCheck.setCreateBy(user.getUsername());
            routerCheck.setCreateTime(new Date());
            routerCheck.setModifyBy(user.getUsername());
            routerCheck.setModifyTime(new Date());
            routerCheck.setTenantId(user.getTenantId());
            boolean bool = routerCheckService.save(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改技术要求", notes = "修改技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> updateRouterCheck(@RequestBody RouterCheck routerCheck) {
        if (StringUtils.isNullOrEmpty(routerCheck.getId())) {
            return CommonResult.failed("ID不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            routerCheck.setModifyBy(user.getUsername());
            routerCheck.setModifyTime(new Date());
            routerCheck.setTenantId(user.getTenantId());
            boolean bool = routerCheckService.updateById(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }


    @ApiOperation(value = "删除技术要求", notes = "根据id删除技术要求")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> delete(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {

            routerCheckService.removeById(ids[i]);
        }
        return CommonResult.success(null, "删除成功！");

    }

    @ApiOperation(value = "导入工艺质量资料", notes = "根据Excel文档导入工艺质量资料")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "query")
    @PostMapping("/import_excel_check")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult importExcelCheck(@RequestParam("file") MultipartFile file, String tenantId, String branchCode) {
        String step = "";
        List<RouterCheckDto> list = new ArrayList<>();
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        try {
            File excelFile = null;
            //给导入的excel一个临时的文件名
            StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
            tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            try {
                //将导入的excel数据生成证件实体类list
                java.lang.reflect.Field[] fields = RouterCheckDto.class.getDeclaredFields();
                //封装证件信息实体类
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                List<RouterCheckDto> checkList = ExcelUtils.importExcel(excelFile, RouterCheckDto.class, fieldNames, 1, 0, 0, tempName.toString());
                step += "获取列表成功";
                List<RouterCheckDto> list2 = new ArrayList<>();

                // 获取图号列表
                String drawnos = "";
                for (int i = 0; i < checkList.size(); i++) {
                    if (!StringUtils.isNullOrEmpty(checkList.get(i).getRouterNo())) {
                        list2.add(checkList.get(i));
                    }
                    if (!drawnos.contains(checkList.get(i).getRouterNo() + ",")) {
                        drawnos += checkList.get(i).getRouterNo() + ",";
                    }
                }
                step += "获取图号成功";

                checkList = list2;
                list = checkList;
                // 遍历图号插入检查内容
                for (int i = 0; i < drawnos.split(",").length; i++) {
                    // 先删除历史数据
                    QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
                    queryWrapper.eq("type", "检查内容");
                    queryWrapper.eq("tenant_id", tenantId);
                    queryWrapper.eq("branch_code", branchCode);
                    queryWrapper.eq("drawing_no", drawnos.split(",")[i]);
                    routerCheckService.remove(queryWrapper);
                    int check_order = 1;


                    for (int j = 0; j < checkList.size(); j++) {
                        // 插入新数据

                        QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<Sequence>();
                        queryWrapper2.eq("opt_name", checkList.get(j).getOptName().trim());
                        queryWrapper2.eq("tenant_id", tenantId);
                        queryWrapper2.eq("branch_code", branchCode);
                        queryWrapper.inSql("router_id", "select id from base_router where is_active='1' and router_no ='" + drawnos.split(",")[i] + "' and branch_code='" + branchCode + "'");
                        List<Sequence> sequences = sequenceService.list(queryWrapper2);
                        if (sequences.size() > 1) {
                            step += sequences.get(0).getRouterId() + sequences.get(0).getId() + checkList.get(j).getOptName();
                            if (checkList.get(j).getRouterNo().equals(drawnos.split(",")[i])) {
                                RouterCheck routerCheck = new RouterCheck();
                                routerCheck.setCreateBy(user.getUsername());
                                routerCheck.setRouterId(sequences.get(0).getRouterId());
                                routerCheck.setSequenceId(sequences.get(0).getId());
                                routerCheck.setCreateTime(new Date());
                                routerCheck.setModifyBy(user.getUsername());
                                routerCheck.setModifyTime(new Date());
                                routerCheck.setTenantId(tenantId);
                                routerCheck.setBranchCode(branchCode);
                                routerCheck.setType("检查内容");
                                routerCheck.setName(checkList.get(j).getName());
                                routerCheck.setDrawingNo(drawnos.split(",")[i]);
                                routerCheck.setCheckOrder(check_order);
                                routerCheck.setUnit(checkList.get(j).getPropertyUnit());
                                routerCheck.setMethod(checkList.get(j).getPropertyInputtype());
                                routerCheck.setIsEmpty(1);
                                routerCheck.setDefualtValue(checkList.get(i).getPropertyDefaultvalue());
                                routerCheck.setStatus("1");
                                routerCheck.setPropertySymbol(checkList.get(i).getPropertySymbol());
                                routerCheck.setPropertyLowerlimit(checkList.get(i).getPropertyLowerlimit());
                                routerCheck.setPropertyUplimit(checkList.get(i).getPropertyUplimit());
                                routerCheck.setPropertyTestmethod(checkList.get(i).getPropertyTestmethod());
                                routerCheck.setPropertyDatatype(checkList.get(i).getPropertyInputtype());
                                check_order++;
                                routerCheckService.save(routerCheck);
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                step += ex.getMessage();
            }
            step += "检查内容保存完成";
            try {
                java.lang.reflect.Field[] qualityFields = RouterCheckQualityDto.class.getDeclaredFields();
                //封装证件信息实体类
                String[] qualityFieldNames = new String[qualityFields.length];
                for (int i = 0; i < qualityFieldNames.length; i++) {
                    qualityFieldNames[i] = qualityFields[i].getName();
                }
                List<RouterCheckQualityDto> qualityList = ExcelUtils.importExcel(excelFile, RouterCheckQualityDto.class, qualityFieldNames, 1, 0, 1, tempName.toString());
                FileUtils.delete(excelFile);
                step += "资料类型列表获取";
                List<RouterCheckQualityDto> list3 = new ArrayList<>();
                String drawnos = "";
                for (int i = 0; i < qualityList.size(); i++) {
                    if (!StringUtils.isNullOrEmpty(qualityList.get(i).getRouterNo())) {
                        list3.add(qualityList.get(i));
                    }
                    if (!drawnos.contains(qualityList.get(i).getRouterNo() + ",")) {
                        drawnos += qualityList.get(i).getRouterNo() + ",";
                    }
                }
                qualityList = list3;

                step += "资料类型列表去空";
                // 遍历图号插入资料资料
                for (int i = 0; i < drawnos.split(",").length; i++) {
                    // 先删除历史数据
                    QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
                    queryWrapper.eq("type", "质量资料");
                    queryWrapper.eq("tenant_id", tenantId);
                    queryWrapper.eq("branch_code", branchCode);
                    queryWrapper.eq("drawing_no", drawnos.split(",")[i]);

                    routerCheckService.remove(queryWrapper);
                    int check_order = 1;
                    // 插入新数据


                    for (int j = 0; j < qualityList.size(); j++) {
                        QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<Sequence>();
                        queryWrapper2.eq("opt_name", qualityList.get(j).getOptName().trim());
                        queryWrapper2.eq("tenant_id", tenantId);
                        queryWrapper2.eq("branch_code", branchCode);
                        queryWrapper.inSql("router_id", "select id from base_router where is_active='1' and router_no ='" + drawnos.split(",")[i] + "' and branch_code='" + branchCode + "'");

                        List<Sequence> sequences = sequenceService.list(queryWrapper2);
                        if (sequences.size() > 1) {
                            step += sequences.get(0).getRouterId() + sequences.get(0).getId() + qualityList.get(j).getOptName();
                            RouterCheck routerCheck = new RouterCheck();
                            routerCheck.setCreateBy(user.getUsername());
                            routerCheck.setRouterId(sequences.get(0).getRouterId());
                            routerCheck.setSequenceId(sequences.get(0).getId());
                            routerCheck.setCreateTime(new Date());
                            routerCheck.setModifyBy(user.getUsername());
                            routerCheck.setModifyTime(new Date());
                            routerCheck.setTenantId(tenantId);
                            routerCheck.setBranchCode(branchCode);
                            routerCheck.setType("质量资料");
                            routerCheck.setName(qualityList.get(j).getName());
                            routerCheck.setDrawingNo(qualityList.get(j).getRouterNo());
                            routerCheck.setCheckOrder(check_order);
                            routerCheck.setIsEmpty(1);
                            routerCheck.setStatus("1");
                            routerCheck.setPropertyObjectname(qualityList.get(j).getName());
                            check_order++;
                            routerCheckService.save(routerCheck);


                        }
                    }
                }
                step += "资料类型列表保存";
            } catch (Exception ex) {
                step += ex.getMessage();
            }

            return CommonResult.success(step, "成功");
        } catch (Exception e) {
            return CommonResult.failed("失败:" + step + e.getMessage());
        }
    }


}
