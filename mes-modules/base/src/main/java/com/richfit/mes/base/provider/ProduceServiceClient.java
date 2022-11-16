package com.richfit.mes.base.provider;

import com.richfit.mes.base.provider.fallback.ProduceServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackHead;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2022.9.22
 * @LastEditors: zhiqiang.lu
 * @LastEditTime: 2022.9.22
 * @Description: 添加produce接口
 * @LastEdit: 添加通过工艺id查询跟单列表
 */
@FeignClient(name = "produce-service", decode404 = true, fallback = ProduceServiceClientFallbackImpl.class)
public interface ProduceServiceClient {
    @GetMapping(value = "/api/produce/track_head/select_by_routerid")
    CommonResult<List<TrackHead>> selectByRouterId(@ApiParam(value = "工艺id") @RequestParam(required = false) String routerId);

    /**
     * 功能描述:根据项目BOM工作号,车间查询是否被跟单使用,有返回数量已被使用
     *
     * @param workNo
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/11/15 17:40
     * @return: int
     **/
    @GetMapping(value = "/api/produce/track_head/queryCountByWorkNo")
    int queryCountByWorkNo(@RequestParam("workNo") String workNo, @RequestParam("branchCode") String branchCode);
}
