package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.PrechargeFurnaceService;
import com.richfit.mes.produce.service.heat.HeatTrackAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工Controller
 */
@Slf4j
@Api(value = "跟单派工", tags = {"跟单派工"})
@RestController
@RequestMapping("/api/produce/heat/assign")
public class HeatTrackAssignController extends BaseController {

    @Autowired
    private HeatTrackAssignService heatTrackAssignService;

    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;


    @ApiOperation(value = "未报工查询")
    @PostMapping("/queryNotAtWork")
    public CommonResult<IPage<Assign>> queryNotAtWork(@RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryNotAtWork(dispatchingDto));
    }

    @ApiOperation(value = "装炉")
    @PostMapping("/save")
    public CommonResult<List<Assign>> save(@RequestBody List<Assign> assignList) throws ParseException {
        Set<String> optNames = new HashSet();
        PrechargeFurnace prechargeFurnace = new PrechargeFurnace();
        prechargeFurnace.setTempWork("");
        for (Assign assign : assignList) {
            optNames.add(assign.getOptName());
        }
        prechargeFurnace.setOptName(optNames.toString());
        prechargeFurnaceService.save(prechargeFurnace);
        return CommonResult.success(assignList);
    }
}
