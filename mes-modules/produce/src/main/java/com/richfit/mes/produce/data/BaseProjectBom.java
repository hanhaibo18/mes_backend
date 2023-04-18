package com.richfit.mes.produce.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    @Autowired
    private TrackHeadMapper trackHeadMapper;
    @Autowired
    private BaseServiceClient baseServiceClient;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";//mysql驱动
        String urlBase = "jdbc:mysql://10.134.100.41:3306/mes_base";//连接地址
        String userBase = "mes_base";//用户
        String passwordBase = "Mes_base@mes2022";//密码
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
        planQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY");
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
        trackHeadQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY");
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

    @GetMapping("/project_bom")
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectBom() {
        System.out.println("更新开始");
        //获取未绑定project_bom_id的track_head_id
        List<String> trackHeadIds = trackHeadMapper.selectIdWithoutProjectBom();
        //根据ids获取图号工作号信息
        List<TrackHead> trackHeads = trackHeadMapper.selectByIds(trackHeadIds);
        Map<String, List> listMap = baseServiceClient.bindingBom(trackHeads);
        //获取已存在bom但是未绑定的trackHeadList
        // TODO: 2023/4/18
        List<TrackHead> bindingBomTrackHeads = listMap.get("trackHeadList");
        trackHeadService.updateBatchById(bindingBomTrackHeads);

        //获取不存在bom的trackHeadIds
        // TODO: 2023/4/18
        List<String> noBomIds = listMap.get("noBomIds");

        //不存在bom的新生成bom
        List<ProjectBom> bomList = new ArrayList<>();
        List<TrackAssembly> assemblyList = trackHeadMapper.selectAssemblyByTrackHeadIds(noBomIds);
        for (TrackAssembly trackAssembly : assemblyList) {
            ProjectBom bom = new ProjectBom();
            bom.setPublishState(1);
            bom.setWorkPlanNo(trackAssembly.getWorkNo());
            bom.setTenantId(trackAssembly.getTenantId());
            bom.setDrawingNo(trackAssembly.getDrawingNo());
            bom.setMaterialNo(trackAssembly.getMaterialNo());
            bom.setGrade(trackAssembly.getGrade());
            bom.setIsNumFrom(trackAssembly.getIsNumFrom());
            bom.setIsKeyPart(trackAssembly.getIsKeyPart());
            bom.setIsNeedPicking(trackAssembly.getIsNeedPicking());
            bom.setIsEdgeStore(trackAssembly.getIsEdgeStore());
            bom.setIsCheck(trackAssembly.getIsCheck());
            bom.setNumber(trackAssembly.getNumber());
            bom.setTrackType(trackAssembly.getTrackType());
            bom.setWeight(Float.parseFloat(trackAssembly.getWeight().toString()));
            bom.setUnit(trackAssembly.getUnit());
            bom.setSourceType(trackAssembly.getSourceType());
            bom.setState("1");
            bom.setPublishState(1);
            bom.setProjectName("图号：" + trackAssembly.getDrawingNo() + ",工作号：" + trackAssembly.getWorkNo() + " 逆向生成");

            bomList.add(bom);
        }
        baseServiceClient.addBom(bomList);
        //给新增的bom绑定trackHead
        trackHeads = trackHeadMapper.selectByIds(noBomIds);
        listMap = baseServiceClient.bindingBom(trackHeads);
        bindingBomTrackHeads = listMap.get("trackHeadList");
        trackHeadService.updateBatchById(bindingBomTrackHeads);

        System.out.println("更新完成");
    }

}
