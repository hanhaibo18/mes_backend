package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmBomService;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmBom;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzw
 * @date 2022-01-12 13:27
 */
@Slf4j
@Api("图纸")
@RestController
@RequestMapping("/api/base/pdmBom")
public class PdmBomController {

    @Autowired
    private PdmBomService pdmBomService;

    @GetMapping(value = "/getBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getList(String id, String ver){
        ArrayList<PdmBom> pdmBoms = new ArrayList<>();
        pdmBoms.add(pdmBomService.getBomByProcessIdAndRev(id, ver));
        return CommonResult.success(pdmBoms);
    }

    @PostMapping(value = "/getChildBomByProcessIdAndRev")
    public CommonResult<List<PdmBom>> getChildBomList(String id, String ver){
        String opId = id + "@" + ver;
        QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("p_id",opId)
        .orderByAsc("order_no+1");
        return CommonResult.success(pdmBomService.list(queryWrapper));
    }
}
