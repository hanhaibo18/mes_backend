package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.GetProductNameService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api("查询产品名称")
@RestController
@RequestMapping("/api/base/getProductName")
public class GetProductNameController extends BaseController {
    @Autowired
    private GetProductNameService workingHoursService;

    @GetMapping("/query_product_name")
    public CommonResult<List> queryProductName(String branchCode) {
        QueryWrapper<List> wrapper = new QueryWrapper<List>();
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            wrapper.eq("branch_code", branchCode);
        }
        return CommonResult.success(workingHoursService.queryProductName(wrapper));
    }
}
