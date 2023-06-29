package com.richfit.mes.base.provider;

import com.richfit.mes.base.provider.fallback.SystemServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.constant.SecurityConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {


    @GetMapping(value = "/api/sys/user/find_one")
    public CommonResult<TenantUserVo> getUserById(@RequestParam("id") String id);

    @GetMapping(value = "/api/sys/user/queryUserByBranchCode")
    public CommonResult<List<TenantUserVo>> queryUserByBranchCode(@RequestParam("branchCode") String branchCode);

    @GetMapping(value = "/api/sys/user/queryUserByBranchCodeList")
    public CommonResult<List<TenantUserVo>> queryUserByBranchCodeList(@RequestParam("branchCode") String branchCode);

    @GetMapping(value = "/api/sys/item/item/param/list")
    public CommonResult<List<ItemParam>> selectItemClass(@RequestParam("code") String code, @RequestParam("name") String name, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/sys/attachment/get/{id}")
    public CommonResult<Attachment> attachment(@PathVariable String id);

    @GetMapping(value = "/api/sys/attachment/getinput/{id}")
    public CommonResult<byte[]> getAttachmentInputStream(@PathVariable String id);

    @GetMapping(value = "/api/sys/tenant/{id}")
    public CommonResult<Tenant> getTenant(@PathVariable String id);

    @GetMapping(value = "/api/sys/tenant/getTenantById")
    public CommonResult<Tenant> tenantById(@RequestParam String id);

    /**
     * 功能描述: 根据组织机构获取 质检人员
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/7/8 16:04
     * @return: List<TenantUserVo>
     **/
    @GetMapping("/api/sys/user/queryByBranchCode")
    public List<TenantUserVo> queryByBranchCode(@RequestParam("branchCode") String branchCode);

    @GetMapping("/api/sys/user/queryByTendId")
    public CommonResult<List<TenantUserVo>> queryByTendId();


    @GetMapping("/api/sys/user/queryUserByTenantId")
    public List<TenantUserVo> queryUserByTenantId(@RequestParam("tenantId") String tenantId);

    @GetMapping("/api/sys/data_dictionary/data_dictionary_param")
    public CommonResult<List<DataDictionaryParam>> getDataDictionaryParamByBranchCode(@RequestParam("branchCode") String branchCode);

    @ApiOperation(value = "查询所有的租户列表信息(包括内置)", notes = "查询所有启用的租户列表信息(包括内置)")
    @GetMapping("/api/sys/tenant/query/tenant/list/inner_all")
    public CommonResult<List<Tenant>> queryTenantAllList();

    /**
     * 功能描述:查询所有的租户列表信息
     *
     * @Date: 2022/12/26 16:18
     * @return: CommonResult
     **/
    @ApiOperation(value = "查询所有的租户列表信息", notes = "查询所有启用的租户列表信息")
    @GetMapping("/api/sys/tenant/query/tenant/list/inner")
    public CommonResult<List<Tenant>> queryTenantList(@RequestHeader(value = SecurityConstants.FROM) String header);

}
