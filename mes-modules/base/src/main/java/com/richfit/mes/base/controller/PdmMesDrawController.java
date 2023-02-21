package com.richfit.mes.base.controller;

import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.service.PdmMesDrawService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.PdmMesDraw;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Slf4j
@Api(value = "图纸", tags = {"图纸"})
@RestController
@RequestMapping("/api/base/mes/pdmDraw")
public class PdmMesDrawController {

    @Autowired
    private PdmMesDrawService pdmMesDrawService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺图纸", notes = "工艺图纸查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序图纸VO", required = true, dataType = "PdmDraw", paramType = "body")
    public CommonResult<List<PdmMesDraw>> getList(PdmMesDraw pdmMesDraw) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.and(wrapper -> wrapper.eq("op_id", pdmMesDraw.getOpId()).or().eq("op_id", pdmMesDraw.getItemId() + "@" + pdmMesDraw.getItemId() + "@" + pdmMesDraw.getDataGroup()));
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesDraw.getDataGroup());
        List<PdmMesDraw> list = pdmMesDrawService.list(queryWrapper);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(list.size());
        System.out.println(pdmMesDraw.getItemId());
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺图纸分页查询", notes = "工艺图纸分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmMesDraw>> getPageList(int page, int limit, PdmMesDraw pdmMesDraw) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.and(wrapper -> wrapper.eq("op_id", pdmMesDraw.getOpId()).or().eq("op_id", pdmMesDraw.getItemId() + "@" + pdmMesDraw.getItemId() + "@" + pdmMesDraw.getDataGroup()));
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesDraw.getDataGroup());
        return CommonResult.success(pdmMesDrawService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "工艺图纸列表查询", notes = "工艺图纸列表查询")
    @GetMapping("/query/drawList/{itemId}/{dataGroup}")
    public List<PdmMesDraw> queryDraw(@PathVariable String itemId, @PathVariable String dataGroup) {
        return pdmMesDrawService.queryDraw(itemId, dataGroup);
    }


    @ApiOperation(value = "上传工艺图纸", notes = "上传工艺图纸")
    @PostMapping("/save")
    public CommonResult savePdmDraw(@RequestBody PdmMesDraw pdmMesDraw) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        pdmMesDraw.setDrawId(UuidUtils.generateUuid().replaceAll("-",""));
        pdmMesDraw.setCreate_by(currentUser.getUsername());//上传人
        pdmMesDraw.setCreateTime(new Date());
        pdmMesDraw.setSycTime(new Date());
        pdmMesDraw.setOp("1");
        pdmMesDraw.setIsUpload(1);
        boolean save = pdmMesDrawService.save(pdmMesDraw);
        if (save) {
            return CommonResult.success(ResultCode.SUCCESS);
        } else {
            return CommonResult.failed(ResultCode.FAILED);
        }
    }

    @ApiOperation(value = "删除工艺图纸", notes = "删除工艺图纸")
    @PostMapping("/delete")
    @ApiImplicitParam(name = "drawId", value = "图纸id", required = true, dataType = "String")
    public CommonResult deletePdmDraw(@RequestParam("drawId") String drawId) {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("draw_id",drawId);
        boolean i = pdmMesDrawService.removeByMap(paramMap);
        if (i) {
            return CommonResult.success(ResultCode.SUCCESS);
        } else {
            return CommonResult.failed(ResultCode.FAILED,"删除失败");
        }
    }
}
