package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * <p>
 * 跟单模板打印
 * </p>
 *
 * @author 马峰
 * @since 2022-2-14
 */
@Slf4j
@Api("跟单模板打印")
@RestController
@RequestMapping("/api/produce/jdbc")
public class TrackJdbcController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;



    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/query")
    public List<Map<String, Object>> query(String table, String where,String limit) throws IOException {
        where = where.toLowerCase();
        where = where.replace("insert","");
        where = where.replace("delete","");
        where = where.replace("drop","");
        where = where.replace("truncate","");
        where = where.replace("exex","");
        String sql = "select * from "+table+" where " + where+" LIMIT "+limit;
        return  jdbcTemplate.queryForList(sql);

    }

    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/total")
    public List<Map<String, Object>> total(String table, String where) throws IOException {
        where = where.toLowerCase();
        where = where.replace("insert","");
        where = where.replace("delete","");
        where = where.replace("drop","");
        where = where.replace("truncate","");
        where = where.replace("exex","");
        String sql = "select count(1) as count from "+table+" where " + where;
        return  jdbcTemplate.queryForList(sql);

    }
}
