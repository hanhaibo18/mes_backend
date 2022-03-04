package com.richfit.mes.produce.controller.statistics;

import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.produce.entity.SjtjDto;
import com.richfit.mes.produce.service.SjtjServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 马峰
 */
@Slf4j
@Api("数据统计")
@RestController
@RequestMapping("/api/produce/sjtj")
public class SjtjController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SjtjServiceImpl sjtjService;


    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/query")
    public List<SjtjDto> query1(String branchCode,String createTime, String endTime) {
        List<SjtjDto> list = sjtjService.query1(branchCode,createTime,endTime);
        return list;
    }

}

