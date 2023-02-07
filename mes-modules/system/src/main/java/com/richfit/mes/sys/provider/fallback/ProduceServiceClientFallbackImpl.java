package com.richfit.mes.sys.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.CheckAttachment;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.sys.provider.ProduceServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/6/30 15:32
 */
@Component
@Slf4j
public class ProduceServiceClientFallbackImpl implements ProduceServiceClient {

    @Override
    public CommonResult<Boolean> saveCheckFile(CheckAttachment checkAttachment) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Boolean> batchSaveCodeRule(List<CodeRule> rules) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Boolean> batchSaveCodeRuleItem(List<CodeRuleItem> items) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Boolean> batchSaveTrackHeadTemplate(List<ProduceTrackHeadTemplate> templates) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Boolean> countQueryRules(String rulesId) {
        return CommonResult.success(null);
    }

}
