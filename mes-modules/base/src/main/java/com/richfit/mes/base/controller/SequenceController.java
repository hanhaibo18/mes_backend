package com.richfit.mes.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.enmus.MessageEnum;
import com.richfit.mes.base.enmus.OptTypeEnum;
import com.richfit.mes.base.entity.SequenceExportVo;
import com.richfit.mes.base.service.*;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private OperationTypeSpecService operatiponTypeSpecService;

    @Autowired
    private RouterCheckService routerCheckService;

    @Autowired
    private OperationAssignService operationAssignService;


    /**
     * 功能描述: 工序查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/13 11:37
     **/
    @ApiOperation(value = "工艺", notes = "工艺")
    @GetMapping("/list")
    public CommonResult<List<Sequence>> list(@ApiParam(value = "工艺id", required = true) @RequestParam String routerId) {
        try {
            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
            queryWrapper.eq("router_id", routerId);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByAsc("opt_order");
            List<Sequence> sequences = sequenceService.list(queryWrapper);
            return CommonResult.success(sequences);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
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
    @ApiOperation(value = "工艺", notes = "工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "optCode", value = "工序编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "optName", value = "工序名称", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Sequence>> page(int page, int limit, String routerId, String routerNo, String optCode, String optName, String branchCode) {
        try {

            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
            if (!StringUtils.isNullOrEmpty(routerId)) {
                queryWrapper.eq("router_id", routerId);
            }
            if (!StringUtils.isNullOrEmpty(routerNo)) {
                if (!StringUtils.isNullOrEmpty(branchCode)) {
                    queryWrapper.inSql("router_id", "select id from base_router where router_no ='" + routerNo + "' and branch_code='" + branchCode + "'");
                } else {
                    queryWrapper.inSql("router_id", "select id from base_router where router_no ='" + routerNo + "'");
                }
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(optCode)) {
                queryWrapper.eq("opt_code", optCode);
            }
            if (!StringUtils.isNullOrEmpty(optName)) {
                queryWrapper.eq("opt_name", optName);
            }
            // queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByAsc("opt_order");

            IPage<Sequence> sequences = sequenceService.page(new Page<Sequence>(page, limit), queryWrapper);
            sequences.setRecords(setOptCodeAndName(sequences.getRecords()));

            return CommonResult.success(sequences);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工序", notes = "新增工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/add")
    public CommonResult<Sequence> addSequence(@RequestBody Sequence sequence) {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        if (StringUtils.isNullOrEmpty(sequence.getOptCode())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            sequence.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            sequence.setCreateBy(user.getUsername());
            sequence.setModifyBy(user.getUsername());
            sequence.setCreateTime(new Date());
            sequence.setModifyTime(new Date());
            sequence.setTenantId(sequence.getTenantId());
            // 根据工序的工序类型添加质量资料
            //查询类型关联的质量资料
            QueryWrapper<OperationTypeSpec> queryWrapperOperationTypeSpec = new QueryWrapper<OperationTypeSpec>();
            queryWrapperOperationTypeSpec.eq("opt_type", sequence.getOptType());
            queryWrapperOperationTypeSpec.eq("branch_code", sequence.getBranchCode());
            queryWrapperOperationTypeSpec.eq("tenant_id", user.getTenantId());
            List<OperationTypeSpec> operationTypeSpecs = operatiponTypeSpecService.list(queryWrapperOperationTypeSpec);
            for (OperationTypeSpec dts : operationTypeSpecs) {
                RouterCheck routerCheck = new RouterCheck();
                routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                routerCheck.setSequenceId(sequence.getId());
                routerCheck.setRouterId(sequence.getRouterId());
                routerCheck.setName(dts.getPropertyName());
                routerCheck.setType("质量资料");
                routerCheck.setStatus("1");
                routerCheck.setDefualtValue(dts.getPropertyValue());
                routerCheck.setPropertyObjectname(dts.getPropertyName());

                routerCheck.setBranchCode(sequence.getBranchCode());
                routerCheck.setTenantId(user.getTenantId());
                routerCheck.setCreateTime(new Date());
                routerCheck.setCreateBy(user.getUsername());
                routerCheck.setModifyTime(new Date());
                routerCheck.setModifyBy(user.getUsername());
                routerCheckService.save(routerCheck);
            }
            boolean bool = sequenceService.save(sequence);
            if (bool) {
                return CommonResult.success(sequence, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "批量新增工序", notes = "批量新增工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/addList")
    public CommonResult addSequenceList(@RequestBody JSONObject list) {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        String branchCode = list.get("branchCode").toString();
        String tenantId = list.get("tenantId").toString();

        List<Sequence> sequenceList = JSON.parseArray(JSONObject.toJSONString(list.get("sequenceList")), Sequence.class);
        for (Sequence sequence : sequenceList) {
            if (StringUtils.isNullOrEmpty(sequence.getOptCode())) {
                return CommonResult.failed("编码不能为空！");
            } else {
                sequence.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                sequence.setCreateBy(user.getUsername());
                sequence.setModifyBy(user.getUsername());
                sequence.setCreateTime(new Date());
                sequence.setModifyTime(new Date());
                sequence.setTenantId(tenantId);
                sequence.setBranchCode(branchCode);
                // 根据工序的工序类型添加质量资料
                //查询类型关联的质量资料
                QueryWrapper<OperationTypeSpec> queryWrapperOperationTypeSpec = new QueryWrapper<OperationTypeSpec>();
                queryWrapperOperationTypeSpec.eq("opt_type", sequence.getOptType());
                queryWrapperOperationTypeSpec.eq("branch_code", sequence.getBranchCode());
                queryWrapperOperationTypeSpec.eq("tenant_id", sequence.getTenantId());
                List<OperationTypeSpec> operationTypeSpecs = operatiponTypeSpecService.list(queryWrapperOperationTypeSpec);
                for (OperationTypeSpec dts : operationTypeSpecs) {
                    RouterCheck routerCheck = new RouterCheck();
                    routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    routerCheck.setSequenceId(sequence.getId());
                    routerCheck.setRouterId(sequence.getRouterId());
                    routerCheck.setName(dts.getPropertyName());
                    routerCheck.setType("质量资料");
                    routerCheck.setStatus("1");
                    routerCheck.setDefualtValue(dts.getPropertyValue());
                    routerCheck.setPropertyObjectname(dts.getPropertyName());

                    routerCheck.setBranchCode(sequence.getBranchCode());
                    routerCheck.setTenantId(user.getTenantId());
                    routerCheck.setCreateTime(new Date());
                    routerCheck.setCreateBy(user.getUsername());
                    routerCheck.setModifyTime(new Date());
                    routerCheck.setModifyBy(user.getUsername());
                    routerCheckService.save(routerCheck);
                }
                sequenceService.save(sequence);
            }
        }
        return null;
    }

    @ApiOperation(value = "修改工序", notes = "修改工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/update")
    public CommonResult<Sequence> updateSequence(@RequestBody Sequence sequence) {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        if (StringUtils.isNullOrEmpty(sequence.getOptCode())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            sequence.setModifyBy(user.getUsername());
            sequence.setModifyTime(new Date());
            Sequence sequenceOld = sequenceService.getById(sequence.getId());
            if (!sequence.getOptType().equals(sequenceOld.getOptType())) {
                //删除工序已关联的质量资料历史数据
                QueryWrapper<RouterCheck> queryWrapperRouterCheck = new QueryWrapper<>();
                queryWrapperRouterCheck.eq("sequence_id", sequence.getId());
                queryWrapperRouterCheck.eq("type", "质量资料");
                queryWrapperRouterCheck.eq("branch_code", sequence.getBranchCode());
                queryWrapperRouterCheck.eq("tenant_id", user.getTenantId());
                routerCheckService.remove(queryWrapperRouterCheck);

                //工序质量资料
                //查询类型关联的质量资料
                QueryWrapper<OperationTypeSpec> queryWrapperOperationTypeSpec = new QueryWrapper<OperationTypeSpec>();
                queryWrapperOperationTypeSpec.eq("opt_type", sequence.getOptType());
                queryWrapperOperationTypeSpec.eq("branch_code", sequence.getBranchCode());
                queryWrapperOperationTypeSpec.eq("tenant_id", user.getTenantId());
                List<OperationTypeSpec> operationTypeSpecs = operatiponTypeSpecService.list(queryWrapperOperationTypeSpec);
                for (OperationTypeSpec dts : operationTypeSpecs) {
                    RouterCheck routerCheck = new RouterCheck();
                    routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    routerCheck.setSequenceId(sequence.getId());
                    routerCheck.setRouterId(sequence.getRouterId());
                    routerCheck.setName(dts.getPropertyName());
                    routerCheck.setType("质量资料");
                    routerCheck.setStatus("1");
                    routerCheck.setDefualtValue(dts.getPropertyValue());
                    routerCheck.setPropertyObjectname(dts.getPropertyName());

                    routerCheck.setBranchCode(sequence.getBranchCode());
                    routerCheck.setTenantId(user.getTenantId());
                    routerCheck.setCreateTime(new Date());
                    routerCheck.setCreateBy(user.getUsername());
                    routerCheck.setModifyTime(new Date());
                    routerCheck.setModifyBy(user.getUsername());
                    routerCheckService.save(routerCheck);
                }
            }
            boolean bool = sequenceService.update(sequence, new QueryWrapper<Sequence>().eq("id", sequence.getId()).eq("branch_code", sequence.getBranchCode()));
            if (bool) {
                return CommonResult.success(sequence, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }


    @ApiOperation(value = "修改工序", notes = "修改工序")
    @ApiImplicitParam(name = "sequence", value = "工序", required = true, dataType = "Sequence", paramType = "path")
    @PostMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<List<Sequence>> batchupdateSequence(@RequestBody List<Sequence> sequences) {

        for (Sequence sequence : sequences) {

            sequenceService.update(sequence, new QueryWrapper<Sequence>().eq("id", sequence.getId()).eq("branch_code", sequence.getBranchCode()));
        }
        return CommonResult.success(sequences, "操作成功！");

    }

    @ApiOperation(value = "查询工序", notes = "根据编码获得工序")
    @ApiImplicitParam(name = "sequenceCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Sequence>> find(String id, String optCode, String optName, String routerId) {
        QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(optCode)) {
            queryWrapper.like("opt_code", optCode);
        }
        if (!StringUtils.isNullOrEmpty(optName)) {
            queryWrapper.like("opt_name", optName);
        }
        if (!StringUtils.isNullOrEmpty(optName)) {
            queryWrapper.like("opt_name", optName);
        }
        if (!StringUtils.isNullOrEmpty(routerId)) {
            queryWrapper.eq("router_id", routerId);
        }

        queryWrapper.orderByAsc("opt_order");
        List<Sequence> result = sequenceService.list(queryWrapper);
        result = setOptCodeAndName(result);

        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询工序", notes = "根据工艺ID获得工序")
    @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/getByRouterId")
    public CommonResult<List<Sequence>> getByRouterId(String routerId, String branchCode) {
        QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
        queryWrapper.eq("router_id", routerId);

        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.orderByAsc("opt_order");
        List<Sequence> result = sequenceService.list(queryWrapper);
        result = setOptCodeAndName(result);
        return CommonResult.success(result, "操作成功！");
    }


    @ApiOperation(value = "查询工序", notes = "根据工艺ID获得工序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/getByRouterNo")
    public CommonResult<List<Sequence>> getByRouterNo(String routerNo, String branchCode, String tenantId, String optId) {

        QueryWrapper<Router> query = new QueryWrapper<Router>();

        if (!StringUtils.isNullOrEmpty(routerNo)) {
            query.eq("router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            query.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            query.eq("tenant_id", tenantId);
        }
        query.in("status", "1");
        List<Router> routers = routerService.list(query);
        if (routers.size() > 0) {
            Router router = routers.get(0);
            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
            queryWrapper.eq("router_id", router.getId());
            if (!StringUtils.isNullOrEmpty(optId)) {
                queryWrapper.eq("opt_id", optId);
            }
            queryWrapper.orderByAsc("opt_order");
            List<Sequence> result = sequenceService.list(queryWrapper);
            result = setOptCodeAndName(result);
            return CommonResult.success(result, "操作成功！");
        }


        return CommonResult.success(null, "操作成功！");
    }


    public List<Sequence> setOptCodeAndName(List<Sequence> result) {

        for (int i = 0; i < result.size(); i++) {
            if (StringUtils.isNullOrEmpty(result.get(i).getOptCode())) {
                QueryWrapper<Operatipon> qw = new QueryWrapper<Operatipon>();
                qw.like("branch_code", result.get(i).getBranchCode());
                if (!StringUtils.isNullOrEmpty(result.get(i).getOptId())) {
                    qw.eq("id", result.get(i).getOptId());
                }


                List<Operatipon> opts = operatiponService.list(qw);
                if (opts.size() > 0) {
                    result.get(i).setOptCode(opts.get(0).getOptCode());
                    if (StringUtils.isNullOrEmpty(result.get(i).getOptName())) {
                        result.get(i).setOptName(opts.get(0).getOptName());
                    }
                }
            }


        }
        return result;
    }

    @ApiOperation(value = "删除工序", notes = "根据id删除工序")
    @ApiImplicitParam(name = "id", value = "ids", required = true, dataType = "String", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<Sequence> deleteById(@RequestBody String[] ids) {
        for (String id : ids) {
            Sequence sequence = sequenceService.getById(id);
            QueryWrapper<RouterCheck> queryWrapperRouterCheck = new QueryWrapper<>();
            queryWrapperRouterCheck.eq("sequence_id", sequence.getId());
            queryWrapperRouterCheck.eq("type", "质量资料");
            queryWrapperRouterCheck.eq("branch_code", sequence.getBranchCode());
            queryWrapperRouterCheck.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            routerCheckService.remove(queryWrapperRouterCheck);
        }

        boolean bool = sequenceService.removeByIds(java.util.Arrays.asList(ids));
        if (bool) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }

    @ApiOperation(value = "导入工序", notes = "根据Excel文档导入工序")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file, String tenantId, String branchCode) {

        CommonResult result = null;
        String msg = "";
        String[] fieldNames = {"status", "content", "remark", "versionCode", "optName", "opNo", "optType", "singlePieceHours", "prepareEndHours", "isQualityCheck", "isScheduleCheck"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);

            List<SequenceExportVo> headCheck = ExcelUtils.importExcel(excelFile, SequenceExportVo.class, fieldNames, 0, 1, 0, 0, tempName.toString());
            if(headCheck.size()>0
                    && "是否导入".equals(headCheck.get(0).getStatus())
                    && "工艺号".equals(headCheck.get(0).getContent())
                    && "工艺描述".equals(headCheck.get(0).getRemark())
                    && "版本号".equals(headCheck.get(0).getVersionCode())
                    && "工序名".equals(headCheck.get(0).getOptName())
                    && "工序号".equals(headCheck.get(0).getOpNo())
                    && "工序类型".equals(headCheck.get(0).getOptType())
                    && "单件".equals(headCheck.get(0).getSinglePieceHours())
                    && "准结".equals(headCheck.get(0).getPrepareEndHours())
                    && "质检确认".equals(headCheck.get(0).getIsQualityCheck())
                    && "调度确认".equals(headCheck.get(0).getIsScheduleCheck())){

            }else{
                return CommonResult.failed("导入模板错误!，请重新校验模板");
            }
            //将导入的excel数据生成证件实体类list
            List<Sequence> list = ExcelUtils.importExcel(excelFile, Sequence.class, fieldNames, 1, 0, 0, tempName.toString());
            List<Sequence> list2 = new ArrayList<>();
            // 获取图号列表
            String drawnos = "";
            String drawnames = "";
            for (int i = 0; i < list.size(); i++) {
                if (!StringUtils.isNullOrEmpty(list.get(i).getContent()) && "X".equals(list.get(i).getStatus())) {
                    list2.add(list.get(i));
                    if (!StringUtils.isNullOrEmpty(list.get(i).getContent()) && !drawnos.contains(list.get(i).getContent() + "@" + list.get(i).getVersionCode() + ",")) {
                        drawnos += list.get(i).getContent() + "@" + list.get(i).getVersionCode() + ",";
                        drawnames += list.get(i).getRemark() + ",";
                    }
                }

            }

            FileUtils.delete(excelFile);
            list = list2;
            msg = "导入完成";

            // 遍历图号
            for (int j = 0; j < drawnos.split(",").length; j++) {
                String drawno = drawnos.split(",")[j].split("@")[0];
                String drawversion = drawnos.split(",")[j].split("@")[1];
                String drawname = drawnames.split(",")[j];
                List<Sequence> newlist = new ArrayList<>();

                List<Router> routers = routerService.list(new QueryWrapper<Router>().eq("router_no", drawno).eq("status", "1").eq("tenant_id", tenantId).eq("branch_code", branchCode));
                int existRouterIndex = -1;
                // 如果已存在的工艺和当前版本不一致，则改为历史工艺
                for (int jj = 0; jj < routers.size(); jj++) {
                    // 如果工艺版本相同，则删除该工艺下的工序
                    if (routers.get(jj).getVersion().equals(drawversion)) {
                        existRouterIndex = jj;
                        sequenceService.remove(new QueryWrapper<Sequence>().eq("router_id", routers.get(jj).getId()));
                    } else {
                        routers.get(jj).setStatus("2");
                        routers.get(jj).setIsActive("0");
                        routerService.updateById(routers.get(jj));
                    }
                }
                // 如果没有工艺，则新增工艺
                if (existRouterIndex == -1) {
                    Router r = new Router();
                    r.setTenantId(tenantId);
                    r.setBranchCode(branchCode);
                    r.setStatus("1");
                    r.setIsActive("1");
                    r.setType("0");
                    r.setVersion(drawversion);
                    r.setRouterNo(drawno);
                    r.setRouterName(drawname);
                    r.setCreateTime(new Date());
                    r.setModifyTime(new Date());
                    r.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    r.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    routerService.save(r);
                    routers = routerService.list(new QueryWrapper<Router>().eq("router_no", drawno).eq("status", "1").eq("tenant_id", tenantId).eq("branch_code", branchCode));
                    existRouterIndex = 0;
                }

                int optOrder = 0;
                // 遍历导入数据
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCreateTime(new Date());
                    list.get(i).setModifyTime(new Date());
                    list.get(i).setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    list.get(i).setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    list.get(i).setTenantId(tenantId);
                    list.get(i).setBranchCode(branchCode);
                    if ("是".equals(list.get(i).getIsQualityCheck())) {
                        list.get(i).setIsQualityCheck("1");
                    }
                    if ("否".equals(list.get(i).getIsQualityCheck())) {
                        list.get(i).setIsQualityCheck("0");
                    }
                    if ("是".equals(list.get(i).getIsScheduleCheck())) {
                        list.get(i).setIsScheduleCheck("1");
                    }
                    if ("否".equals(list.get(i).getIsScheduleCheck())) {
                        list.get(i).setIsScheduleCheck("0");
                    }
                    if ("是".equals(list.get(i).getIsParallel())) {
                        list.get(i).setIsParallel("1");
                    }
                    if ("否".equals(list.get(i).getIsParallel())) {
                        list.get(i).setIsParallel("0");
                    }
                    list.get(i).setOptOrder(optOrder);
                    list.get(i).setTechnologySequence(String.valueOf(optOrder));
                    list.get(i).setOptNextOrder(optOrder + 1);
                    list.get(i).setIsParallel("0");
                    try {
                        // 获取工艺类型
                        int optType = OptTypeEnum.getCode(list.get(i).getOptType());
                        list.get(i).setOptType(String.valueOf(optType));
                        if (!StringUtils.isNullOrEmpty(list.get(i).getContent()) && list.get(i).getContent().equals(drawno) && list.get(i).getVersionCode().equals(drawversion)) {

                            list.get(i).setStatus("1");
                            if (routers.size() > 0) {
                                list.get(i).setRouterId(routers.get(existRouterIndex).getId());
                            } else {
                                msg += "第" + (i + 1) + "行:" + "找不到图号,";
                                list.get(i).setStatus("0");
                            }
                            msg = "工艺获取完成";


                            // 获取工序
                            List<Operatipon> opts = operatiponService.list(new QueryWrapper<Operatipon>().eq("opt_name", list.get(i).getOptName()).eq("opt_type", optType).eq("branch_code", branchCode));
                            if (opts.size() > 0) {
                                list.get(i).setOptId(opts.get(0).getId());
                                list.get(i).setOptCode(opts.get(0).getOptCode());

                            } else {
                                // 如果没有工序则新增工序
                                Operatipon o = new Operatipon();
                                o.setOptCode(list.get(i).getOptName());
                                o.setOptType(optType);
                                o.setTenantId(tenantId);
                                o.setBranchCode(branchCode);
                                o.setOptName(list.get(i).getOptName());
                                operatiponService.save(o);
                                opts = operatiponService.list(new QueryWrapper<Operatipon>().eq("opt_name", list.get(i).getOptName()).eq("opt_type", optType));
                                list.get(i).setOptId(opts.get(0).getId());
                                list.get(i).setOptCode(opts.get(0).getOptCode());

                            }
                            optOrder++;
                            newlist.add(list.get(i));
                            msg = "工序获取完成";


                        }
                    } catch (Exception ex) {

                    }

                }
                // 将最后一道工序的下工序设置为0
                if (newlist.size() > 0) {
                    newlist.get(newlist.size() - 1).setOptNextOrder(0);
                    boolean bool = sequenceService.saveBatch(newlist);
                }
            }

            return CommonResult.success(msg);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage() + msg);
        }
    }

    @ApiOperation(value = "导出工艺信息", notes = "通过Excel文档导出工艺信息")
    @GetMapping("/export_excel")
    public void exportExcel(String routerId, String routerNo, String optCode, String optName, String branchCode, HttpServletResponse rsp) {

        QueryWrapper<Router> queryWrapper2 = new QueryWrapper<Router>();
        if (!StringUtils.isNullOrEmpty(routerId)) {
            queryWrapper2.eq("id", routerId);
        }
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            queryWrapper2.eq("router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper2.eq("branch_code", branchCode);
        }
        List<Router> routers = routerService.list(queryWrapper2);


        QueryWrapper<Sequence> queryWrapper = new QueryWrapper<Sequence>();
        queryWrapper.eq("router_id", routers.get(0).getId());
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(optCode)) {
            queryWrapper.eq("opt_code", optCode);
        }
        if (!StringUtils.isNullOrEmpty(optName)) {
            queryWrapper.eq("opt_name", optName);
        }
        queryWrapper.orderByAsc("opt_order");

        List<Sequence> sequences = sequenceService.list(queryWrapper);

        //处理返回数据
        for (Sequence sequence : sequences) {
            try {
                sequence.setStatus("X");
                sequence.setContent(routers.get(0).getRouterNo());
                sequence.setRemark(routers.get(0).getRouterName());
                sequence.setVersionCode(routers.get(0).getVersion());
                sequence.setIsScheduleCheck(MessageEnum.getMessage(Integer.parseInt(sequence.getIsScheduleCheck())));
                sequence.setIsQualityCheck(MessageEnum.getMessage(Integer.parseInt(sequence.getIsQualityCheck())));
                sequence.setIsParallel(MessageEnum.getMessage(Integer.parseInt(sequence.getIsParallel())));
                sequence.setOptType(OptTypeEnum.getMessage(Integer.parseInt(sequence.getOptType())));
            } catch (Exception ex) {
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

        String fileName = "工艺数据" + format.format(new Date()) + ".xlsx";

        String[] columnHeaders = {"是否导入", "工艺号", "工艺描述", "版本号", "工序名", "工序号", "工序类型", "单件", "准结", "质检确认", "调度确认"};
        String[] fieldNames = {"status", "content", "remark", "versionCode", "optName", "opNo", "optType", "singlePieceHours", "prepareEndHours", "isQualityCheck", "isScheduleCheck"};
        //export
        try {
            ExcelUtils.exportExcel(fileName, sequences, columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "新增工序派工", notes = "新增工序派工")
    @ApiImplicitParam(name = "assign", value = "工序派工", required = true, dataType = "SequenceAssign", paramType = "path")
    @PostMapping("/assign/save")
    public CommonResult<Boolean> assignSave(@RequestBody OperationAssign assign) {
        return CommonResult.success(operationAssignService.save(assign), "操作成功！");
    }

    @ApiOperation(value = "修改工序派工", notes = "修改工序派工")
    @ApiImplicitParam(name = "assign", value = "工序派工", required = true, dataType = "SequenceAssign", paramType = "path")
    @PutMapping("/assign/update")
    public CommonResult<Boolean> assignUpdate(@RequestBody OperationAssign assign) {
        return CommonResult.success(operationAssignService.updateById(assign), "操作成功！");
    }

    @ApiOperation(value = "删除工序派工", notes = "删除工序派工")
    @DeleteMapping("/assign/delete")
    public CommonResult<Boolean> assignDelete(String sequenceId) {
        QueryWrapper<OperationAssign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operation_id", sequenceId);
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        return CommonResult.success(operationAssignService.remove(queryWrapper), "操作成功！");
    }

    @ApiOperation(value = "查询工序派工", notes = "根据工艺ID查询工序派工")
    @GetMapping("/assign/get")
    public CommonResult<OperationAssign> assignGet(String sequenceId) {
        QueryWrapper<OperationAssign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operation_id", sequenceId);
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        return CommonResult.success(operationAssignService.getOne(queryWrapper), "操作成功！");
    }

    @GetMapping("/querySequenceById")
    public CommonResult<Sequence> querySequenceById(String id) {
        Sequence sequence = sequenceService.getById(id);
        return CommonResult.success(sequence);
    }

    @GetMapping("/queryCraft")
    public String queryCraft(String id) {
        return sequenceService.queryCraft(id);
    }
}
