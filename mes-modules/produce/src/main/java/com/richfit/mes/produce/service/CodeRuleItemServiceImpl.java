package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CodeRuleItemMapper;
import com.richfit.mes.produce.entity.CodeRuleItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 编码规则 服务实现类
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Service
@Slf4j
public class CodeRuleItemServiceImpl extends ServiceImpl<CodeRuleItemMapper, CodeRuleItem> implements CodeRuleItemService {

    public static final String IS_COMPANY_RULE = "0";
    public static final String IS_SITE_RULE = "1";
    public static final String IS_ROLE_RULE = "2";

    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private CodeRuleItemService codeRuleItemService;

    @Override
    public CommonResult<String> importCodeRuleByExcel(MultipartFile file) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        String tenantId = currentUser.getTenantId();
        //封装编码规则实体类
        String[] codeRuleFieldNames = {"ruleNo", "name", "code", "status", "level", "branchCode", "isFixed", "maxLength", "roleId"};
        String[] codeRuleItemFiledNames = {"ruleNo", "orderNo", "type", "remark", "width", "constant", "dateFormat", "snStep", "prefixChar", "suffixChar", "snDefault", "snResetDependency", "snCurrentValue", "maxLength", "compDirect", "compChar", "checkType", "checkRegex"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //模板校验
            //将导入的excel数据生成实体类list
            List<CodeRuleItemDto> codeRuleItemDtoList = ExcelUtils.importExcel(excelFile, CodeRuleItemDto.class, codeRuleItemFiledNames, 1, 0, 1, tempName.toString());
            List<CodeRule> codeRuleList = ExcelUtils.importExcel(excelFile, CodeRule.class, codeRuleFieldNames, 1, 0, 0, tempName.toString());
            if (CollectionUtils.isEmpty(codeRuleList)) {
                return CommonResult.failed("未检测到编码规则！");
            }
            //判断该编码是否存在
            boolean exist = false;
            List<String> existRuleNoList = new ArrayList<>();
            for (CodeRule codeRule : codeRuleList) {
                if (CheckCodeExist(codeRule)) {
                    exist = true;
                    existRuleNoList.add(codeRule.getRuleNo());
                }
                if (StringUtils.isNullOrEmpty(codeRule.getMaxLength())) {
                    codeRule.setMaxLength("10");
                }
                if (StringUtils.isNullOrEmpty(codeRule.getName())) {
                    return CommonResult.failed("名称不能为空！");
                }
                if (!IS_ROLE_RULE.equals(codeRule.getLevel())) {
                    codeRule.setRoleId(null);
                }
                if (!IS_SITE_RULE.equals(codeRule.getLevel())) {
                    codeRule.setBranchCode(null);
                }
                codeRule.setTenantId(tenantId);
            }
            if (exist) {
                return CommonResult.failed("编码已存在，编码序号：" + existRuleNoList);
            }
            //保存codeRule 然后根据codeRuleId保存codeRuleItem
            codeRuleService.saveBatch(codeRuleList);
            if (!CollectionUtils.isEmpty(codeRuleItemDtoList)) {
                List<CodeRuleItem> codeRuleItemList = new ArrayList<>();
                for (CodeRuleItemDto codeRuleItemDto : codeRuleItemDtoList) {
                    CodeRuleItem codeRuleItem = new CodeRuleItem();
                    BeanUtils.copyProperties(codeRuleItemDto, codeRuleItem);
                    codeRuleItemList.add(codeRuleItem);
                }
                Map<String, List<CodeRuleItem>> codeRuleItemMap = codeRuleItemList.stream().collect(Collectors.groupingBy(CodeRuleItem::getRuleNo));
                for (CodeRule codeRule : codeRuleList) {
                    String codeRuleId = codeRule.getId();
                    if (null != codeRuleItemMap.get(codeRule.getRuleNo())) {
                        List<CodeRuleItem> list = codeRuleItemMap.get(codeRule.getRuleNo());
                        for (CodeRuleItem codeRuleItem : list) {
                            codeRuleItem.setCodeRuleId(codeRuleId);
                            codeRuleItem.setTenantId(tenantId);
                            codeRuleItem.setBranchCode(codeRule.getBranchCode());
                        }
                        this.saveBatch(codeRuleItemMap.get(codeRule.getRuleNo()));
                    }
                }
            }
        } catch (IOException e) {
            log.error("解析excel失败:", e);
            FileUtils.delete(excelFile);
        }
        //删除保存的文件
        FileUtils.delete(excelFile);
        return CommonResult.success("excel导入成功！");
    }

    private boolean CheckCodeExist(CodeRule entity) {
        String errorStr = "";
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", entity.getCode());
        queryWrapper.eq("tenant_id", entity.getTenantId());
        List<CodeRule> codeRules = codeRuleService.list(queryWrapper);
        //修改操作判断时 过滤掉自己
        if (!StringUtils.isNullOrEmpty(entity.getId())) {
            codeRules = codeRules.stream().filter(item -> !item.getId().equals(entity.getId())).collect(Collectors.toList());
        }
        //公司级别的校验
        if (IS_COMPANY_RULE.equals(entity.getLevel())) {
            if (codeRules.size() > 0) {
                if (IS_COMPANY_RULE.equals(codeRules.get(0).getLevel())) {
                    errorStr = "公司级编码" + entity.getCode() + "已存在，切勿重复添加";
                } else if (IS_SITE_RULE.equals(codeRules.get(0).getLevel())) {
                    errorStr = "车间级编码" + entity.getCode() + "已存在，无法再添加公司级编码";
                } else {
                    errorStr = "权限级编码" + entity.getCode() + "已存在，无法再添加公司级编码";
                }
            }
        } else if (IS_SITE_RULE.equals(entity.getLevel())) {
            //公司级别
            List<CodeRule> companyRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_COMPANY_RULE)).collect(Collectors.toList());
            //车间级别
            List<CodeRule> siteRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_SITE_RULE) && item.getBranchCode().equals(entity.getBranchCode())).collect(Collectors.toList());
            //角色级别
            List<CodeRule> roleRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_ROLE_RULE)).collect(Collectors.toList());
            if (companyRules.size() > 0) {
                errorStr = "公司级编码" + entity.getCode() + "已存在，无法再添加车间级";
            }
            if (siteRules.size() > 0) {
                errorStr = "已选车间已存在编码" + entity.getCode() + "，无法再添加";
            }
            if (roleRules.size() > 0) {
                errorStr = "权限级编码" + entity.getCode() + "已存在，无法再添加车间级";
            }
        } else {
            //公司级别
            List<CodeRule> companyRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_COMPANY_RULE)).collect(Collectors.toList());
            //车间级别
            List<CodeRule> siteRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_SITE_RULE)).collect(Collectors.toList());
            //角色级别
            List<CodeRule> roleRules = codeRules.stream().filter(item -> item.getLevel().equals(IS_ROLE_RULE)).collect(Collectors.toList());
            if (companyRules.size() > 0) {
                errorStr = "公司级编码" + entity.getCode() + "已存在，无法再添加权限级";
            }
            if (siteRules.size() > 0) {
                errorStr = "车间级编码" + entity.getCode() + "已存在，无法再添加权限级";
            }
            if (roleRules.size() > 0) {
                List<String> roleIdList = new ArrayList<>(Arrays.asList(entity.getRoleId().split(",")));
                for (CodeRule roleRule : roleRules) {
                    List<String> roleIdList1 = new ArrayList<>(Arrays.asList(roleRule.getRoleId().split(",")));
                    roleIdList.retainAll(roleIdList1);
                    if (roleIdList.size() > 0) {
                        errorStr = "角色" + org.apache.commons.lang.StringUtils.join(roleIdList, ",") + "已被赋予编码" + entity.getCode() + "，无法再为此角色分配相同编码的权限";
                    }
                }

            }
        }
        if (!StringUtils.isNullOrEmpty(errorStr)) {
            throw new GlobalException(errorStr, ResultCode.FAILED);
        }
        return false;
    }
}
