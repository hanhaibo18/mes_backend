package com.richfit.mes.produce.service;

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
     * 功能描述: 激活工序
     *
     * @param trackItemId
     * @Author: xinYu.hou
     * @Date: 2022/7/13 14:43
     * @return: Boolean
     **/
    Boolean activationProcess(String trackItemId);
}
