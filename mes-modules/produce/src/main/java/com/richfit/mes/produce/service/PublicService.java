package com.richfit.mes.produce.service;

import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PublicService.java
 * @Author: Hou XinYu
 * @Description: 跟单流程公用方法
 * @CreateTime: 2022年07月12日 16:31:00
 */
public interface PublicService {
    /**
     * 功能描述: 正向流程 修改状态方法
     *
     * @param map
     * @param code
     * @Author: xinYu.hou
     * @Date: 2022/7/12 16:33
     * @return: Boolean
     **/
    Boolean publicUpdateState(Map<String, String> map, int code);

    /**
     * 功能描述: 更改报工状态方法
     *
     * @param map Id
     * @Author: xinYu.hou
     * @Date: 2022/7/12 16:54
     * @return: Boolean
     **/
    Boolean updateComplete(Map<String, String> map);

    /**
     * 功能描述: 更改派工状态
     *
     * @param map
     * @Author: xinYu.hou
     * @Date: 2022/8/1 16:12
     * @return: Boolean
     **/
    Boolean updateDispatching(Map<String, String> map);

    /**
     * 功能描述: 更改质检状态
     *
     * @param map
     * @Author: xinYu.hou
     * @Date: 2022/8/3 14:37
     * @return: Boolean
     **/
    Boolean updateQualityTesting(Map<String, String> map);

    /**
     * 功能描述: 更改调度状态
     *
     * @param map
     * @Author: xinYu.hou
     * @Date: 2022/8/5 16:15
     * @return: Boolean
     **/
    Boolean updateDispatch(Map<String, String> map);

    /**
     * 功能描述: 激活工序
     *
     * @param map
     * @Author: xinYu.hou
     * @Date: 2022/7/13 14:43
     * @return: Boolean
     **/
    Boolean activationProcess(Map<String, String> map);

    /**
     * 功能描述: 第三方下工序激活
     *
     * @param trackHeadId
     * @param certificateId   合格证Id
     * @param optSequenceList 工序顺序号
     * @Author: xinYu.hou
     * @Date: 2022/7/22 9:30 updateTime: 2022年8月25日10:49:09
     * @return: Boolean
     **/
    Boolean thirdPartyAction(String trackHeadId, String certificateId, List<String> optSequenceList);

    /**
     * 功能描述: 自动派工
     *
     * @param map
     * @Author: xinYu.hou
     * @Date: 2022/8/15 2:45
     * @return: Boolean
     **/
    Boolean automaticProcess(Map<String, String> map);

    public boolean activation(TrackItem trackItem);

}
