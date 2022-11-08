package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.DisqualificationFinalResult;
import com.richfit.mes.produce.entity.quality.DisqualificationFinalResultDto;

/**
 * @ClassName: DisqualificationFinalResultService.java
 * @Author: Hou XinYu
 * @Description: 不合格品最终结果Service
 * @CreateTime: 2022年11月03日 11:07:00
 */
public interface DisqualificationFinalResultService extends IService<DisqualificationFinalResult> {

    /**
     * 功能描述: 保存最终结果
     *
     * @param disqualificationFinalResult
     * @Author: xinYu.hou
     * @Date: 2022/11/3 18:03
     * @return: Boolean
     **/
    Boolean saveDisqualificationFinalResult(DisqualificationFinalResultDto disqualificationFinalResult);

}
