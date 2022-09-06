package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProbeInfo;

import java.util.List;
import java.util.Map;

/**
 * @author renzewen
 * @Description 探头信息
 */
public interface ProbeInfoService extends IService<ProbeInfo> {

    /**
     * 根据探伤ids查询每条探伤记录对应的探头信息
     * @param ids
     * @return
     */
    Map<String, List<ProbeInfo>> getProbeByRecordIds(List<String> ids);
}
