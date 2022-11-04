package com.richfit.mes.produce.service.quality;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.DisqualificationFinalResult;
import com.richfit.mes.common.model.produce.DisqualificationUserOpinion;
import com.richfit.mes.produce.dao.quality.DisqualificationFinalResultMapper;
import com.richfit.mes.produce.entity.quality.DisqualificationFinalResultDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: DisqualificationFinalResultServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 不合格品最终结果ServiceImpl
 * @CreateTime: 2022年11月03日 11:07:00
 */
@Service
public class DisqualificationFinalResultServiceImpl extends ServiceImpl<DisqualificationFinalResultMapper, DisqualificationFinalResult> implements DisqualificationFinalResultService {

    @Resource
    private DisqualificationUserOpinionService userOpinionService;

    @Override
    public Boolean saveDisqualificationFinalResult(DisqualificationFinalResultDto disqualificationFinalResultDto) {
        DisqualificationFinalResult disqualificationFinalResult = new DisqualificationFinalResult();
        BeanUtils.copyProperties(disqualificationFinalResultDto, disqualificationFinalResult);
        if (CollectionUtils.isNotEmpty(disqualificationFinalResultDto.getAcceptDeviationNoList())) {
            disqualificationFinalResult.setAcceptDeviationNo(String.join(",", disqualificationFinalResultDto.getAcceptDeviationNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationFinalResultDto.getRepairNoList())) {
            disqualificationFinalResult.setRepairNo(String.join(",", disqualificationFinalResultDto.getRepairNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationFinalResultDto.getScrapNoList())) {
            disqualificationFinalResult.setScrapNo(String.join(",", disqualificationFinalResultDto.getScrapNoList()));
        }
        UpdateWrapper<DisqualificationUserOpinion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", disqualificationFinalResultDto.getOpinionId());
        //提交最终审核给意见添加空字符串,不为NULL,标识已处理
        updateWrapper.set("opinion", "");
        userOpinionService.update(updateWrapper);
        return this.save(disqualificationFinalResult);
    }
}
