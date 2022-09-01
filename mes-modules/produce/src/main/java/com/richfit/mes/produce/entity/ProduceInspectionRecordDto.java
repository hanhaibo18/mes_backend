package com.richfit.mes.produce.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.model.produce.ProduceDefectsInfo;
import lombok.Data;

import java.util.List;

/**
 * @Author: renzewen
 * @Date: 2022/8/22 9:48
 */
@Data
public class ProduceInspectionRecordDto{

    /**
     * 探伤记录信息
     */
    private JSONObject inspectionRecord;
    /**
     * 缺陷列表
     */
    private List<ProduceDefectsInfo> produceDefectsInfos;
    /**
     * 关联工序ids
     */
    private List<String> itemIds;
    /**
     * 模板类型
     */
    private String tempType;

}
