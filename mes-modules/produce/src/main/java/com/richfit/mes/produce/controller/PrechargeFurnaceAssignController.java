package com.richfit.mes.produce.controller;



import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnaceAssign;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.PrechargeFurnaceAssignService;
import com.richfit.mes.produce.service.TrackItemService;
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
@RequestMapping("/api/produce/prechargeFurnaceAssign")
public class PrechargeFurnaceAssignController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private TrackItemService trackItemService;

    @ApiOperation(value = "批量新增预装炉派工", notes = "批量新增预装炉派工")
    @PostMapping("/furnaceAssign")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> furnaceAssign(@RequestBody JSONObject jsonObject) {
        //预装炉ids
        List<Long> furnaceIds = JSON.parseArray(com.alibaba.fastjson.JSONObject.toJSONString(jsonObject.get("furnaceIds")), Long.class);
        //派工信息
        Assign assign = (Assign) jsonObject.get("assign");
        return CommonResult.success(prechargeFurnaceAssignService.furnaceAssign(assign,furnaceIds));
    }

    @ApiOperation(value = "配炉已派工列表查询")
    @GetMapping("/assigned_furnace_page_list")
    public CommonResult<Page> assignedFurnacePageList(Long id, String texture, String endTime, String startTime, int page, int limit, String branchCode, String workblankType){
        QueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceQueryWrapper.eq("branch_code",branchCode)
                .eq(!ObjectUtil.isEmpty(id),"id",id)
                .eq(!StringUtils.isNullOrEmpty(texture),"texture",texture)
                .ge(!StringUtils.isNullOrEmpty(startTime),"date_format(assign_time, '%Y-%m-%d')", startTime)
                .le(!StringUtils.isNullOrEmpty(endTime),"date_format(assign_time, '%Y-%m-%d')", endTime)
                .eq(!ObjectUtil.isEmpty(workblankType),"workblank_type",workblankType)
                .eq("assign_by", SecurityUtils.getCurrentUser().getUsername());
        return CommonResult.success(prechargeFurnaceAssignService.page(new Page<>(page, limit), prechargeFurnaceQueryWrapper));
    }

    @ApiOperation(value = "根据预装炉派工id查询工序列表查询")
    @GetMapping("/assigned_furnace_item_list")
    public CommonResult<List> assignedFurnaceItemList(Long id){
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("precharge_furnace_assign_id",id);
        return CommonResult.success(trackItemService.list(queryWrapper));
    }
}

