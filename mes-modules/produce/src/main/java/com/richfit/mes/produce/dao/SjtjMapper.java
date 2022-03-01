package com.richfit.mes.produce.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface SjtjMapper {
    @Select("select b.template_code,sum(b.number) number from produce_track_head b where b.branch_code like '%%BOMCO_ZS%%' GROUP BY b.template_code")
    List<Map> query1(String branchCode);
    @Select(" select b.template_code, sum(a.qty) qty FROM\n" +
            " produce_assign a\n" +
            " LEFT JOIN produce_track_head b ON a.tenant_id = b.tenant_id \n" +
            " WHERE b.branch_code LIKE '%BOMCO_ZS%' GROUP BY b.template_code")
    List<Map> query2();
    @Select(" select b.template_code, sum(a.completed_qty) completed_qty FROM\n" +
            " produce_track_complete a\n" +
            " LEFT JOIN produce_track_head b ON a.tenant_id = b.tenant_id \n" +
            " WHERE b.branch_code LIKE '%BOMCO_ZS%' GROUP BY b.template_code")
    List<Map> query3();
    @Select(" select b.template_code, sum(a.qualify + a.unqualify) qualify FROM\n" +
            " produce_track_check a\n" +
            " LEFT JOIN produce_track_head b ON a.tenant_id = b.tenant_id \n" +
            " WHERE b.branch_code LIKE '%BOMCO_ZS%' GROUP BY b.template_code")
    List<Map> query4();
}
