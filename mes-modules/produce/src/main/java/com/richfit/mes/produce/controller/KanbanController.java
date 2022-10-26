package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.ProcessTrack;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.KanbanDto;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.service.KanbanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: GaoLiang
 * @Date: 2020/10/16 10:36
 */
@Slf4j
@Api(value = "看板管理", tags = {"看板管理"})
@RestController
@RequestMapping("/api/produce/kanban")
public class KanbanController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KanbanService kanbanService;
    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询机加、装配看板信息", notes = "根据查询条件返回订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="queryDto",value="看板属性", paramType="BasePageDto")
    })
    @GetMapping("/query/progress")
    public CommonResult queryProgress(BasePageDto<String> queryDto) throws GlobalException {

        KanbanDto kanbanDto = null;
        try {
            kanbanDto = objectMapper.readValue(queryDto.getParam(), KanbanDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if(null==kanbanDto){
            kanbanDto = new KanbanDto();
        }

        kanbanDto.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        kanbanDto.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());

        IPage<ProcessTrack> processList = kanbanService.queryProgress(new Page<ProcessTrack>(queryDto.getPage(), queryDto.getLimit()),kanbanDto);

        return CommonResult.success(processList);

    }
}
