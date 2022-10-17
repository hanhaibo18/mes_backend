package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.entity.quality.QueryCheckDto;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;
import com.richfit.mes.produce.entity.quality.SignedRecordsVo;

import java.util.List;

/**
 * @ClassName: DisqualificationService.java
 * @Author: Hou XinYu
 * @Description: 不合格品
 * @CreateTime: 2022年09月29日 15:14:00
 */
public interface DisqualificationService extends IService<Disqualification> {

    /**
     * 功能描述: 查询质检员查看列表
     *
     * @param queryInspectorDto
     * @Author: xinYu.hou
     * @Date: 2022/9/29 16:14
     * @return: IPage<DisqualificationService>
     **/
    IPage<Disqualification> queryInspector(QueryInspectorDto queryInspectorDto);

    /**
     * 功能描述: 创建申请单
     *
     * @param disqualification
     * @Author: xinYu.hou
     * @Date: 2022/9/30 9:29
     * @return: Boolean
     **/
    Boolean saveDisqualification(Disqualification disqualification);

    /**
     * 功能描述: 修改申请单
     *
     * @param disqualification
     * @Author: xinYu.hou
     * @Date: 2022/9/30 9:30
     * @return: Boolean
     **/
    Boolean updateDisqualification(Disqualification disqualification);

    /**
     * 功能描述: 修改不合格申请单
     *
     * @param id
     * @param state
     * @Author: xinYu.hou
     * @Date: 2022/9/30 10:24
     * @return: Boolean
     **/
    Boolean updateIsIssue(String id, String state);

    /**
     * 功能描述: 查询质量检测部质检人员
     *
     * @Author: xinYu.hou
     * @Date: 2022/10/14 15:12
     * @return: List<TenantUserVo>
     **/
    List<TenantUserVo> queryUser();

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
     * 功能描述: 查询签核记录列表
     *
     * @param disqualificationId
     * @Author: xinYu.hou
     * @Date: 2022/10/17 17:04
     * @return: List<SignedRecordsVo>
     **/
    List<SignedRecordsVo> querySignedRecordsList(String disqualificationId);
}
