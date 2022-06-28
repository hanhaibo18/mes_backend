package com.richfit.mes.sys.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.CheckAttachment;
import com.richfit.mes.sys.provider.fallback.BaseServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: XinYu.Hou
 * @Date: 2022年6月28日 16:46:56
 */
@FeignClient(name = "produce-service", fallback = BaseServiceClientFallbackImpl.class)
public interface ProduceServiceClient {

    @GetMapping(value = "/api/base/branch/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(@RequestParam("branchCode") String branchCode);


    /**
     * 功能描述: 新增关联信息
     *
     * @param checkAttachment
     * @Author: xinYu.hou
     * @Date: 2022/6/28 16:49
     * @return: Boolean
     **/
    @PostMapping(value = "/api/produce/check_file/saveCheckFile")
    public CommonResult<Boolean> saveCheckFile(@RequestBody CheckAttachment checkAttachment);

}
