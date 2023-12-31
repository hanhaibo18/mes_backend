package com.kld.mes.erp.provider;

import com.kld.mes.erp.provider.fallback.BaseServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "base-service", decode404 = true, fallback = BaseServiceClientFallbackImpl.class)
public interface BaseServiceClient {

    @GetMapping(value = "/api/base/sequence/getByRouterNo")
    public CommonResult<List<Sequence>> getByRouterNo(@RequestParam("routerNo") String routerNo,
                                                      @RequestParam("branchCode") String branchCode,
                                                      @RequestParam("tenantId") String tenantId,
                                                      @RequestParam("optId") String optId,
                                                      @RequestHeader(value = SecurityConstants.FROM) String header
    );

    @PostMapping(value = "/api/base/sequence/push")
    public CommonResult<Map> push(@RequestBody Router router);

    @GetMapping(value = "/api/base/branch/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(@RequestParam("branchCode") String branchCode);

    @GetMapping("/api/base/product/product/listByNo")
    public CommonResult<List<Product>> selectProduct(@RequestParam("materialNo") String materialNo, @RequestParam("drawingNo") String drawingNo, @RequestParam("materialType") String materialType, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/base/device/find_one")
    public CommonResult<Device> getDeviceById(@RequestParam("id") String id);

    @GetMapping(value = "/api/base/calendar/class")
    public CommonResult<List<CalendarClass>> selectCalendarClass(@RequestParam("name") String name);

    @GetMapping(value = "/api/base/calendar/day/list")
    public CommonResult<List<CalendarDay>> selectCalendarDay(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate);

    @GetMapping(value = "/api/base/router/getByRouterNo")
    public CommonResult<Router> getRouterByNo(@RequestParam("routerNo") String routerNo,
                                              @RequestParam("branchCode") String branchCode
    );


    @GetMapping(value = "/api/base/sequencesite/find")
    public CommonResult<List<SequenceSite>> getSequenceDevice(@RequestParam("sequenceId") String sequenceId, @RequestParam("siteId") String siteId, @RequestParam("siteCode") String siteCode, @RequestParam("branchCode") String branchCode, @RequestParam("isDefault") String isDefault);

    @GetMapping(value = "/api/base/deviceperson/find")
    public CommonResult<List<DevicePerson>> getDevicePerson(@RequestParam("deviceId") String deviceId, @RequestParam("userId") String userId, @RequestParam("branchCode") String branchCode, @RequestParam("isDefault") String isDefault);

    @GetMapping(value = "/api/base/workingHours/hoursList")
    public CommonResult<List<Product>> queryProductName();

    @GetMapping(value = "/api/base/project_bom/getProjectBomPartByIdList")
    public List<ProjectBom> getProjectBomPartByIdList(@RequestParam("id") String id);

    @GetMapping("/api/base/routerCheck/find")
    public CommonResult<List<RouterCheck>> find(@RequestParam("drawingNo") String drawingNo, @RequestParam("optId") String optId, @RequestParam("type") String type);

    @GetMapping("/api/base/routerCheck/queryRouterList")
    public List<RouterCheck> queryRouterList(@RequestParam("optId") String optId, @RequestParam("type") String type, @RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);


    @GetMapping("/api/base/opttypespec/list")
    public CommonResult<List<OperationTypeSpec>> list(@RequestParam("optType") String optType, @RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);

    @GetMapping("/api/base/project_bom/queryBom")
    public ProjectBom queryBom(@RequestParam("workPlanNo") String workPlanNo, @RequestParam("branchCode") String branchCode);
}
