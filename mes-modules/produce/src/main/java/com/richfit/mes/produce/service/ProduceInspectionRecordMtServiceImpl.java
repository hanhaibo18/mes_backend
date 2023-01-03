package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordMt;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ProduceInspectionRecordMtMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceInspectionRecordMtServiceImpl extends ServiceImpl<ProduceInspectionRecordMtMapper, ProduceInspectionRecordMt> implements ProduceInspectionRecordMtService,RecordStragegy{


    @Autowired
    private SystemServiceClient systemServiceClient;

    /**
     * 根据ids获取记录
     * @return
     */
    @Override
    public List<ProduceInspectionRecordMt> queryListByIds(List<String> ids){
        List<ProduceInspectionRecordMt> produceInspectionRecordMts = this.list(new QueryWrapper<ProduceInspectionRecordMt>().in("id", ids));
        //探伤记录map
        Map<String, ProduceInspectionRecordMt> recordMap = produceInspectionRecordMts.stream().collect(Collectors.toMap(ProduceInspectionRecordMt::getId, Function.identity()));
        /*//缺陷记录 key->探伤记录id
        Map<String, List<ProduceDefectsInfo>> defectsInfoMap = produceDefectsInfoService.getMapByRecordIds(ids);

        recordMap.forEach((key,value)->{
            value.setDefectsInfoList(defectsInfoMap.get(key));
        });*/
        return new ArrayList<>(recordMap.values());
    }

    @Override
    public Boolean updateAuditInfo(String id, String isAudit, String auditRemark) {
        UpdateWrapper<ProduceInspectionRecordMt> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_audit", isAudit).set("audit_remark", auditRemark).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
        return this.update(updateWrapper);
    }

    @Override
    public String getType() {
        return "mt";
    }
}
