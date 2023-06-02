package com.richfit.mes.produce.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.TrackAssemblyService;
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
    @Autowired
    private TrackAssemblyService trackAssemblyService;

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
        planQueryWrapper.eq("tenant_id", "12345678901234567890123456789002").eq("branch_code", "BOMCO_BF_BY").isNull("project_bom");
        List<Plan> plans = planService.list(planQueryWrapper);
        for (Plan plan : plans) {
            for (Map<String, Object> map : maps) {
                if (plan.getDrawNo().equals(map.get("drawing_no")) && plan.getWorkNo().equals(map.get("work_plan_no"))) {
                    plan.setProjectBom(map.get("id").toString());
                    plan.setProjectBomWork(map.get("work_plan_no").toString());
                    plan.setProjectBomName(map.get("project_name").toString());
                    plan.setProjectBomGroup("{}");
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
    public void updateProjectBom() {
        System.out.println("更新开始");
        //获取未绑定project_bom_id的track_head_id
        List<String> trackHeadIdList = trackHeadMapper.selectIdWithoutProjectBom();
        int i = 1;
        //拆分一次查询100个
        List<List> splitList = splitList(trackHeadIdList, 100);
        for (List trackHeadIds : splitList) {
            projectBomUpdate(trackHeadIds, i);
            i++;
        }

        System.out.println("更新完成");
    }

    @GetMapping("/assembly")
    public void updateAssembly() {
        //获取绑定装配且装配表的projectBomId为空的跟单号以及跟单号对应的主项目bomId
        List<TrackHead> trackHeadList = trackHeadMapper.selectNoBomIdTrack();
        int i = 1;
        List<List> splitList = splitList(trackHeadList, 100);
        for (List<TrackHead> trackHeads : splitList) {
            addProjectBomIdtoAssembly(trackHeads);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void addProjectBomIdtoAssembly(List<TrackHead> trackHeads) {
        for (TrackHead trackHead : trackHeads) {
            //用来保存需要变更的装配信息
            List<TrackAssembly> updateAssemblyList = new ArrayList<>();
            //获取当前跟单的所有bom信息
            List<ProjectBom> bomList = baseServiceClient.getBomListByMainBomId(trackHead.getProjectBomId());
            if (CollectionUtils.isEmpty(bomList)) {
                break;
            }
            for (ProjectBom projectBom : bomList) {
                QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("drawing_no", projectBom.getDrawingNo()).eq("material_no", projectBom.getMaterialNo())
                        .eq("grade", projectBom.getGrade()).eq("tenant_id", projectBom.getTenantId())
                        .eq("branch_code", projectBom.getBranchCode()).eq("track_head_id", trackHead.getId());
                List<TrackAssembly> trackAssemblyList = trackAssemblyService.list(queryWrapper);
                //找到唯一的装配信息，添加projectBomId然后保存到updateList
                if (trackAssemblyList != null && trackAssemblyList.size() == 1) {
                    trackAssemblyList.get(0).setProjectBomId(projectBom.getId());
                    trackAssemblyList.get(0).setOptName(projectBom.getOptName());
                    updateAssemblyList.add(trackAssemblyList.get(0));
                } else if (trackAssemblyList != null && projectBom.getOptName() != null) {
                    for (TrackAssembly trackAssembly : trackAssemblyList) {
                        trackAssembly.setOptName(projectBom.getOptName());
                    }
                    updateAssemblyList.addAll(trackAssemblyList);
                }
            }
            if (CollectionUtils.isNotEmpty(updateAssemblyList)) {
                trackAssemblyService.updateBatchById(updateAssemblyList);
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void projectBomUpdate(List trackHeadIds, int i) {
        if (trackHeadIds != null) {
            //根据ids获取图号工作号信息
            List<TrackHead> trackHeads = trackHeadMapper.selectByIds(trackHeadIds);
            Map<String, Object> objectMap = baseServiceClient.bindingBom(trackHeads);
            //获取不存在bom的trackHeadIds
            List<String> noBomIds = (List<String>) objectMap.get("noBomIds");
            //不存在bom的新生成bom
            List<ProjectBom> bomList = new ArrayList<>();
            if (noBomIds != null) {
                List<TrackAssembly> assemblyList = trackHeadMapper.selectAssemblyByTrackHeadIds(noBomIds);
                Map<String, String> mainProjectBomMap = (Map<String, String>) objectMap.get("projectBomMap");
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
                    bom.setWeight(Float.parseFloat(trackAssembly.getWeight() == null ? "0" : trackAssembly.getWeight().toString()));
                    bom.setUnit(trackAssembly.getUnit());
                    bom.setSourceType(trackAssembly.getSourceType());
                    bom.setState("1");
                    bom.setPublishState(1);
                    bom.setProjectName(trackAssembly.getProductName() == null ? trackAssembly.getDrawingNo() + "_" + trackAssembly.getWorkNo() : trackAssembly.getProductName());
                    bom.setIsResolution("0");
                    bom.setBranchCode(trackAssembly.getBranchCode());
                    if ("H".equals(trackAssembly.getGrade())) {
                        bom.setDrawingNo(trackAssembly.getDrawingNo());
                    } else {
                        bom.setMainDrawingNo(mainProjectBomMap.get(trackAssembly.getTrackHeadId()));
                    }
                    bomList.add(bom);
                }
                baseServiceClient.addBom(bomList);
                //给新增的bom绑定trackHead
                trackHeads = trackHeadMapper.selectByIds(noBomIds);
                baseServiceClient.bindingBom(trackHeads);
            }
        }
    }

    @GetMapping("/insert_project_bom")
    public void insertTrackAssemblyProjectBom() {
        System.out.println("更新开始");
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("tenant_id", "12345678901234567890123456789002");
        queryWrapper.eq("branch_code", "BOMCO_BF_BY");
        queryWrapper.isNull("project_bom_id");
        List<TrackHead> trackHeads = trackHeadService.list(queryWrapper);
        for (TrackHead trackHead : trackHeads) {
            String planId = trackHead.getWorkPlanId();
        }
        System.out.println("更新完成");
    }

    public static List<List> splitList(List list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }
        List<List> result = new ArrayList<List>();

        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

}
