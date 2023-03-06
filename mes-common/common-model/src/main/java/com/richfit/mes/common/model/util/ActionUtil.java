package com.richfit.mes.common.model.util;

import com.richfit.mes.common.model.produce.Action;

/**
 * 生产动态工具类
 *
 * @author HanHaiBo
 * @date 2023/3/2 16:45
 */
public class ActionUtil {
    /**
     * @param actionType 动作类型  新增  修改  删除   撤回   开工   报工  审核   派工
     * @param actionItem 操作对象   订单  计划   跟单    质检  库存
     * @param remark     单号信息
     * @return
     */
    public static Action buildAction(String branchId, String actionType, String actionItem, String remark, String ipAddress) {
        Action action = new Action();
        action.setBranchId(branchId);
        action.setActionType(actionType);
        action.setActionItem(actionItem);
        action.setRemark(remark);
        action.setIpAddress(ipAddress);
        return action;
    }
}
