package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackHeadTemplate;
import com.richfit.mes.common.model.produce.TrackHeadTemplateForm;
import com.richfit.mes.produce.service.TrackHeadTemplateFormService;
import com.richfit.mes.produce.service.TrackHeadTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单Controller
 */
@Slf4j
@Api(value = "跟单模板管理", tags = {"跟单模板管理"})
@RestController
@RequestMapping("/api/produce/track_head_template")
public class TrackHeadTemplateController {

    @Autowired
    private TrackHeadTemplateService trackHeadTemplateService;

    @Autowired
    private TrackHeadTemplateFormService trackHeadTemplateFormService;

    public static String TRACK_HEAD_TEMPLATE_ID_NULL_MESSAGE = "ID不能为空！";
    public static String TRACK_HEAD_TEMPLATE_NO_NULL_MESSAGE = "模板编号不能为空！";
    public static String TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE = "操作成功！";
    public static String TRACK_HEAD_TEMPLATE_FAILED_MESSAGE = "操作失败，请重试！";

    @ApiOperation(value = "新增跟单模板", notes = "新增跟单模板")
    @PostMapping("/track_head_template")
    public CommonResult<TrackHeadTemplate> addTrackHeadTemplate(@RequestBody TrackHeadTemplate trackHeadTemplate){
        if(StringUtils.isNullOrEmpty(trackHeadTemplate.getTemplateNo())){
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_NO_NULL_MESSAGE);
        } else {
            trackHeadTemplate.setCreateBy("test");
            trackHeadTemplate.setCreateTime(new Date());
            boolean bool = trackHeadTemplateService.save(trackHeadTemplate);
            if(bool){
                return CommonResult.success(trackHeadTemplate, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "新增跟单表单模板", notes = "新增跟单表单模板")
    @PostMapping("/track_head_template_form/{templateId}")
    public CommonResult addTrackHeadTemplateForm(@PathVariable String templateId, @RequestBody List<TrackHeadTemplateForm> trackHeadTemplateFormList){

        trackHeadTemplateFormList.stream().forEach(trackHeadTemplateForm->{
            trackHeadTemplateForm.setTemplateId(templateId);
            trackHeadTemplateForm.setCreateBy("test");
            trackHeadTemplateForm.setCreateTime(new Date());
        });

        boolean bool = trackHeadTemplateFormService.saveBatch(trackHeadTemplateFormList);
        if(bool){
            return CommonResult.success(null, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "修改跟单模板", notes = "修改跟单模板")
    @PutMapping("/track_head_template")
    public CommonResult<TrackHeadTemplate> updateTrackHeadTemplate(@RequestBody TrackHeadTemplate trackHeadTemplate){
        if(StringUtils.isNullOrEmpty(trackHeadTemplate.getId())){
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_ID_NULL_MESSAGE);
        } else if(StringUtils.isNullOrEmpty(trackHeadTemplate.getTemplateNo())){
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_NO_NULL_MESSAGE);
        } else {
            trackHeadTemplate.setModifyBy("test");
            trackHeadTemplate.setModifyTime(new Date());
            boolean bool = trackHeadTemplateService.updateById(trackHeadTemplate);
            if(bool){
                return CommonResult.success(trackHeadTemplate, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "修改跟单表单模板", notes = "修改跟单表单模板")
    @PutMapping("/track_head_template_form")
    public CommonResult updateTrackHeadTemplateForm(List<TrackHeadTemplateForm> trackHeadTemplateFormList){

        trackHeadTemplateFormList.stream().forEach(trackHeadTemplateForm->{
            trackHeadTemplateForm.setModifyBy("test");
            trackHeadTemplateForm.setModifyTime(new Date());
        });

        boolean bool = trackHeadTemplateFormService.updateBatchById(trackHeadTemplateFormList);
        if(bool){
            return CommonResult.success(trackHeadTemplateFormList, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
        }

    }

    @ApiOperation(value = "删除跟单模板", notes = "删除跟单模板")
    @DeleteMapping("/track_head_template")
    public CommonResult deleteTrackHeadTemplate(@RequestBody List<String> ids){
        if(ids == null || ids.size() == 0){
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackHeadTemplateService.removeByIds(ids);
            if(bool){
                return CommonResult.success(null, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除跟单表单模板", notes = "删除跟单表单模板")
    @DeleteMapping("/track_head_template_form")
    public CommonResult deleteTrackHeadTemplateForm(@RequestBody List<String> ids){
        if(ids == null || ids.size() == 0){
            return CommonResult.failed(TRACK_HEAD_TEMPLATE_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackHeadTemplateFormService.removeByIds(ids);
            if(bool){
                return CommonResult.success(null, TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(TRACK_HEAD_TEMPLATE_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询跟单模板", notes = "分页查询跟单模板")
    @GetMapping("/track_head_template")
    public CommonResult<IPage<TrackHeadTemplate>> selectTrackHeadTemplatePage(String templateNo, int page, int limit){
        QueryWrapper<TrackHeadTemplate> queryWrapper = new QueryWrapper<TrackHeadTemplate>();
        if(!StringUtils.isNullOrEmpty(templateNo)){
            queryWrapper.like("template_no", "%" + templateNo + "%");
        }
        return CommonResult.success(trackHeadTemplateService.page(new Page<TrackHeadTemplate>(page, limit), queryWrapper), TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单模板", notes = "查询跟单模板")
    @GetMapping("/track_head_template/list")
    public CommonResult<List<TrackHeadTemplate>> selectTrackHeadTemplateList(String branchCode){
        QueryWrapper<TrackHeadTemplate> queryWrapper = new QueryWrapper<TrackHeadTemplate>();
        if(!StringUtils.isNullOrEmpty(branchCode)){
            queryWrapper.eq("branch_code", branchCode);
        }
        return CommonResult.success(trackHeadTemplateService.list(queryWrapper), TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询跟单表单模板", notes = "分页查询跟单表单模板")
    @GetMapping("/track_head_template_from")
    public CommonResult<IPage<TrackHeadTemplateForm>> selectTrackHeadTemplateFormPage(String templateId, int page, int limit){
        QueryWrapper<TrackHeadTemplateForm> queryWrapper = new QueryWrapper<TrackHeadTemplateForm>();
        if(!StringUtils.isNullOrEmpty(templateId)){
            queryWrapper.eq("template_id", templateId);
        }
        return CommonResult.success(trackHeadTemplateFormService.page(new Page<TrackHeadTemplateForm>(page, limit), queryWrapper), TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单模板表单", notes = "查询跟单模板表单")
    @GetMapping("/track_head_template_from/list")
    public CommonResult<List<TrackHeadTemplateForm>> selectTrackHeadTemplateFormList(String templateId){
        QueryWrapper<TrackHeadTemplateForm> queryWrapper = new QueryWrapper<TrackHeadTemplateForm>();
        if(!StringUtils.isNullOrEmpty(templateId)){
            queryWrapper.eq("template_id", templateId);
        }
        queryWrapper.orderByAsc("row_place","col_place","order_no");
        return CommonResult.success(trackHeadTemplateFormService.list(queryWrapper), TRACK_HEAD_TEMPLATE_SUCCESS_MESSAGE);
    }

}
