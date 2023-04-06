package com.richfit.mes.produce.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.TrackHeadService;
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

@Slf4j
@Api(value = "更新服务", tags = {"更新服务"})
@RestController
@RequestMapping("/api/produce/update")
public class BaseProjectBom {
    private static JdbcTemplate jdbcTemplateBase;
    @Autowired
    private PlanService planService;
    @Autowired
    private TrackHeadService trackHeadService;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";//mysql驱动
        String urlBase = "jdbc:mysql://11.54.93.106:3306/mes_base";//连接地址
        String userBase = "mes_base";//用户
        String passwordBase = "Mes_base@mes";//密码
        DriverManagerDataSource dataSourceBase = new DriverManagerDataSource();
        dataSourceBase.setUrl(urlBase);
        dataSourceBase.setDriverClassName(driver);
        dataSourceBase.setUsername(userBase);
        dataSourceBase.setPassword(passwordBase);
        jdbcTemplateBase = new JdbcTemplate(dataSourceBase);

    }

    @GetMapping("/plan")
    public void updatePlan() {
        System.out.println("更新开始");
        String sql = "select id,drawing_no,work_plan_no,project_name from base_project_bom WHERE tenant_id = '12345678901234567890123456789002' AND branch_code = 'BOMCO_BF_BY' AND grade = 'H'";
        List<Map<String, Object>> maps = jdbcTemplateBase.queryForList(sql);
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY").groupBy("draw_no", "work_no");
        List<Plan> plans = planService.list(planQueryWrapper);
        for (Plan plan : plans) {
            for (Map<String, Object> map : maps) {
                if (plan.getDrawNo().equals(map.get("drawing_no")) && plan.getWorkNo().equals(map.get("work_plan_no"))) {
                    plan.setProjectBom(map.get("id").toString());
                    plan.setProjectBomWork(map.get("work_plan_no").toString());
                    plan.setProjectBomName(map.get("project_name").toString());
                    break;
                }
            }
        }
        planService.updateBatchById(plans);
        System.out.println("更新完成");
    }

    @GetMapping("/trackHead")
    public void updateTrackHead() {
        System.out.println("更新开始");
        String sql = "select id,drawing_no,work_plan_no,project_name from base_project_bom WHERE tenant_id = '12345678901234567890123456789002' AND branch_code = 'BOMCO_BF_BY' AND grade = 'H'";
        List<Map<String, Object>> maps = jdbcTemplateBase.queryForList(sql);
        QueryWrapper<TrackHead> trackHeadQueryWrapper = new QueryWrapper<>();
        trackHeadQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY").groupBy("drawing_no", "work_no");
        List<TrackHead> trackHeads = trackHeadService.list(trackHeadQueryWrapper);
        for (TrackHead trackHead : trackHeads) {
            for (Map<String, Object> map : maps) {
                if (map.get("drawing_no").equals(trackHead.getDrawingNo()) && map.get("work_plan_no").equals(trackHead.getWorkNo())) {
                    trackHead.setProjectBomId(map.get("id").toString());
                    trackHead.setProjectBomWork(map.get("work_plan_no").toString());
                    trackHead.setProjectBomName(map.get("project_name").toString());
                    break;
                }
            }
        }
        trackHeadService.updateBatchById(trackHeads);
        System.out.println("更新完成");
    }

}
