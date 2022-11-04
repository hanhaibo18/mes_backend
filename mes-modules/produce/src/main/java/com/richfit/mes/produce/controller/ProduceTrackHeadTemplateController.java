package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gwb
 * @since 2022-01-10
 */
@Slf4j
@Api(value = "跟单模板", tags = {"跟单模板"})
@RestController
@RequestMapping("/api/produce/produce-track-head-template")
public class ProduceTrackHeadTemplateController {

    @Autowired
    private ProduceTrackHeadTemplateService produceTrackHeadTemplateService;


    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询接口", notes = "分页查询接口")
    @GetMapping("/page")
    public CommonResult<IPage<ProduceTrackHeadTemplate>> page(String templateCode, String templateName, String type, int page, int limit, String branchCode) {
        try {
            QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
            if (!StringUtils.isNullOrEmpty(templateCode)) {
                queryWrapper.like("template_code", templateCode);
            }
            if (!StringUtils.isNullOrEmpty(templateName)) {
                queryWrapper.like("template_name", templateName);
            }

            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.like("type", type);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", branchCode);
            }
            if(SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            }
            queryWrapper.orderByAsc("LENGTH(template_name)");
            IPage<ProduceTrackHeadTemplate> trackHeadTemplate = produceTrackHeadTemplateService.page(new Page<ProduceTrackHeadTemplate>(page, limit), queryWrapper);
            return CommonResult.success(trackHeadTemplate);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 新增操作信息
     */
    @ApiOperation(value = "新增信息", notes = "新增信息")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<ProduceTrackHeadTemplate> saveProduceTrackHeadTemplate(@RequestBody ProduceTrackHeadTemplate produceTrackHeadTemplate) {
        if(SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            produceTrackHeadTemplate.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        }
        boolean bool = produceTrackHeadTemplateService.save(produceTrackHeadTemplate);
        if (bool) {
            return CommonResult.success(produceTrackHeadTemplate, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    /**
     * 新增操作信息
     */
    @ApiOperation(value = "新增信息", notes = "新增信息")
    @PostMapping("/batchSave")
    public CommonResult<Boolean> batchSaveProduceTrackHeadTemplate(@RequestBody List<ProduceTrackHeadTemplate> templates) {
        return CommonResult.success(produceTrackHeadTemplateService.saveBatch(templates), "操作成功！");
    }


    @ApiOperation(value = "修改信息", notes = "修改信息")
    @PutMapping("/update")
    public CommonResult<ProduceTrackHeadTemplate> updateProduceTrackHeadTemplate(@RequestBody ProduceTrackHeadTemplate produceTrackHeadTemplate) {
        boolean bool = produceTrackHeadTemplateService.updateById(produceTrackHeadTemplate);
        if (bool) {
            return CommonResult.success(produceTrackHeadTemplate, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }


    @ApiOperation(value = "根据id删除信息", notes = "根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(produceTrackHeadTemplateService.removeById(id));
    }

}
