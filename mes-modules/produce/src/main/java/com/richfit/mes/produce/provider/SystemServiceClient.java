package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.provider.fallback.SystemServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {


    @GetMapping(value = "/api/sys/user/find_one")
    public CommonResult<TenantUserVo> getUserById(@RequestParam("id") String id);

    @PostMapping("/api/sys/attachment/selectAttachmentsList")
    public List<Attachment> selectAttachmentsList(@RequestBody List<String> idList);

    @GetMapping(value = "/api/sys/user/queryByUserAccount")
    public CommonResult<TenantUserVo> queryByUserAccount(@RequestParam("userAccount") String userAccount);

    @DeleteMapping("/api/sys/attachment/{id}")
    public CommonResult<Boolean> delete(@PathVariable String id);

    @GetMapping(value = "/api/sys/item/item/param/list")
    public CommonResult<List<ItemParam>> selectItemClass(@RequestParam("code") String code, @RequestParam("name") String name, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/sys/item/item/param/list")
    public CommonResult<List<ItemParam>> selectItemClass(@RequestParam("code") String code, @RequestParam("name") String name);

    @GetMapping(value = "/api/sys/item/param/find_by_code")
    public CommonResult<ItemParam> findItemParamByCode(@RequestParam("code") String code);

    @GetMapping(value = "/api/sys/attachment/get/{id}")
    public CommonResult<Attachment> attachment(@PathVariable String id);

    @GetMapping(value = "/api/sys/attachment/getinput/{id}")
    public CommonResult<byte[]> getAttachmentInputStream(@PathVariable String id);


    @PostMapping(value = "/api/sys/note/save")
    public CommonResult<Boolean> savenote(@RequestParam("sendUser") String sendUser,
                                          @RequestParam("sendTitle") String sendTitle,
                                          @RequestParam("sendContent") String sendContent,
                                          @RequestParam("reseiverUsers") String reseiverUsers,
                                          @RequestParam("branchCode") String branchCode,
                                          @RequestParam("tenantId") String tenantId);

    @GetMapping(value = "/api/sys/qualityInspectionRules/queryQualityInspectionRulesById")
    public CommonResult<QualityInspectionRules> queryQualityInspectionRulesById(@RequestParam("id") String id);
}
