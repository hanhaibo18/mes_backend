package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.DisqualificationFinalResult;
import com.richfit.mes.produce.dao.quality.DisqualificationFinalResultMapper;
import com.richfit.mes.produce.entity.quality.DisqualificationFinalResultDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @ClassName: DisqualificationFinalResultServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 不合格品最终结果ServiceImpl
 * @CreateTime: 2022年11月03日 11:07:00
 */
@Service
public class DisqualificationFinalResultServiceImpl extends ServiceImpl<DisqualificationFinalResultMapper, DisqualificationFinalResult> implements DisqualificationFinalResultService {

    @Override
    public Boolean saveDisqualificationFinalResult(DisqualificationFinalResultDto disqualificationFinalResultDto) {
        DisqualificationFinalResult disqualificationFinalResult = new DisqualificationFinalResult();
        BeanUtils.copyProperties(disqualificationFinalResultDto, disqualificationFinalResult);
        disqualificationFinalResult.setAcceptDeviationNo(String.join(",", disqualificationFinalResultDto.getAcceptDeviationNoList()));
        disqualificationFinalResult.setRepairNo(String.join(",", disqualificationFinalResultDto.getRepairNoList()));
        disqualificationFinalResult.setScrapNo(String.join(",", disqualificationFinalResultDto.getScrapNoList()));
        return this.save(disqualificationFinalResult);
    }
}
