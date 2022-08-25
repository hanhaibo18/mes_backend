package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;

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
     * 功能描述: 质量检验卡更新
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    void updateProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard);
}
