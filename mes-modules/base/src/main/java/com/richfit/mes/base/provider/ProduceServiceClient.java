package com.richfit.mes.base.provider;

import com.richfit.mes.base.provider.fallback.ProduceServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    int queryCountByWorkNo(@RequestParam("projectBomId") String projectBomId);

    @PostMapping("/api/produce/order/query_by_materialcode")
    CommonResult<List<Order>> queryByMaterialCode(@RequestBody List<String> materialCodes, @RequestParam("tenantId") String tenantId);

    @GetMapping("/api/produce/track_head/getTrackHeadByMaterialCodeAndDrawingNo")
    public CommonResult<List<TrackHead>> getTrackHeadByMaterialCodeAndDrawingNo(@RequestParam("materialCodes") List<String> materialCodes, @RequestParam("drawingNos") List<String> drawingNos, @RequestParam("tenantId") String tenantId);

    @GetMapping("/api/produce/track_item/query/id")
    public TrackItem qyeryTrackItemById(@RequestParam("id") String id);

    @PostMapping("/api/produce/track_head/updateBatch")
    public boolean updateBatch(@RequestBody List<TrackHead> trackHeadList);

    @GetMapping("/api/produce/trackassembly/getAssemblyListByProjectBomId")
    public List<TrackAssembly> getAssemblyListByProjectBomId(@RequestParam String projectBomId, @RequestParam String tenantId, @RequestParam String branchCode);

    @ApiOperation(value = "修改装配信息(其他服务调用)")
    @PostMapping("/api/produce/trackassembly/updateAssembly")
    public boolean updateAssembly(@RequestBody List<TrackAssembly> trackAssemblyList);

    @ApiOperation(value = "根据flowId获取follow信息(其他服务调用)")
    @GetMapping("/api/produce/track_head/getFlowInfoById")
    public TrackFlow getFlowInfoById(@RequestParam String id);

    @ApiOperation(value = "根据项目bomId查询跟单列表(其他服务调用)")
    @GetMapping("/api/produce/track_head/getTrackHeadByProjectBomId")
    public List<TrackHead> getTrackHeadByProjectBomId(@RequestParam String bomId, @RequestParam String tenantId, @RequestParam String branchCode);

    @ApiOperation(value = "根据跟单Id查询装配列表(其他服务调用)")
    @GetMapping("/api/produce/trackassembly/getAssemblyListByTrackHeadId")
    public List<TrackAssembly> getAssemblyListByTrackHeadId(@RequestParam String trackHeadId, @RequestParam String tenantId, @RequestParam String branchCode);

    @ApiOperation(value = "批量新增装配列表(其他服务调用)")
    @PostMapping("/api/produce/trackassembly/addAssemblyList")
    public boolean addAssemblyList(@RequestBody List<TrackAssembly> trackAssemblyList);

    @ApiOperation(value = "根据projectBomId删除装配信息(其他服务调用)")
    @GetMapping("/api/produce/trackassembly/deleteByBomId")
    public boolean deleteAssemblyByBomId(@RequestParam String bomId, @RequestParam String tenantId, @RequestParam String branchCode);
}
