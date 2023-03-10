package com.kld.mes.material.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kld.mes.material.service.PhysChemOrderInnerService;
import com.kld.mes.material.utils.OrderUtil;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrderImpactDto;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    //同步状态
    private final static String SYNC_STATUS = "1";
    //未同步状态
    private final static String NO_SYNC_STATUS = "0";
    //材料检测部门未生成报告
    private final static String NO_REPORT_STATUS = "0";
    //未审核状态
    private final static String NO_AUDIT_STATUS = "0";

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
        queryWrapper.inSql("id","SELECT max(id) as id FROM produce_phys_chem_order_inner GROUP BY group_id");
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
            DrawingNoUtil.queryEq(queryWrapper,"draw_no",phyChemTaskVo.getDrawingNo());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getSampleDept())){
            queryWrapper.eq("sample_dept",phyChemTaskVo.getSampleDept());
        }
        if(!StringUtils.isNullOrEmpty(phyChemTaskVo.getBranchCode())){
            queryWrapper.eq("branch_code",phyChemTaskVo.getBranchCode());
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
            //查询出的委托组id集合
            List<String> groupIds = page.getRecords().stream().map(PhysChemOrderInner::getGroupId).collect(Collectors.toList());
            QueryWrapper<PhysChemOrderInner> physChemOrderInnerQueryWrapper = new QueryWrapper<>();
            physChemOrderInnerQueryWrapper.in("group_id", groupIds);
            List<PhysChemOrderInner> list = physChemOrderInnerService.list(physChemOrderInnerQueryWrapper);
            //分组便于取值赋值
            Map<String, List<PhysChemOrderInner>> orderMap = list.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getGroupId));
            for (PhysChemOrderInner record : page.getRecords()) {
                List<PhysChemOrderImpactDto> impacts = new ArrayList<>();
                Set<Map<String, String>> batchNos = new HashSet<>();
                //炉号回显
                StringBuilder batchNoStr = new StringBuilder();
                List<PhysChemOrderInner> groupIdGroup = orderMap.get(record.getGroupId());
                Map<String, List<PhysChemOrderInner>> batchGroup = groupIdGroup.stream().collect(Collectors.groupingBy(PhysChemOrderInner::getBatchNo));
                for (String batchNo : batchGroup.keySet()) {
                    HashMap<String, String> batchNoMap = new HashMap<>();
                    batchNoMap.put("batchNo",batchNo);
                    batchNos.add(batchNoMap);
                    if(!org.springframework.util.StringUtils.isEmpty(String.valueOf(batchNoStr))){
                        batchNoStr.append(",");
                    }
                    batchNoStr.append(batchNo);
                }
                record.setBatchNo(String.valueOf(batchNoStr));
                record.setBatchNos(batchNos);
                //力学性能-冲击赋值值回显
                if(batchGroup.values().size()>0){
                    List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>(batchGroup.values()).get(0);
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
        }
        return  page;
    }

    /**
     * 根据委托单号查询委托单
     */
    @ApiOperation(value = "根据委托组id查询委托单", notes = "根据委托组id查询委托单")
    @ApiImplicitParam(name = "groupId", value = "委托组id", paramType = "query", dataType = "String")
    @GetMapping("/queryByGroupId")
    public List<PhysChemOrderInner> queryByGroupId(@RequestParam("groupId") String groupId){
        List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
        //分组去数据  由于委托单和试验结果数据存一张表  造成委托单数据冗余
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(groupId)){
            queryWrapper.eq("group_id",groupId);
            physChemOrderInners = physChemOrderInnerService.list(queryWrapper);
        }
        return  physChemOrderInners;
    }

    /**
     * 根据委托单号查询委托单
     */
    @ApiOperation(value = "根据orderNo查询委托单", notes = "根据orderNo查询委托单")
    @ApiImplicitParam(name = "orderNo", value = "委托单号", paramType = "query", dataType = "String")
    @GetMapping("/queryByOrderNo")
    public List<PhysChemOrderInner> queryByOrderNo(@RequestParam("orderNo") String orderNo){
        List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
        //分组去数据  由于委托单和试验结果数据存一张表  造成委托单数据冗余
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(orderNo)){
            queryWrapper.eq("order_no",orderNo);
            physChemOrderInners = physChemOrderInnerService.list(queryWrapper);
        }
        return  physChemOrderInners;
    }

    /**
     * 根据报告号查询中间表数据
     */
    @ApiOperation(value = "根据报告号查询中间表数据", notes = "根据报告号查询中间表数据")
    @ApiImplicitParam(name = "reportNo", value = "报告号", paramType = "query", dataType = "String")
    @GetMapping("/queryByReportNo")
    public List<PhysChemOrderInner> queryByReportNo(@RequestParam("reportNo") String reportNo){
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(reportNo)){
            queryWrapper.eq("report_no",reportNo);
        }
        return  physChemOrderInnerService.list(queryWrapper);
    }

    /**
     * 根据委托组Id删除委托单
     */
    @ApiOperation(value = "根据委托组Id删除委托单", notes = "根据委托组Id删除委托单")
    @ApiImplicitParam(name = "groupId", value = "委托组Id", paramType = "query", dataType = "String")
    @GetMapping("/deleteByGroupId")
    public boolean deleteByGroupId(@RequestParam("groupId") String groupId){
        //删除委托单号对应的所有数据
        QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId);
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
     * 批量修改
     */
    @ApiOperation(value = "批量修改", notes = "批量修改")
    @ApiImplicitParam(name = "orderNos", value = "委托单号", paramType = "query", dataType = "String")
    @PostMapping("/changeOrderStatus")
    public boolean changeOrderStatus(@RequestBody List<PhysChemOrderInner> physChemOrderInners){
        return physChemOrderInnerService.updateBatchById(physChemOrderInners);
    }


    @ApiOperation(value = "根据报告号查询数据", notes = "根据报告号查询数据")
    @ApiImplicitParam(name = "reportNo", value = "报告号", paramType = "body", dataType = "list")
    @PostMapping("/synResultInfos")
    public List<PhysChemOrderInner> synResultInfos(@RequestBody List<String> reportNos){
        return physChemOrderInnerService.synResultInfos(reportNos);
    }

    @ApiOperation(value = "根据委托组id获取数据", notes = "根据委托组id获取数据")
    @ApiImplicitParam(name = "groupIds", value = "委托组ids", paramType = "body", dataType = "list")
    @PostMapping("/getInnerListByGroupIds")
    public List<PhysChemOrderInner> getInnerListByGroupIds(@RequestBody List<String> groupIds){
        return physChemOrderInnerService.getInnerListByGroupIds(groupIds);
    }


    @ApiOperation(value = "根据炉批号获取委托单数据", notes = "根据炉批号获取委托单数据")
    @ApiImplicitParam(name = "batchNo", value = "炉批号", paramType = "query", dataType = "String")
    @GetMapping("/getListByBatchNo")
    public List<PhysChemOrderInner>  getListByBatchNo(String batchNo){
        return physChemOrderInnerService.getListByBatchNo(batchNo);
    }



    @ApiOperation(value = "已同步理化检测委托单审核", notes = "已同步理化检测委托单审核")
    @ApiImplicitParam(name = "reportNos", value = "报告号", required = true, paramType = "body", dataType = "list")
    @PostMapping("/auditSnyPhysChemOrder")
    public CommonResult<Boolean> auditPhysChemOrder(@RequestBody List<String> reportNos, String isAudit,String auditBy){
        List<PhysChemOrderInner> physChemOrderInners = physChemOrderInnerService.synResultInfos(reportNos);
        for (PhysChemOrderInner order : physChemOrderInners) {
            //已同步的委托单才能审核
            if(!SYNC_STATUS.equals(order.getSyncStatus())){
                throw new GlobalException("已同步实验数据的委托单才能审核！", ResultCode.FAILED);
            }
        }
        UpdateWrapper<PhysChemOrderInner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("report_no",reportNos)
                .set("is_audit",isAudit)
                //退回状态需要重置同步状态和报告生成状态
                .set(!StringUtils.isNullOrEmpty(isAudit)&&isAudit.equals("2"),"sync_status",NO_SYNC_STATUS)
                .set(!StringUtils.isNullOrEmpty(isAudit)&&isAudit.equals("2"),"report_status",NO_REPORT_STATUS)
                .set("audit_time",new Date())
                .set("audit_by",auditBy);
        return CommonResult.success(physChemOrderInnerService.update(updateWrapper));
    }

    @ApiOperation(value = "已审核委托单合格判定", notes = "已审核委托单合格判定")
    @ApiImplicitParam(name = "reportNos", value = "报告号", required = true, paramType = "body", dataType = "list")
    @PostMapping("/isStandard")
    public CommonResult<Boolean> isStandard(@RequestBody List<String> reportNos, String isStandard,String standardBy){
        List<PhysChemOrderInner> physChemOrderInners = physChemOrderInnerService.synResultInfos(reportNos);
        for (PhysChemOrderInner order : physChemOrderInners) {
            //以审核的才能进行合格判定
            if(NO_AUDIT_STATUS.equals(order.getIsAudit())){
                throw new GlobalException("以审核的委托单才能进行合格判定！", ResultCode.FAILED);
            }
        }
        UpdateWrapper<PhysChemOrderInner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("report_no",reportNos)
                .set("is_standard",isStandard)
                .set("standard_time",new Date())
                .set("standard_by",standardBy);
        return CommonResult.success(physChemOrderInnerService.update(updateWrapper));
    }

    @ApiOperation(value = "删除委托单", notes = "删除委托单")
    @ApiImplicitParam(name = "groupId", value = "委托单组id", required = true, paramType = "body", dataType = "list")
    @GetMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam String groupId){
        QueryWrapper<PhysChemOrderInner> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("group_id",groupId);
        return CommonResult.success(physChemOrderInnerService.remove(deleteWrapper));
    }






}
