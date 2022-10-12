package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;

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
}
