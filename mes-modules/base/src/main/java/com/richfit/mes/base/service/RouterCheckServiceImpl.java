package com.richfit.mes.base.service;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.RouterCheckMapper;
import com.richfit.mes.base.entity.RouterCheckDto;
import com.richfit.mes.base.entity.RouterCheckQualityDto;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mafeng
 * @Description 工序技术要求
 */
@Service
public class RouterCheckServiceImpl extends ServiceImpl<RouterCheckMapper, RouterCheck> implements RouterCheckService {

    @Autowired
    private RouterCheckMapper routerCheckMapper;
    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private RouterService routerService;

    public IPage<RouterCheck> selectPage(Page page, QueryWrapper<RouterCheck> qw) {
        return routerCheckMapper.selectPage(page, qw);
    }

    @Override
    public List<RouterCheck> queryRouterList(String optId, String type, String branchCode, String tenantId) {
        QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sequence_id", optId)
                .eq("type", type)
                .eq("branch_code", branchCode)
                .orderByDesc("modify_time");
        return this.list(queryWrapper);
    }

    @Override
    public CommonResult importExcelCheck(MultipartFile file, String tenantId, String branchCode) {
        String step = "";
        List<RouterCheckDto> list = new ArrayList<>();
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        try {
            File excelFile = null;
            //给导入的excel一个临时的文件名
            StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
            tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            try {
                //将导入的excel数据生成证件实体类list
                java.lang.reflect.Field[] fields = RouterCheckDto.class.getDeclaredFields();
                //封装证件信息实体类
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                List<RouterCheckDto> checkList = ExcelUtils.importExcel(excelFile, RouterCheckDto.class, fieldNames, 1, 0, 0, tempName.toString());

                step += "获取列表成功";
                List<RouterCheckDto> list2 = new ArrayList<>();
                //图号为空校验
                String isNull = "";
                // 获取图号列表
                String drawnos = "";
                for (int i = 0; i < checkList.size(); i++) {
                    if (!StringUtils.isNullOrEmpty(checkList.get(i).getRouterNo()) && "X".equals(checkList.get(i).getIsImport())) {
                        list2.add(checkList.get(i));
                        if (!drawnos.contains(checkList.get(i).getRouterNo() + ",")) {
                            drawnos += checkList.get(i).getRouterNo() + ",";
                        }
                    }
                    if (StringUtils.isNullOrEmpty(checkList.get(i).getRouterNo()) && "X".equals(checkList.get(i).getIsImport())) {
                        isNull = "图号不能为空";
                    }

                }
                step += "获取图号成功";

                checkList = list2;
                //校验
                if((checkList.size()>0 && StringUtils.isNullOrEmpty(drawnos)) || !StringUtils.isNullOrEmpty(drawnos)){
                    String info = checkExportInfo(drawnos, checkList, branchCode);
                    if(!StringUtils.isNullOrEmpty(isNull)){
                        info = info+isNull;
                    }
                    //校验错误信息
                    if(!StringUtils.isNullOrEmpty(info)){
                        return CommonResult.failed("工序质检技术导入校验错误如下：</br>"+info);
                    }
                }

                list = checkList;
                // 遍历图号插入检查内容
                for (int i = 0; i < drawnos.split(",").length; i++) {
                    // 先删除历史数据
                    QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
                    queryWrapper.eq("type", "检查内容");
                    queryWrapper.eq("tenant_id", tenantId);
                    queryWrapper.eq("branch_code", branchCode);
                    queryWrapper.eq("drawing_no", drawnos.split(",")[i]);
                    this.remove(queryWrapper);

                    for (int j = 0; j < checkList.size(); j++) {
                        // 插入新数据
                        QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<Sequence>();
                        queryWrapper2.eq("opt_name", checkList.get(j).getOptName().trim());
                        queryWrapper2.eq("op_no", checkList.get(j).getOptNo().trim());
                        //queryWrapper2.eq("tenant_id", tenantId);
                        queryWrapper2.eq("branch_code", branchCode);
                        queryWrapper2.inSql("router_id", "select id from base_router where is_active='1' and status !='2' and router_no ='" + drawnos.split(",")[i] + "' and branch_code='" + branchCode + "'");
                        List<Sequence> sequences = sequenceService.list(queryWrapper2);
                        if (sequences.size() >= 1) {
                            step += sequences.get(0).getRouterId() + sequences.get(0).getId() + checkList.get(j).getOptName();
                            if (checkList.get(j).getRouterNo().equals(drawnos.split(",")[i])) {
                                RouterCheck routerCheck = new RouterCheck();
                                routerCheck.setCreateBy(user.getUsername());
                                routerCheck.setRouterId(sequences.get(0).getRouterId());
                                routerCheck.setSequenceId(sequences.get(0).getId());
                                routerCheck.setCreateTime(new Date());
                                routerCheck.setModifyBy(user.getUsername());
                                routerCheck.setModifyTime(new Date());
                                routerCheck.setTenantId(tenantId);
                                routerCheck.setBranchCode(branchCode);
                                routerCheck.setType("检查内容");
                                routerCheck.setName(checkList.get(j).getName());
                                routerCheck.setDrawingNo(drawnos.split(",")[i]);
                                routerCheck.setCheckOrder(Integer.parseInt(checkList.get(j).getOrderNo()));
                                routerCheck.setUnit(checkList.get(j).getPropertyUnit());
                                routerCheck.setMethod(checkList.get(j).getPropertyInputtype());
                                routerCheck.setIsEmpty(1);
                                routerCheck.setDefualtValue(checkList.get(j).getPropertyDefaultvalue());
                                routerCheck.setStatus("1");
                                routerCheck.setPropertySymbol(checkList.get(j).getPropertySymbol());
                                routerCheck.setPropertyLowerlimit(checkList.get(j).getPropertyLowerlimit());
                                routerCheck.setPropertyUplimit(checkList.get(j).getPropertyUplimit());
                                routerCheck.setPropertyTestmethod(checkList.get(j).getPropertyTestmethod());
                                routerCheck.setPropertyDatatype(checkList.get(j).getPropertyInputtype());
                                this.save(routerCheck);
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                step += ex.getMessage();
            }
            step += "检查内容保存完成";
            try {
                java.lang.reflect.Field[] qualityFields = RouterCheckQualityDto.class.getDeclaredFields();
                //封装证件信息实体类
                String[] qualityFieldNames = new String[qualityFields.length];
                for (int i = 0; i < qualityFieldNames.length; i++) {
                    qualityFieldNames[i] = qualityFields[i].getName();
                }
                List<RouterCheckQualityDto> qualityList = ExcelUtils.importExcel(excelFile, RouterCheckQualityDto.class, qualityFieldNames, 1, 0, 1, tempName.toString());
                FileUtils.delete(excelFile);
                step += "资料类型列表获取";
                List<RouterCheckQualityDto> list3 = new ArrayList<>();
                String drawnos = "";
                //过滤获取导入状态为X的数据列
                qualityList = qualityList.stream().filter(item->"X".equals(item.getIsImport())).collect(Collectors.toList());
                for (int i = 0; i < qualityList.size(); i++) {
                    if (!StringUtils.isNullOrEmpty(qualityList.get(i).getRouterNo())) {
                        list3.add(qualityList.get(i));
                    }

                    if (!drawnos.contains(qualityList.get(i).getRouterNo() + ",")) {
                        drawnos += qualityList.get(i).getRouterNo() + ",";
                    }
                }
                qualityList = list3;

                step += "资料类型列表去空";
                // 遍历图号插入资料资料
                for (int i = 0; i < drawnos.split(",").length; i++) {
                    // 先删除历史数据
                    QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
                    queryWrapper.eq("type", "质量资料");
                    queryWrapper.eq("tenant_id", tenantId);
                    queryWrapper.eq("branch_code", branchCode);
                    queryWrapper.eq("drawing_no", drawnos.split(",")[i]);

                    this.remove(queryWrapper);
                    int check_order = 1;
                    // 插入新数据


                    for (int j = 0; j < qualityList.size(); j++) {
                        QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<Sequence>();
                        queryWrapper2.eq("opt_name", qualityList.get(j).getOptName().trim());
                        //queryWrapper2.eq("tenant_id", tenantId);
                        queryWrapper2.eq("branch_code", branchCode);
                        queryWrapper2.inSql("router_id", "select id from base_router where is_active='1' and router_no ='" + drawnos.split(",")[i] + "' and branch_code='" + branchCode + "'");

                        List<Sequence> sequences = sequenceService.list(queryWrapper2);
                        if (sequences.size() >= 1) {
                            step += sequences.get(0).getRouterId() + sequences.get(0).getId() + qualityList.get(j).getOptName();
                            RouterCheck routerCheck = new RouterCheck();
                            routerCheck.setCreateBy(user.getUsername());
                            routerCheck.setRouterId(sequences.get(0).getRouterId());
                            routerCheck.setSequenceId(sequences.get(0).getId());
                            routerCheck.setCreateTime(new Date());
                            routerCheck.setModifyBy(user.getUsername());
                            routerCheck.setModifyTime(new Date());
                            routerCheck.setTenantId(tenantId);
                            routerCheck.setBranchCode(branchCode);
                            routerCheck.setType("质量资料");
                            routerCheck.setName(qualityList.get(j).getName());
                            routerCheck.setDrawingNo(qualityList.get(j).getRouterNo());
                            routerCheck.setCheckOrder(check_order);
                            routerCheck.setIsEmpty(1);
                            routerCheck.setStatus("1");
                            routerCheck.setPropertyObjectname(qualityList.get(j).getName());
                            check_order++;
                            this.save(routerCheck);
                        }
                    }
                }
                step += "资料类型列表保存";
            } catch (Exception ex) {
                step += ex.getMessage();
            }

            return CommonResult.success(step, "成功");
        } catch (Exception e) {
            return CommonResult.failed("失败:" + step + e.getMessage());
        }
    }

    /**
     * 校验导入的数据
     * @param drawnos
     * @param checkList
     * @return
     */
    private String checkExportInfo(String drawnos,List<RouterCheckDto> checkList,String branchCode){
        //提示信息
        StringBuilder info = new StringBuilder();
        //空值校验
        List<RouterCheckDto> nullList = checkList.stream().filter(
                item -> StringUtils.isNullOrEmpty(item.getOptNo()) || StringUtils.isNullOrEmpty(item.getOptName())
        ).collect(Collectors.toList());
        if(nullList.size()>0){
            info.append("工序号或工序名称不能为空</br>");
        }
        //工序号校验
        List<RouterCheckDto> orderList = checkList.stream().filter(
                item -> !NumberUtil.isNumber(item.getOrderNo())
        ).collect(Collectors.toList());
        if(orderList.size()>0){
            info.append("序号必须为数字</br>");
        }

        //图号集合
        List<String> drawnoList = Arrays.asList(drawnos.split(","));
        if(drawnoList.size()>0){
            //图号查询的工艺
            List<Router> router = routerService.list(new QueryWrapper<Router>().in("router_no", drawnoList));
            //已有的图号
            List<String> routerNos = router.stream().map(Router::getRouterNo).collect(Collectors.toList());
            //1.图号校验
            for (String drawno : drawnoList) {
                boolean exist = routerNos.contains(drawno);
                if(!exist){
                    info.append("(图号："+drawno+") 未维护工艺管理数据</br>");
                }
            }
            for (RouterCheckDto routerCheckDto : checkList) {
                //2.工序校验
                if(routerNos.contains(routerCheckDto.getRouterNo()) && !StringUtils.isNullOrEmpty(routerCheckDto.getOptNo()) && !StringUtils.isNullOrEmpty(routerCheckDto.getOptName())){
                    QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<Sequence>();
                    queryWrapper2.eq("opt_name", routerCheckDto.getOptName().trim());
                    queryWrapper2.eq("op_no", routerCheckDto.getOptNo().trim());
                    queryWrapper2.eq("branch_code", branchCode);
                    queryWrapper2.inSql("router_id", "select id from base_router where is_active='1' and router_no ='" + routerCheckDto.getRouterNo() + "' and branch_code='" + branchCode + "'");
                    List<Sequence> sequences = sequenceService.list(queryWrapper2);
                    if(sequences.size()==0){
                        info.append("图号："+routerCheckDto.getRouterNo()+"的工艺，未维护工序（序号为:"+routerCheckDto.getOptNo()+"工序名为："+routerCheckDto.getOptName()+"）"+"的工序数据<br>");
                    }
                }
            }
        }
        //3.规则校验
        for (RouterCheckDto routerCheckDto : checkList) {
            checkPropertySymbol(info, routerCheckDto);
        }

        return String.valueOf(info);
    }

    /**
     * 规则校验
     * @param info
     * @param routerCheckDto
     */
    private void checkPropertySymbol(StringBuilder info, RouterCheckDto routerCheckDto) {
        //3.规则校验
        String propertySymbol = routerCheckDto.getPropertySymbol();
        String propertyDefaultvalue = routerCheckDto.getPropertyDefaultvalue();
        String propertyLowerlimit = routerCheckDto.getPropertyLowerlimit();
        String propertyUplimit = routerCheckDto.getPropertyUplimit();
        String msg = "";
        if(!NumberUtil.isNumber(StringUtils.isNullOrEmpty(propertyLowerlimit)?"0":propertyLowerlimit)
                || !NumberUtil.isNumber(StringUtils.isNullOrEmpty(propertyUplimit)?"0":propertyUplimit)){
            msg = "最大值最小值必须为数字</br>";
        }else{
            if (
                    ("等于设定值".equals(propertySymbol) ||
                            "不等于设定值".equals(propertySymbol)) &&
                            StringUtils.isNullOrEmpty(propertyDefaultvalue)
            ) {
                msg = "必须填写默认设定值</br>";
            }
            if (
                    ("结果 > 最小值".equals(propertySymbol) ||
                            "结果 >= 最小值".equals(propertySymbol)) &&
                            StringUtils.isNullOrEmpty(propertyLowerlimit)
            ) {
                msg = "必须填写最小值</br>";
            }
            if (
                    ("结果 < 最大值".equals(propertySymbol) ||
                            "结果 <= 最大值".equals(propertySymbol)) &&
                            StringUtils.isNullOrEmpty(routerCheckDto.getPropertyUplimit())
            ) {
                msg = "必须填写最大值</br>";
            }
            if (
                    ("最小值 <= 结果 <= 最大值".equals(propertySymbol) ||
                            "最小值 < 结果 < 最大值".equals(propertySymbol) ||
                            "最小值 < 结果 <= 最大值".equals(propertySymbol) ||
                            "最小值 <= 结果 < 最大值".equals(propertySymbol)) &&
                            (StringUtils.isNullOrEmpty(propertyUplimit) || StringUtils.isNullOrEmpty(propertyLowerlimit))
            ) {
                msg = "最大值和最小值必须填写</br>";
            } else{
                if (
                        ("最小值 <= 结果 <= 最大值".equals(propertySymbol) ||
                                "最小值 < 结果 <= 最大值".equals(propertySymbol) ||
                                "最小值 <= 结果 < 最大值".equals(propertySymbol)) &&
                                Double.parseDouble(propertyLowerlimit) > Double.parseDouble(propertyUplimit)
                ) {
                    msg = "最大值必须大于等于最小值</br>";
                }
                if (
                        "最小值 < 结果 < 最大值".equals(propertySymbol) &&
                                Double.parseDouble(propertyLowerlimit) > Double.parseDouble(propertyUplimit)
                ) {
                    msg = "最大值必须大于最小值</br>";
                }
            }
        }
        if(!StringUtils.isNullOrEmpty(msg)){
            info.append("(图号："+routerCheckDto.getRouterNo()+"，工序号："+routerCheckDto.getOptNo()+"，工序名称："+routerCheckDto.getOptName()+") "+msg);
        }
    }

}
