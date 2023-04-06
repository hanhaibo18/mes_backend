package com.richfit.mes.base.update;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.base.service.ProductionBomService;
import com.richfit.mes.base.service.ProjectBomService;
import com.richfit.mes.common.model.base.ProductionBom;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author HanHaiBo
 * @date 2023/3/29 13:52
 */

@Slf4j
@Api(value = "更新服务", tags = {"更新服务"})
@RestController
@RequestMapping("/api/base/update")
public class UpdateProjectBom {

    private static JdbcTemplate jdbcTemplateProduce;
    @Autowired
    private ProjectBomService projectBomService;
    @Autowired
    private ProductionBomService productionBomService;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";//mysql驱动
        String urlProduce = "jdbc:mysql://10.134.100.41:3306/mes_produce";//连接地址
        String userProduce = "mes_produce";//用户
        String passwordProduce = "Mes_produce@mes2022";//密码
        DriverManagerDataSource dataSourceProduce = new DriverManagerDataSource();
        dataSourceProduce.setUrl(urlProduce);
        dataSourceProduce.setDriverClassName(driver);
        dataSourceProduce.setUsername(userProduce);
        dataSourceProduce.setPassword(passwordProduce);
        jdbcTemplateProduce = new JdbcTemplate(dataSourceProduce);
    }

    @GetMapping("/projectBom")
    public void updateProjectBom() {
        System.out.println("更新开始");
        String sql = "select draw_no,work_no,ANY_VALUE(draw_no_name) as draw_no_name from produce_plan where tenant_id = '12345678901234567890123456789002' AND branch_code = 'BOMCO_BF_BY' GROUP BY draw_no,work_no ORDER BY draw_no;";//student 数据库表明
        List<Map<String, Object>> maps = jdbcTemplateProduce.queryForList(sql);
        Set<Object> draw_nos = maps.stream().map(x -> x.get("draw_no")).collect(Collectors.toSet());
        QueryWrapper<ProductionBom> productionBomQueryWrapper = new QueryWrapper<>();
        productionBomQueryWrapper.in("drawing_no", draw_nos);
        productionBomQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY").eq("grade", "H");
        List<ProductionBom> productionBoms = productionBomService.list(productionBomQueryWrapper);
        for (Map<String, Object> map : maps) {
            for (ProductionBom productionBom : productionBoms) {
                if (map.get("draw_no").equals(productionBom.getDrawingNo())) {
                    map.put("bom_id", productionBom.getId());
                    break;
                }
            }
        }
        for (Map<String, Object> map : maps) {
            if (map.get("bom_id") != null) {
                String bom_id = map.get("bom_id").toString();
                String work_no = map.get("work_no").toString();
                String draw_no = map.get("draw_no").toString();
                String draw_no_name = map.get("draw_no_name") == null ? draw_no + ":" + work_no : map.get("draw_no_name").toString();
                productionBomService.issueBom(bom_id, work_no, draw_no_name, "12345678901234567890123456789002", "BOMCO_BF_BY");
            }
        }
        System.out.println("更新结束");

    }

}
