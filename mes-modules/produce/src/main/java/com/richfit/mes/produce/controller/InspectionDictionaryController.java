package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.InspectionDictionary;
import com.richfit.mes.produce.service.InspectionDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(tags = "探伤数据字典")
@RestController
@RequestMapping("/api/produce/inspectionDictionary")
public class InspectionDictionaryController {

    @Autowired
    private InspectionDictionaryService inspectionDictionaryService;

    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询全部下拉列表头", notes = "分页查询全部下拉列表头")
    @GetMapping("/getHeadInfo")
    public CommonResult<IPage> queryByCondition(String dicCode, String dicValue,String tempType, int page, int limit) throws GlobalException {
        QueryWrapper<InspectionDictionary> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(dicCode)){
            queryWrapper.eq("dic_code",dicCode);
        }
        if(!StringUtils.isNullOrEmpty(dicValue)){
            queryWrapper.eq("dic_value",dicValue);
        }
        if(!StringUtils.isNullOrEmpty(tempType)){
            queryWrapper.eq("temp_type",tempType);
        }
        //父节点为空 即为下拉列表的头信息
        queryWrapper.isNull("parent_id")
                    //升序排列
                    .orderByAsc("serial_num");

        return CommonResult.success(inspectionDictionaryService.page(new Page<>(page, limit), queryWrapper));
    }


    /**
     * 新增操作信息
     */
    @ApiOperation(value = "新增或修改操作信息", notes = "新增或修改操作信息")
    @PostMapping("/addOrUpdate")
    public CommonResult<Boolean> addOrUpdate(@RequestBody InspectionDictionary inspectionDictionary) throws GlobalException{
        return CommonResult.success(inspectionDictionaryService.saveOrUpdate(inspectionDictionary));
    }

    /**
     * 删除操作信息
     */
    @ApiOperation(value = "删除操作信息", notes = "根据id删除操作信息")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException{
        //父节点需要删除关联的子节点数据
        QueryWrapper<InspectionDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id)
                .or(wrapper->wrapper.eq("parent_id",id));
        return CommonResult.success(inspectionDictionaryService.remove(queryWrapper));
    }



    /**
     * 根据ID获取下拉列表值信息
     */
    @ApiOperation(value = "根据ID获取下拉列表值信息", notes = "根据ID获取下拉列表值信息")
    @GetMapping("/getListInfoById/{id}")
    public CommonResult<List> getListInfoById(@PathVariable String id) throws GlobalException {
        QueryWrapper<InspectionDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id)
                .orderByAsc("serial_num");
        inspectionDictionaryService.list(queryWrapper);
        return CommonResult.success(inspectionDictionaryService.list(queryWrapper));
    }

    /**
     * 根据模板类型获取全部下拉列表信息
     */
    @ApiOperation(value = "根据模板类型获取全部下拉列表信息", notes = "根据模板类型获取全部下拉列表信息")
    @GetMapping("/getListInfoByTempType/{tempType}")
    public CommonResult<Map> getListInfoByTempType(@PathVariable String tempType) throws GlobalException {
        QueryWrapper<InspectionDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("temp_type",tempType)
                .orderByAsc("serial_num");
        List<InspectionDictionary> list = inspectionDictionaryService.list(queryWrapper);

        //头信息
        Map<String, InspectionDictionary> headsMap = list.stream().filter(item -> StringUtils.isNullOrEmpty(item.getParentId()))
                .collect(Collectors.toMap(InspectionDictionary::getId, Function.identity()));
        //下拉列表信息
        Map<String, List<InspectionDictionary>> valuesMap = list.stream().filter(item -> !StringUtils.isNullOrEmpty(item.getParentId()))
                .collect(Collectors.groupingBy(InspectionDictionary::getParentId));

        Map<String, List> returnMap = new HashMap<>();

        headsMap.forEach((key,value)->{
            if(!StringUtils.isNullOrEmpty(value.getDicCode())){
                returnMap.put(value.getDicCode(),!ObjectUtil.isEmpty(valuesMap.get(key))?valuesMap.get(key):new ArrayList());
            }
        });

        return CommonResult.success(returnMap);
    }



}
