package com.richfit.mes.sys.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.QualityInspectionRulesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: QualityInspectionRulesService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月28日 10:24:00
 */

@Slf4j
@Service
public class QualityInspectionRulesServiceImpl extends ServiceImpl<QualityInspectionRulesMapper, QualityInspectionRules> implements QualityInspectionRulesService {

    @Override
    public boolean saveQualityInspectionRules(QualityInspectionRules qualityInspectionRules) {
        qualityInspectionRules.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return this.save(qualityInspectionRules);
    }

    @Override
    public boolean updateQualityInspectionRules(QualityInspectionRules qualityInspectionRules) {
        return this.updateById(qualityInspectionRules);
    }

    @Override
    public boolean deleteQualityInspectionRules(String id) {
        return removeById(id);
    }

    @Override
    public IPage<QualityInspectionRules> queryQualityInspectionRulesPage(String stateName, long page, long limit, String order, String orderCol) {
        QueryWrapper<QualityInspectionRules> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state_name", stateName);
        queryWrapper.orderByDesc("branch_code");
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("order_no");
        }
        return this.page(new Page<>(page, limit), queryWrapper);
    }

    @Override
    public void exportExcel(HttpServletResponse rsp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "质检规则" + format.format(new Date()) + ".xlsx";

            String[] columnHeaders = {"状态名称", "是否计算工时", "是否移到下一步骤", "部门", "最后更新人", "最后更新时间"};

            String[] fieldNames = {"stateName", "isGiveTime", "isNext", "branchCode", "modifyBy", "modifyTime"};
            //export
            ExcelUtils.exportExcel(fileName, this.list(), columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}