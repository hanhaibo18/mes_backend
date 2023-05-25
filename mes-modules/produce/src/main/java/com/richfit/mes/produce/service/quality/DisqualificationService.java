package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.entity.quality.DisqualificationDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.entity.quality.QueryCheckDto;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DisqualificationService.java
 * @Author: Hou XinYu
 * @Description: 不合格品
 * @CreateTime: 2022年09月29日 15:14:00
 */
public interface DisqualificationService extends IService<Disqualification> {

    /**
     * 功能描述: 查询申请人创建的申请单
     *
     * @param queryInspectorDto
     * @Author: xinYu.hou
     * @Date: 2022/9/29 16:14
     * @return: IPage<DisqualificationService>
     **/
    IPage<Disqualification> queryInspector(QueryInspectorDto queryInspectorDto);

    /**
     * 功能描述: 查询不合格质检信息分页接口
     *
     * @param queryCheckDto
     * @Author: xinYu.hou
     * @Date: 2022/10/17 10:44
     * @return: IPage<Disqualification>
     **/
    IPage<Disqualification> queryCheck(QueryCheckDto queryCheckDto);

    /**
     * 功能描述: 查询处理单位列表
     *
     * @param queryCheckDto
     * @Author: xinYu.hou
     * @Date: 2023/1/16 3:40
     * @return: IPage<Disqualification>
     **/
    IPage<Disqualification> queryDealWith(QueryCheckDto queryCheckDto);

    /**
     * 功能描述: 责任裁决列表
     *
     * @param queryCheckDto
     * @Author: xinYu.hou
     * @Date: 2023/1/16 4:03
     * @return: IPage<Disqualification>
     **/
    IPage<Disqualification> queryResponsibility(QueryCheckDto queryCheckDto);

    /**
     * 功能描述: 技术裁决列表
     *
     * @param queryCheckDto
     * @Author: xinYu.hou
     * @Date: 2023/1/16 4:03
     * @return: IPage<Disqualification>
     **/
    IPage<Disqualification> queryTechnology(QueryCheckDto queryCheckDto);

    /**
     * 功能描述: 创建申请单
     *
     * @param disqualification
     * @Author: xinYu.hou
     * @Date: 2022/9/30 9:29
     * @return: Boolean
     **/
    Boolean saveOrUpdateDisqualification(DisqualificationDto disqualification);


    /**
     * 功能描述: 关单
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/9/30 10:24
     * @return: Boolean
     **/
    Boolean updateIsIssue(String id);

    /**
     * 功能描述: 查询质量检测部质检人员
     *
     * @Author: xinYu.hou
     * @Date: 2022/10/14 15:12
     * @return: List<TenantUserVo>
     **/
    List<TenantUserVo> queryUser();


    /**
     * 功能描述: 查询申请单信息
     *
     * @param branchCode
     * @param disqualificationId
     * @Author: xinYu.hou
     * @Date: 2022/10/24 16:54
     * @return: DisqualificationItemVo
     **/
    DisqualificationItemVo inquiryRequestFormNew(String disqualificationId, String branchCode);

    /**
     * 功能描述: 查询申请单信息
     *
     * @param tiId
     * @param branchCode
     * @param disqualificationId
     * @Author: xinYu.hou
     * @Date: 2022/10/24 16:54
     * @return: DisqualificationItemVo
     **/
    DisqualificationItemVo inquiryRequestForm(String tiId, String branchCode, String disqualificationId);

    /**
     * 功能描述:根据跟单Id查询分流表中产品编号
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @Date: 2022/11/3 15:53
     * @return: List<Map < String, String>>
     **/
    List<Map<String, String>> queryProductNoList(String trackHeadId);

//    /**
//     * 功能描述: 查询质量管理列表,根据当前登录用处查询指定质检人员
//     *
//     * @param queryInspectorDto
//     * @Author: xinYu.hou
//     * @Date: 2022/9/29 16:14
//     * @return: IPage<DisqualificationService>
//     **/
//    IPage<Disqualification> queryQuality(QueryInspectorDto queryInspectorDto);

    /**
     * 功能描述: 跟单回滚
     *
     * @param id
     * @param type
     * @Author: xinYu.hou
     * @Date: 2023/1/18 15:20
     * @return: Boolean
     **/
    Boolean rollBack(String id, Integer type);

    /**
     * 功能描述:
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2023/1/30 9:46
     * @return: Boolean
     **/
    Boolean rollBackAll(String id);

    /**
     * 功能描述: 退回
     *
     * @param id
     * @param type
     * @Author: xinYu.hou
     * @Date: 2023/2/27 9:57
     * @return: Boolean
     **/
    Boolean sendBack(String id, Integer type);

    String deleteById(String disqualificationId);

    IPage<Disqualification> queryInspectorByCompany(QueryInspectorDto queryInspectorDto);

    /**
     * 功能描述: 查询上一次填写不合格申请单数据
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2023/3/29 14:39
     * @return: DisqualificationItemVo
     **/
    DisqualificationItemVo queryLastTimeDataByCreateBy(String branchCode);

    /**
     * 不合格导出
     * @param rsp
     * @param queryInspectorDto
     */
    void exportExcel(HttpServletResponse rsp, QueryInspectorDto queryInspectorDto);
}
