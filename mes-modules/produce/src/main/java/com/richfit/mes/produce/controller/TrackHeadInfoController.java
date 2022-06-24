package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackHeadInfo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.TrackHeadInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 鲁志强
 * @Description 跟单预设信息Controller
 */
@Slf4j
@Api(tags = "跟单预设信息")
@RestController
@RequestMapping("/api/produce/track_head_info")
public class TrackHeadInfoController extends BaseController {

    @Autowired
    private TrackHeadInfoService trackHeadInfoService;

    @ApiOperation(value = "新增", notes = "新增跟单预设信息")
    @PostMapping("/add")
    public void add(@ApiParam(value = "跟单预设信息", required = true) @RequestBody TrackHeadInfo trackHeadInfo) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            QueryWrapper<TrackHeadInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.eq("branch_code", trackHeadInfo.getBranchCode());
            List<TrackHeadInfo> l = trackHeadInfoService.list(queryWrapper);
            if (l.size() > 0) {
                throw new Exception("当前已保存跟单预设信息，不能再次添加");
            }
            trackHeadInfo.setId(UUID.randomUUID().toString().replace("-", ""));
            trackHeadInfo.setTenantId(user.getTenantId());
            trackHeadInfo.setCreateBy(user.getUsername());
            trackHeadInfo.setCreateTime(new Date());
            trackHeadInfo.setModifyBy(user.getUsername());
            trackHeadInfo.setModifyTime(new Date());
            trackHeadInfoService.save(trackHeadInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("保存出现异常");
        }
    }

    @ApiOperation(value = "更新", notes = "更新跟单预设信息")
    @PostMapping("/update")
    public void update(@ApiParam(value = "跟单预设信息", required = true) @RequestBody TrackHeadInfo trackHeadInfo) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            trackHeadInfo.setTenantId(user.getTenantId());
            trackHeadInfo.setCreateBy(user.getUsername());
            trackHeadInfo.setCreateTime(new Date());
            trackHeadInfo.setModifyBy(user.getUsername());
            trackHeadInfo.setModifyTime(new Date());
            trackHeadInfoService.updateById(trackHeadInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("保存出现异常");
        }
    }

    @ApiOperation(value = "查询", notes = "查询跟单预设信息")
    @GetMapping("/{id}")
    public CommonResult<TrackHeadInfo> byId(@ApiParam(value = "跟单预设信息", required = true) @PathVariable String id) throws Exception {
        try {
            return CommonResult.success(trackHeadInfoService.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("保存出现异常");
        }
    }

    @ApiOperation(value = "查询", notes = "查询跟单预设信息")
    @GetMapping("/branchCode/{branchCode}")
    public CommonResult<TrackHeadInfo> byBranchCode(@ApiParam(value = "工厂代码", required = true) @PathVariable String branchCode) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            QueryWrapper<TrackHeadInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.eq("branch_code", branchCode);
            List<TrackHeadInfo> l = trackHeadInfoService.list(queryWrapper);
            return CommonResult.success(l.size() > 0 ? l.get(0) : null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("保存出现异常");
        }
    }

    @ApiOperation(value = "保存&跟新", notes = "保存&跟新跟单预设信息")
    @PostMapping("/add_update")
    public void add_update(@ApiParam(value = "跟单预设信息", required = true) @RequestBody TrackHeadInfo trackHeadInfo) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            QueryWrapper<TrackHeadInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.eq("branch_code", trackHeadInfo.getBranchCode());
            List<TrackHeadInfo> l = trackHeadInfoService.list(queryWrapper);
            trackHeadInfo.setTenantId(user.getTenantId());
            trackHeadInfo.setModifyBy(user.getUsername());
            trackHeadInfo.setModifyTime(new Date());
            if (l.size() > 0) {
                trackHeadInfoService.updateById(trackHeadInfo);
            } else {
                trackHeadInfo.setId(UUID.randomUUID().toString().replace("-", ""));
                trackHeadInfo.setCreateBy(user.getUsername());
                trackHeadInfo.setCreateTime(new Date());
                trackHeadInfoService.save(trackHeadInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("保存出现异常");
        }
    }
}
