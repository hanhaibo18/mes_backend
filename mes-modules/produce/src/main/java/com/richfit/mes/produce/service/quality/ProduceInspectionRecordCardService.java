package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCardContent;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
public interface ProduceInspectionRecordCardService extends IService<ProduceInspectionRecordCard> {

    /**
     * 功能描述: 质量检验卡保存
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    void saveProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard);


    /**
     * 功能描述: 质量检验卡质检明细信息更新
     *
     * @param produceInspectionRecordCardContent 质量检验卡明细信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    void updateTrackCheckDetail(ProduceInspectionRecordCardContent produceInspectionRecordCardContent);

    /**
     * 功能描述: 质量检验卡更新
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    void updateProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard);

    /**
     * 功能描述: 质量检验卡查询
     *
     * @param flowId 质量检测卡id/flowID
     * @return ProduceInspectionRecordCard 质量检测卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    ProduceInspectionRecordCard selectProduceInspectionRecordCard(String flowId) throws Exception;


    /**
     * 功能描述: 工序质检信息查询
     *
     * @param flowId 质量检测卡id/flowID，如果itemId有值flowId可以为null
     * @param itemId 工序id，null为查询全部工序质检信息
     * @return ProduceInspectionRecordCard 质量检测卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    List<ProduceInspectionRecordCardContent> selectItemCheckList(String flowId, String itemId) throws Exception;
}
