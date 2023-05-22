package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.provider.fallback.BaseServiceClientFallbackImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @GetMapping(value = "/api/base/router/getRouter")
    public CommonResult<Router> getRouter(@RequestParam("routerId") String routerId);

    @GetMapping(value = "/api/base/router/getByRouter")
    public CommonResult<Router> getByRouterId(@RequestParam("routerId") String routerId,
                                              @RequestParam("branchCode") String branchCode
    );

    @GetMapping(value = "/api/base/router/getByRouterNos")
    public CommonResult<List<Router>> getByRouterNos(@RequestParam("routerNos") String routerNos,
                                                     @RequestParam("branchCode") String branchCode
    );

    /**
     * 根据idList获得工艺
     *
     * @param idList
     * @return
     */
    @PostMapping("/api/base/router/getByIds")
    public CommonResult<List<Router>> getByRouterId(@RequestBody List<String> idList);

    @GetMapping(value = "/api/base/opt/find")
    public CommonResult<List<Operatipon>> find(@RequestParam("id") String id, @RequestParam("optCode") String optCode, @RequestParam("optName") String optName, @RequestParam("routerId") String routerId, @RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);


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
    public CommonResult<OperationAssign> assignGet(@RequestParam("optName") String optName, @RequestParam("branchCode") String branchCode);

    @GetMapping("/api/base/sequence/querySequenceById")
    public CommonResult<Sequence> querySequenceById(@RequestParam("optName") String optName, @RequestParam("branchCode") String branchCode);

    /**
     * 功能描述:查询工艺版本
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/18 16:41
     * @return: String
     **/
    @GetMapping("/api/base/sequence/queryCraft")
    public String queryCraft(@RequestParam("optName") String optName, @RequestParam("branchCode") String branchCode);

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
    public Branch queryTenantIdByBranchCode(@RequestParam("branchCode") String branchCode);


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

    /**
     * 功能描述: 通过物料号码查询物流信息
     *
     * @param materialNoList
     * @Author: hujia
     * @Date: 2023/03/28 11:09
     * @return: String
     **/
    @PostMapping(value = "/api/base/product/list_by_material_no_list")
    public List<Product> listByMaterialNoList(@RequestBody List<String> materialNoList);

    /**
     * 功能描述: 根据物料号 图号查询校验
     *
     * @param materialNo
     * @param drawingNo
     * @Author: xinYu.hou
     * @Date: 2022/11/30 14:01
     * @return: List<Product>
     **/
    @GetMapping("/api/base/product/selectOrderProduct")
    List<Product> selectOrderProduct(@RequestParam("materialNo") String materialNo, @RequestParam("drawingNo") String drawingNo);

    @GetMapping("/api/base/product/selectOrderProduct/inner")
    List<Product> selectOrderProductInner(@RequestParam("materialNo") String materialNo, @RequestParam("drawingNo") String drawingNo, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping("/api/base/opt/queryOptByOptNames")
    List<Operatipon> queryOptByOptNames(@ApiParam(value = "工序字典名称") @RequestBody List<String> optNams,
                                        @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode);

    @PostMapping("/api/base/router/get_by_drawNo")
    public CommonResult<List<Router>> getByDrawNo(@RequestBody List<String> drawNos, @RequestParam String branchCode);


    @GetMapping("/api/base/sequence/query_by_routerIds")
    public List<Sequence> querySequenceByRouterIds(@ApiParam(value = "工艺id", required = true) @RequestBody List<String> routerIds);

    /**
     * 功能描述:根据id查询工序字典列表
     *
     * @Author: hujia
     **/
    @ApiOperation(value = "根据id查询工序字典列表", notes = "根据id查询工序字典列表")
    @GetMapping("/api/base/opt/queryOptByIds")
    List<Operatipon> queryOptByIds(@ApiParam(value = "工序字典idList") @RequestBody List<String> optIds);

    @PostMapping("/api/base/project_bom/bindingBom")
    public Map<String, Object> bindingBom(@RequestBody List<TrackHead> trackHeads);

    @PostMapping("/api/base/project_bom/addBom")
    public void addBom(@RequestBody List<ProjectBom> bomList);

    @ApiOperation(value = "根据主项目bom获取项目bom列表")
    @PostMapping("/api/base/project_bom/getBomListByMainBomId")
    public List<ProjectBom> getBomListByMainBomId(@RequestParam String id);

    @ApiOperation(value = "根据branchCode获取机构信息")
    @GetMapping("/api/base/branch/getBranchInfoByBranchCode")
    public Branch getBranchInfoByBranchCode(@RequestParam String branchCode);

    @ApiOperation(value = "查询工艺")
    @GetMapping("/api/base/router/find")
    public CommonResult<List<Router>> find(@RequestParam String id, @RequestParam String routerNo, @RequestParam String routerName, @RequestParam String version, @RequestParam String branchCode, @RequestParam String tenantId, @RequestParam String status, @RequestParam String testBar, @RequestParam String texture);

    @ApiOperation(value = "获取所有车间")
    @GetMapping("/api/base/branch/query_all_branch")
    public List<Branch> queryAllBranch();

    @ApiOperation(value = "获取所有车间Inner")
    @GetMapping("/api/base/branch/query_all_branch_inner")
    public List<Branch> queryAllBranchInner(@RequestHeader(value = SecurityConstants.FROM) String header);

}
