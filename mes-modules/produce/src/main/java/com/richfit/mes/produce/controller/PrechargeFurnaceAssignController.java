package com.richfit.mes.produce.controller;



import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.service.PrechargeFurnaceAssignPersonService;
import com.richfit.mes.produce.service.PrechargeFurnaceAssignService;
import com.richfit.mes.produce.service.TrackCompleteService;
import com.richfit.mes.produce.service.TrackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (PrechargeFurnaceAssign)表控制层
 *
 * @author makejava
 * @since 2023-05-19 10:36:13
 */
@RestController
@Api(value = "配炉派工", tags = {"配炉派工"})
@RequestMapping("/api/produce/prechargeFurnaceAssign")
public class PrechargeFurnaceAssignController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private PrechargeFurnaceAssignPersonService prechargeFurnaceAssignPersonService;

    @ApiOperation(value = "批量新增预装炉派工", notes = "批量新增预装炉派工")
    @PostMapping("/furnaceAssign")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> furnaceAssign(@RequestBody JSONObject jsonObject) {
        //预装炉ids
        List<Long> furnaceIds = JSON.parseArray(com.alibaba.fastjson.JSONObject.toJSONString(jsonObject.get("furnaceIds")), Long.class);
        //派工信息
        Assign assign = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("assign"))),Assign.class);
        return CommonResult.success(prechargeFurnaceAssignService.furnaceAssign(assign,furnaceIds));
    }

    @ApiOperation(value = "编辑装炉派工", notes = "编辑装炉派工")
    @PostMapping("/update_furnace_assign")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateFurnaceAssign(@RequestBody JSONObject jsonObject) {
        //派工信息
        Assign assign = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("assign"))),Assign.class);
        //预装炉派工id
        String furnaceAssignId = jsonObject.getString("id");

        return CommonResult.success(prechargeFurnaceAssignService.updateFurnaceAssign(assign,furnaceAssignId));
    }

    @ApiOperation(value = "配炉已派工列表查询")
    @GetMapping("/assigned_furnace_page_list")
    public CommonResult<Page> assignedFurnacePageList(Long id, String texture, String endTime, String startTime, int page, int limit, String branchCode, String workblankType,String orderCol,String order){
        QueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceQueryWrapper.eq("branch_code",branchCode)
                .eq(!ObjectUtil.isEmpty(id),"furnace_id",id)
                .eq(!StringUtils.isNullOrEmpty(texture),"texture",texture)
                .ge(!StringUtils.isNullOrEmpty(startTime),"date_format(assign_time, '%Y-%m-%d')", startTime)
                .le(!StringUtils.isNullOrEmpty(endTime),"date_format(assign_time, '%Y-%m-%d')", endTime)
                .eq(!ObjectUtil.isEmpty(workblankType),"workblank_type",workblankType)
                .eq("assign_by", SecurityUtils.getCurrentUser().getUsername());
        OrderUtil.query(prechargeFurnaceQueryWrapper,orderCol,order);
        Page<PrechargeFurnaceAssign> prechargeFurnaceAssigns = prechargeFurnaceAssignService.page(new Page<>(page, limit), prechargeFurnaceQueryWrapper);
        if (null != prechargeFurnaceAssigns.getRecords()) {
            for (PrechargeFurnaceAssign prechargeFurnaceAssign : prechargeFurnaceAssigns.getRecords()) {
                //添加 派工人员返回
                QueryWrapper<PrechargeFurnaceAssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
                assignPersonQueryWrapper.eq("precharge_furnace_assign_id", prechargeFurnaceAssign.getId());
                prechargeFurnaceAssign.setAssignPersons(prechargeFurnaceAssignPersonService.list(assignPersonQueryWrapper));
            }
        }
        return CommonResult.success(prechargeFurnaceAssigns);
    }

    @ApiOperation(value = "根据预装炉派工id查询工序列表查询")
    @GetMapping("/assigned_furnace_item_list")
    public CommonResult<List> assignedFurnaceItemList(String id){
        return CommonResult.success(prechargeFurnaceAssignService.assignedFurnaceItemList(id));
    }

    @ApiOperation(value = "修改报工(锻造)")
    @PostMapping("/updateComplete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateComplete(@RequestBody List<CompleteDto> completeDtos) {
        for (CompleteDto completeDto : completeDtos) {
            trackCompleteService.updateComplete(completeDto);
        }
        return CommonResult.success(true);
    }
}

