package com.kld.mes.material.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kld.mes.material.service.PhysChemOrderInnerService;
import com.kld.mes.material.utils.OrderUtil;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrderImpactDto;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        queryWrapper.inSql("id","SELECT max(id) as id FROM produce_phys_chem_order_inner GROUP BY order_no");
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getBatchNo())){
            queryWrapper.eq("batch_no",phyChemTaskVo.getBatchNo());
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
            queryWrapper.ge("date_format(modify_time, '%Y-%m-%d')",phyChemTaskVo.getStartTime());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getEndTime())){
            queryWrapper.le("date_format(modify_time, '%Y-%m-%d')",phyChemTaskVo.getEndTime());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getStatus())){
            queryWrapper.in("status",phyChemTaskVo.getStatus().split(","));
        }
        //排序
        OrderUtil.query(queryWrapper, phyChemTaskVo.getOrderCol(), phyChemTaskVo.getOrder());
        //分页
        Page<PhysChemOrderInner> page = physChemOrderInnerService.page(new Page<>(phyChemTaskVo.getPage(), phyChemTaskVo.getLimit()), queryWrapper);

        if(page.getRecords().size()>0) {
            //查询出的委托单号集合
            List<String> orderNos = page.getRecords().stream().map(PhysChemOrderInner::getOrderNo).collect(Collectors.toList());
            QueryWrapper<PhysChemOrderInner> physChemOrderInnerQueryWrapper = new QueryWrapper<>();
            physChemOrderInnerQueryWrapper.in("order_no", orderNos);
            List<PhysChemOrderInner> list = physChemOrderInnerService.list(physChemOrderInnerQueryWrapper);
            //分组便于取值赋值
            Map<String, List<PhysChemOrderInner>> orderMap = list.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getOrderNo));
            for (PhysChemOrderInner record : page.getRecords()) {
                List<PhysChemOrderImpactDto> impacts = new ArrayList<>();
                //力学性能-冲击赋值
                List<PhysChemOrderInner> physChemOrderInners = orderMap.get(record.getOrderNo());
                for (PhysChemOrderInner physChemOrderInner : physChemOrderInners) {
                    if(StringUtils.isNullOrEmpty(physChemOrderInner.getForceImpactDirection())
                    && StringUtils.isNullOrEmpty(physChemOrderInner.getForceImpactGap())
                    && StringUtils.isNullOrEmpty(physChemOrderInner.getForceImpactTemp())){
                        continue;
                    }else{
                        PhysChemOrderImpactDto physChemOrderImpactDto = new PhysChemOrderImpactDto();
                        BeanUtils.copyProperties(physChemOrderInner, physChemOrderImpactDto);
                        impacts.add(physChemOrderImpactDto);
                    }


                }
                record.setImpacts(impacts);
            }
        }
        return  page;
    }

    /**
     * 分页查询委托单数据
     */
    @ApiOperation(value = "根据委托单号查询委托单", notes = "根据委托单号查询委托单")
    @ApiImplicitParam(name = "orderNo", value = "委托单号", paramType = "query", dataType = "String")
    @GetMapping("/queryByOrderNo")
    public List<PhysChemOrderInner> queryByOrderNo(@RequestParam("orderNo") String orderNo){
        //分组去数据  由于委托单和试验结果数据存一张表  造成委托单数据冗余
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(orderNo)){
            queryWrapper.eq("order_no",orderNo);
        }
        return  physChemOrderInnerService.list(queryWrapper);
    }

    /**
     * 分页查询委托单数据
     */
    @ApiOperation(value = "根据委托单号删除委托单", notes = "根据委托单号删除委托单")
    @ApiImplicitParam(name = "orderNo", value = "委托单号", paramType = "query", dataType = "String")
    @GetMapping("/deleteByOrderNo")
    public boolean deleteByOrderNo(@RequestParam("orderNo") String orderNo){
        //删除委托单号对应的所有数据
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        return  physChemOrderInnerService.remove(queryWrapper);
    }



    /**
     * 保存委托单
     * @param physChemOrderInners
     * @return
     */
    @ApiOperation(value = "保存委托单", notes = "保存委托单")
    @ApiImplicitParam(name = "physChemOrderInners", value = "委托单List", paramType = "body", dataType = "List")
    @PostMapping("/saveOrder")
    public boolean saveOrder(@RequestBody List<PhysChemOrderInner> physChemOrderInners){
        for (PhysChemOrderInner physChemOrderInner : physChemOrderInners) {
            if(StringUtils.isNullOrEmpty(physChemOrderInner.getId())){
                physChemOrderInner.setCreateTime(DateUtil.date());
            }
            physChemOrderInner.setModifyTime(DateUtil.date());
        }
        //由于委托单冲击试验有些数据是多选的所以 一个委托单会生成单号和报告号一样的多条数据
        return physChemOrderInnerService.saveBatch(physChemOrderInners);
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
     * 批量委托
     */
    @ApiOperation(value = "批量委托", notes = "批量委托")
    @ApiImplicitParam(name = "orderNos", value = "委托单号", paramType = "query", dataType = "String")
    @PostMapping("/changeOrderStatus")
    public boolean changeOrderStatus(@RequestBody List<String> orderNos){
        UpdateWrapper<PhysChemOrderInner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("order_no",orderNos)
                .set("status","1");
        return physChemOrderInnerService.update(updateWrapper);
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
