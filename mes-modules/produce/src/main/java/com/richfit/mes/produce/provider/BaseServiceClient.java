package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.produce.provider.fallback.BaseServiceClientFallbackImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
                                                      @RequestParam("optId") String optId
    );

    @GetMapping(value = "/api/base/branch/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(@RequestParam("branchCode") String branchCode);

    @GetMapping(value = "/api/base/branch/branch/one")
    public CommonResult<Branch> selectBranchByCodeAndTenantId(@RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);

    @GetMapping("/api/base/product/product/listByNo")
    public CommonResult<List<Product>> selectProduct(@RequestParam("materialNo") String materialNo, @RequestParam("drawingNo") String drawingNo, @RequestParam("materialType") String materialType);

    @GetMapping(value = "/api/base/device/find_one")
    public CommonResult<Device> getDeviceById(@RequestParam("id") String id);

    @PostMapping("/api/base/device/findByIds")
    public List<Device> getDeviceByIdList(@RequestBody List<String> idList);

    @GetMapping(value = "/api/base/calendar/class")
    public CommonResult<List<CalendarClass>> selectCalendarClass(@RequestParam("name") String name);

    @GetMapping(value = "/api/base/calendar/day/list")
    public CommonResult<List<CalendarDay>> selectCalendarDay(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate);

    @GetMapping(value = "/api/base/router/getByRouterNo")
    public CommonResult<Router> getByRouterNo(@RequestParam("routerNo") String routerNo,
                                              @RequestParam("branchCode") String branchCode
    );

    @GetMapping(value = "/api/base/router/getByRouterNos")
    public CommonResult<List<Router>> getByRouterNos(@RequestParam("routerNos") String routerNos,
                                                     @RequestParam("branchCode") String branchCode
    );

    @GetMapping(value = "/api/base/opt/find")
    public CommonResult<List<Operatipon>> find(@RequestParam("id") String id, @RequestParam("optCode") String optCode, @RequestParam("optName") String optName, @RequestParam("routerId") String routerId, @RequestParam("branchCode") String branchCode,@RequestParam("tenantId") String tenantId);


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

    @GetMapping("/api/base/sequence/assign/get")
    public CommonResult<OperationAssign> assignGet(@RequestParam("sequenceId") String sequenceId);

    @GetMapping("/api/base/sequence/querySequenceById")
    public CommonResult<Sequence> querySequenceById(@RequestParam("id") String id);

    /**
     * 功能描述:查询工艺版本
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/8/18 16:41
     * @return: String
     **/
    @GetMapping("/api/base/sequence/queryCraft")
    public String queryCraft(@RequestParam("id") String id);

    /**
     * 功能描述: 查询PDM图纸
     *
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/8/30 10:28
     * @return: CommonResult<List < PdmDraw>>
     **/
    @GetMapping("/api/base/pdmDraw/query/queryDrawList")
    public CommonResult<List<PdmDraw>> queryDrawList(@RequestParam("itemId") String itemId);

    /**
     * 功能描述: 查询工序
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/8/30 10:47
     * @return: CommonResult<PdmMesDraw>
     **/
    @GetMapping("/api/base/mes/pdmOption/queryOptionDraw/queryOptionDraw")
    public CommonResult<PdmMesOption> queryOptionDraw(@RequestParam("id") String id);


    /**
     * 功能描述: ERP库存查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/12 11:37
     **/
    @GetMapping("/api/base/routerCheck/select_by_id")
    public CommonResult<RouterCheck> routerCheckSelectById(@ApiParam(value = "id", required = true) @RequestParam String id);

    /**
     * 功能描述: 根据组织结构查询租户Id
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/10/14 15:44
     * @return: String
     **/
    @GetMapping(value = "/api/base/branch/queryTenantIdByBranchCode")
    public String queryTenantIdByBranchCode(@RequestParam("branchCode") String branchCode);


    /**
     * 功能描述: 查询质检上传文件列表
     *
     * @param type
     * @param branchCode
     * @param tenantId
     * @Author: xinYu.hou
     * @Date: 2022/10/20 14:13
     * @return: List<OperationTypeSpec>
     **/
    @GetMapping("/api/base/opttypespec/queryOperationTypeSpecByType")
    public List<OperationTypeSpec> queryOperationTypeSpecByType(@RequestParam("type") String type, @RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);

    /**
     * 功能描述: 通过物料号码查询物流信息
     *
     * @param materialNo
     * @Author: zhiqiang.lu
     * @Date: 2022/10/21 11:09
     * @return: String
     **/
    @GetMapping(value = "/api/base/product/list_by_material_no")
    public List<Product> listByMaterialNo(@RequestParam String materialNo);
}
