package com.richfit.mes.sys.service;

import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.sys.*;
import com.richfit.mes.sys.dao.TenantMapper;
import com.richfit.mes.sys.provider.BaseServiceClient;
import com.richfit.mes.sys.provider.ProduceServiceClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 租户服务
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public JsonNode getAdditionalInfo(String tenantId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = tenantMapper.getAdditionalInfo(tenantId);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            return objectMapper.readTree(s);
        }
    }

    @Override
    public CommonResult saveAdditionalInfo(JsonNode addInfo, String tenantId) {
        return CommonResult.success(tenantMapper.saveAdditionalInfo(addInfo.toString(), tenantId));
    }

    @Override
    public Boolean addTenant(Tenant tenant) {

        this.save(tenant);

        //默认创建一个该租户下的 租户管理员角色
        roleService.addTenantAdminRole(tenant.getId());

        return true;
    }

    @Autowired
    private ItemClassTempService itemClassTempService;
    @Autowired
    private ItemParamTempService itemParamTempService;
    @Autowired
    private CodeRuleTempService codeRuleTempService;
    @Autowired
    private CodeRuleItemTempService codeRuleItemTempService;
    @Autowired
    private RoleTempService roleTempService;
    @Autowired
    private TrackHeadTempService trackHeadTempService;

    @Autowired
    private ItemClassService itemClassService;
    @Autowired
    private ItemParamService itemParamService;
    @Autowired
    private ProduceServiceClient produceServiceClient;
    @Autowired
    private BaseServiceClient baseServiceClient;

    @Override
    @Transactional
    public String initData(String tenantId) {
        Tenant tenant = this.getById(tenantId);
        Boolean result = false;
        if (tenant != null && !StringUtils.isEmpty(tenant.getId())) {
            // 初始化组织机构
            CommonResult<Branch> commonResult =
                    baseServiceClient.initBranch(tenantId, tenant.getTenantCode(), tenant.getTenantName());
            Branch branch = new Branch();
            if (commonResult != null) {
                branch = commonResult.getData();
            }

            // 开始初始化角色数据
            // 判断是否初始话过角色
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("tenant_id", tenantId);
            //role_code 不等于 role_tenant_admin  去掉管理员的租户代码
            roleQueryWrapper.ne("role_code", "role_tenant_admin");
            List<Role> roleList = roleService.list(roleQueryWrapper);
            //当没有找到角色数据的时候才能进行角色初始化
            if (roleList == null || roleList.size() == 0) {
                List<RoleTemp> roleTemps = roleTempService.list();
                List<Role> roles = new ArrayList<>();
                for (RoleTemp temp : roleTemps) {
                    Role role = new Role();
                    role.setRoleName(temp.getRoleName());
                    role.setRoleCode(tenant.getTenantCode() + "_" + temp.getRoleCode());
                    role.setRoleDesc(temp.getRoleDesc());
                    role.setEnabled(temp.getEnabled());
                    role.setRemark(temp.getRemark());
                    role.setRoleType(temp.getRoleType());
                    role.setTenantId(tenantId);
                    role.setOrgId(branch.getBranchCode());
                    roles.add(role);
                }
                if (roles.size() > 0) {
                    result = roleService.batchAdd(roles);
                    if (!result) {
                        return "初始化角色数据失败！";
                    }
                }
            }

            // 开始初始化字典数据
            // 删除原字典class数据
            QueryWrapper<ItemClass> itemClassQueryWrapper = new QueryWrapper<>();
            itemClassQueryWrapper.eq("tenant_id", tenantId);
            itemClassService.remove(itemClassQueryWrapper);
            // 删除原字典内容数据
            QueryWrapper<ItemParam> itemParamQueryWrapper = new QueryWrapper<>();
            itemParamQueryWrapper.eq("tenant_id", tenantId);
            itemParamService.remove(itemParamQueryWrapper);

            List<ItemClassTemp> itemClassTemps = itemClassTempService.list();
            List<ItemParamTemp> itemParamTemps = itemParamTempService.list();

            List<ItemClass> classes = new ArrayList<>();
            List<ItemParam> params = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(itemClassTemps)) {
                for (ItemClassTemp temp : itemClassTemps) {
                    ItemClass itemClass = new ItemClass();
                    itemClass.setTenantId(tenantId);
                    itemClass.setCode(temp.getCode());
                    itemClass.setName(temp.getName());
                    itemClass.setRemark(temp.getRemark());
                    itemClass.setId(UuidUtils.generateUuid().replace("-", ""));
                    if (CollectionUtils.isNotEmpty(itemParamTemps)) {
                        for (ItemParamTemp paramTemp : itemParamTemps) {
                            if (paramTemp.getClassId().equals(temp.getId())) {
                                ItemParam itemParam = new ItemParam();
                                itemParam.setClassId(itemClass.getId());
                                itemParam.setLabel(paramTemp.getLabel());
                                itemParam.setCode(paramTemp.getCode());
                                itemParam.setType(paramTemp.getType());
                                itemParam.setTenantId(tenantId);
                                itemParam.setRemark(paramTemp.getRemark());
                                itemParam.setOrderNum(paramTemp.getOrderNum());
                                itemParam.setUnit(paramTemp.getUnit());
                                params.add(itemParam);
                            }
                        }
                    }
                    classes.add(itemClass);
                }
            }

            if (classes.size() > 0) {
                result = itemClassService.saveBatch(classes);
                if (!result) {
                    return "初始化字典项失败！";
                }
            }

            if (params.size() > 0) {
                result = itemParamService.saveBatch(params);
                if (!result) {
                    return "初始化字典参数失败！";
                }
            }

            // 开始初始化编码规则
            List<CodeRuleTemp> codeRuleTemps = codeRuleTempService.list();
            List<CodeRuleItemTemp> codeRuleItemTemps = codeRuleItemTempService.list();

            List<CodeRule> codeRules = new ArrayList<>();
            List<CodeRuleItem> codeRuleItems = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(codeRuleTemps)) {
                for (CodeRuleTemp temp : codeRuleTemps) {
                    CodeRule entity = new CodeRule();
                    entity.setTenantId(tenantId);
                    entity.setCode(temp.getCode());
                    entity.setBranchCode(branch.getBranchCode());
                    entity.setName(temp.getName());
                    entity.setRemark(temp.getRemark());
                    entity.setCodeType(temp.getCodeType());
                    entity.setCurValue(temp.getCurValue());
                    entity.setIsFixed(temp.getIsFixed());
                    entity.setIsInner(temp.getIsInner());
                    entity.setMaxLength(temp.getMaxLength());
                    entity.setStatus(temp.getStatus());
                    entity.setId(UuidUtils.generateUuid().replace("-", ""));

                    if (CollectionUtils.isNotEmpty(codeRuleItemTemps)) {
                        for (CodeRuleItemTemp itemTemp : codeRuleItemTemps) {
                            if (itemTemp.getCodeRuleId().equals(temp.getId())) {
                                CodeRuleItem item = new CodeRuleItem();
                                item.setCodeRuleId(entity.getId());
                                item.setTenantId(tenantId);
                                item.setBranchCode(branch.getBranchCode());
                                item.setRemark(itemTemp.getRemark());
                                item.setOrderNo(itemTemp.getOrderNo());
                                item.setType(itemTemp.getType());
                                item.setCheckType(itemTemp.getCheckType());
                                item.setPrefixChar(itemTemp.getPrefixChar());
                                item.setSuffixChar(itemTemp.getSuffixChar());
                                item.setWidth(itemTemp.getWidth());
                                item.setMaxLength(itemTemp.getMaxLength());
                                item.setBussinessColumn(itemTemp.getBussinessColumn());
                                item.setCheckRegex(itemTemp.getCheckRegex());
                                item.setCompChar(itemTemp.getCompChar());
                                item.setCompDirect(itemTemp.getCompDirect());
                                item.setConstant(itemTemp.getConstant());
                                item.setDateFormat(itemTemp.getDateFormat());
                                item.setMaxLength(itemTemp.getMaxLength());
                                item.setSnCurrentDate(itemTemp.getSnCurrentDate());
                                item.setSnCurrentValue(itemTemp.getSnCurrentValue());
                                item.setSnDefault(itemTemp.getSnDefault());
                                item.setSnResetDependency(itemTemp.getSnResetDependency());
                                item.setSnStep(itemTemp.getSnStep());
                                codeRuleItems.add(item);
                            }
                        }
                    }
                    codeRules.add(entity);
                }
            }

            if (codeRules.size() > 0) {
                CommonResult<Boolean> pResult = produceServiceClient.batchSaveCodeRule(codeRules);
                if (pResult != null && pResult.getData() != null) {
                    result = pResult.getData();
                }
                if (!result) {
                    return "初始化编码规则失败！";
                }
            }

            if (codeRuleItems.size() > 0) {
                CommonResult<Boolean> pResult = produceServiceClient.batchSaveCodeRuleItem(codeRuleItems);
                if (pResult != null && pResult.getData() != null) {
                    result = pResult.getData();
                }
                if (!result) {
                    return "初始化编码规则项失败！";
                }
            }

            // 开始初始化跟单模板
            List<TrackHeadTemp> trackHeadTemps = trackHeadTempService.list();

            List<ProduceTrackHeadTemplate> templates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(trackHeadTemps)) {
                Branch finalBranch = branch;
                templates = trackHeadTemps.stream().map(temp -> {
                    ProduceTrackHeadTemplate entity = new ProduceTrackHeadTemplate();
                    entity.setTenantId(tenantId);
                    entity.setBranchCode(finalBranch.getBranchCode());
                    entity.setRemark(temp.getRemark());
                    entity.setFileId(temp.getFileId());
                    entity.setFilePath(temp.getFilePath());
                    entity.setSheet1(temp.getSheet1());
                    entity.setSheet2(temp.getSheet2());
                    entity.setSheet3(temp.getSheet3());
                    entity.setSheet4(temp.getSheet4());
                    entity.setTemplateName(temp.getTemplateName());
                    entity.setTemplateCode(temp.getTemplateCode());
                    return entity;
                }).collect(Collectors.toList());
            }

            if (templates.size() > 0) {
                CommonResult<Boolean> pResult = produceServiceClient.batchSaveTrackHeadTemplate(templates);
                if (pResult != null && pResult.getData() != null) {
                    result = pResult.getData();
                }
                if (!result) {
                    return "初始化跟单模板失败！";
                }
            }
        }

        return "操作成功！";
    }
}
