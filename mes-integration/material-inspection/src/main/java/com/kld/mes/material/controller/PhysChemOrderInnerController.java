package com.kld.mes.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kld.mes.material.service.PhysChemOrderInnerService;
import com.kld.mes.material.utils.OrderUtil;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/11/17 18:10
 */
@Slf4j
@Api(tags = "理化检测接口")
@RestController
@RequestMapping("/api/material")
public class PhysChemOrderInnerController extends BaseController {

    @Autowired
    private PhysChemOrderInnerService physChemOrderInnerService;


    /**
     * 分页查询委托单数据
     */
    @ApiOperation(value = "分页查询委托单列表", notes = "分页查询委托单列表")
    @ApiImplicitParam(name = "phyChemTaskVo", value = "检验任务查询实体", paramType = "body", dataType = "PhyChemTaskVo")
    @PostMapping("/query/page")
    public Page<PhysChemOrderInner> page(@RequestBody PhyChemTaskVo phyChemTaskVo) throws GlobalException {
        //分组去数据  由于委托单和试验结果数据存一张表  造成委托单数据冗余
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id","SELECT max(id) as id FROM produce_phys_chem_order_inner GROUP BY report_no");
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getBatchNo())){
            queryWrapper.eq("batch_no",phyChemTaskVo.getBatchNo());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getOrderNo())){
            queryWrapper.eq("order_no",phyChemTaskVo.getOrderNo());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getProductName())){
            queryWrapper.eq("product_name",phyChemTaskVo.getProductName());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getDrawingNo())){
            queryWrapper.eq("draw_no",phyChemTaskVo.getDrawingNo());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getSampleDept())){
            queryWrapper.eq("sample_dept",phyChemTaskVo.getSampleDept());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getStartTime())){
            queryWrapper.ge("modify_time",phyChemTaskVo.getStartTime());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getEndTime())){
            queryWrapper.le("modify_time",phyChemTaskVo.getEndTime());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getStatus())){
            queryWrapper.in("status",phyChemTaskVo.getStatus().split(","));
        }
        //排序
        OrderUtil.query(queryWrapper, phyChemTaskVo.getOrderCol(), phyChemTaskVo.getOrder());

        return  physChemOrderInnerService.page(new Page<>(phyChemTaskVo.getPage(), phyChemTaskVo.getLimit()),queryWrapper);
    }

    /**
     * 保存委托单
     * @param physChemOrderInner
     * @return
     */
    @ApiOperation(value = "保存委托单", notes = "保存委托单")
    @ApiImplicitParam(name = "physChemOrderInner", value = "委托单实体", paramType = "body", dataType = "physChemOrderInner")
    @PostMapping("/saveOrder")
    public boolean saveOrder(@RequestBody PhysChemOrderInner physChemOrderInner){
        //在试验结果为生成之前 中间表该报告号只会有一条数据 且是只有委托单的数据
        return physChemOrderInnerService.saveOrUpdate(physChemOrderInner);
    }

    /**
     * 修改委托单状态
     */
    @ApiOperation(value = "修改委托单状态", notes = "修改委托单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportNo", value = "报告号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "reportStatus", value = "委托状态", paramType = "query", dataType = "String")
    })
    @GetMapping("/changeOrderSatus")
    public boolean changeOrderSatus(@RequestParam("reportNo") String reportNo,@RequestParam("reportStatus")  String reportStatus){
        return physChemOrderInnerService.changeOrderSatus(reportNo,reportStatus);
    }

    /**
     * 修改委托单同步状态
     */
    @ApiOperation(value = "修改委托单同步状态", notes = "修改委托单同步状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportNo", value = "报告号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "syncStatus", value = "同步状态", paramType = "query", dataType = "String")
    })
    @GetMapping("/changeOrderSyncStatus")
    public boolean changeOrderSyncSatus(@RequestParam("reportNo") String reportNo,@RequestParam("syncStatus")  String syncStatus){
        return physChemOrderInnerService.changeOrderSyncSatus(reportNo,syncStatus);
    }

    /**
     * 同步实验结果
     */
    @ApiOperation(value = "同步实验结果", notes = "同步实验结果")
    @ApiImplicitParam(name = "reportNo", value = "报告号", paramType = "body", dataType = "list")
    @PostMapping("/synResultInfos")
    public List<PhysChemOrderInner> synResultInfos(@RequestBody List<String> reportNos){
        return physChemOrderInnerService.synResultInfos(reportNos);
    }


    @ApiOperation(value = "根据炉批号获取委托单数据", notes = "根据炉批号获取委托单数据")
    @ApiImplicitParam(name = "batchNo", value = "炉批号", paramType = "query", dataType = "String")
    @GetMapping("/getListByBatchNo")
    public List<PhysChemOrderInner>  getListByBatchNo(String batchNo){
        return physChemOrderInnerService.getListByBatchNo(batchNo);
    }


}
