package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.QualityInspectionRules;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName: QualityInspectionRulesService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月28日 10:24:00
 */
public interface QualityInspectionRulesService extends IService<QualityInspectionRules> {

    /**
     * 功能描述: 创建质检规则
     *
     * @param qualityInspectionRules
     * @Author: xinYu.hou
     * @Date: 2022/7/28 10:28
     * @return: boolean
     **/
    CommonResult<Boolean> saveQualityInspectionRules(QualityInspectionRules qualityInspectionRules);

    /**
     * 功能描述: 修改质检规则
     *
     * @param qualityInspectionRules
     * @Author: xinYu.hou
     * @Date: 2022/7/28 10:28
     * @return: boolean
     **/
    CommonResult<Boolean> updateQualityInspectionRules(QualityInspectionRules qualityInspectionRules);

    /**
     * 功能描述: 删除质检规则
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/28 10:28
     * @return: boolean
     **/
    boolean deleteQualityInspectionRules(String id);

    /**
     * 功能描述:
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/7/28 10:29
     * @return: IPage<QualityInspectionRules>
     **/
    IPage<QualityInspectionRules> queryQualityInspectionRulesPage(String stateName, long page, long limit, String order, String orderCol);

    /**
     * 功能描述: 导出到Excel
     *
     * @param rsp
     * @Author: xinYu.hou
     * @Date: 2022/7/28 11:39
     * @return: void
     **/
    void exportExcel(HttpServletResponse rsp);

    /**
     * 功能描述: 根据车间查询规则列表
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/8/1 17:17
     * @return: List<QualityInspectionRules>
     **/
    List<QualityInspectionRules> queryQualityInspectionRulesList(String branchCode);

    /**
     * 功能描述: 根据Id查询质检规则
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/8/4 10:23
     * @return: QualityInspectionRules
     **/
    QualityInspectionRules queryQualityInspectionRulesById(String id);
}
