package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.common.model.sys.Attachment;

import java.util.List;

/**
 * @author 马峰
 * @Description 质检结果
 */
public interface TrackCheckDetailService extends IService<TrackCheckDetail> {

    /**
     * 描述: 根据工序id查询质检的工序项目列表
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    List<TrackCheckDetail> selectByTiId(String tiId);

    /**
     * 功能描述: 查询详情
     *
     * @param tiId 工序id
     * @Author: xinYu.hou
     * @Date: 2022/6/30 17:43
     * @return: TrackCheck
     **/
    TrackCheck queryQualityTestingResult(String tiId);

    /**
     * 功能描述: 获取质检文件列表
     *
     * @param tiId
     * @Author: xinYu.hou
     * @Date: 2022/7/1 14:43
     * @return: List<Attachment>
     **/
    List<Attachment> getAttachmentListByTiId(String tiId);
}
