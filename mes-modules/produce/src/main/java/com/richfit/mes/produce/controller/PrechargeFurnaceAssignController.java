package com.richfit.mes.produce.controller;



import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnaceAssign;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.service.PrechargeFurnaceAssignService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (PrechargeFurnaceAssign)表控制层
 *
 * @author makejava
 * @since 2023-05-19 10:36:13
 */
@RestController
@RequestMapping("prechargeFurnaceAssign")
public class PrechargeFurnaceAssignController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;

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
}

