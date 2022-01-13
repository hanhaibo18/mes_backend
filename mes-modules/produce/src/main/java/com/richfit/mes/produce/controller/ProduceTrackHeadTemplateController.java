package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import feign.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gwb
 * @since 2022-01-10
 */
@Slf4j
@Api("接口")
@RestController
@RequestMapping("/api/produce/produce-track-head-template")
public class ProduceTrackHeadTemplateController{

    @Autowired
    private ProduceTrackHeadTemplateService produceTrackHeadTemplateService;


    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询接口", notes = "分页查询接口")
    @GetMapping("/page")
    public CommonResult queryByCondition( int page, int limit) throws GlobalException {
        QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
        return CommonResult.success(produceTrackHeadTemplateService.page(new Page<ProduceTrackHeadTemplate>(page, limit), queryWrapper));
    }

    /**
     * 新增操作信息
     */
    @ApiOperation(value = "新增信息", notes = "新增信息")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<ProduceTrackHeadTemplate> saveProduceTrackHeadTemplate(ProduceTrackHeadTemplate produceTrackHeadTemplate) {

        boolean bool = produceTrackHeadTemplateService.save(produceTrackHeadTemplate);
        if (bool) {
            return CommonResult.success(produceTrackHeadTemplate, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    @ApiOperation(value = "修改信息", notes = "修改信息")
    @PutMapping("/update")
    public CommonResult<Boolean> updateById( ProduceTrackHeadTemplate produceTrackHeadTemplate) throws GlobalException{
        return CommonResult.success(produceTrackHeadTemplateService.updateById(produceTrackHeadTemplate));
    }

    @ApiOperation(value = "根据id删除信息", notes = "根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteById(@Param("id") String id) throws GlobalException{
        return CommonResult.success(produceTrackHeadTemplateService.removeById(id));
    }
}
