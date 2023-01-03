package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.TrackAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

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
    private TrackAssignService trackAssignService;

    @ApiOperation(value = "未报工查询")
    @PostMapping("/queryNotAtWork")
    public CommonResult<IPage<Assign>> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(trackAssignService.queryNotAtWork(dispatchingDto));
    }
}
