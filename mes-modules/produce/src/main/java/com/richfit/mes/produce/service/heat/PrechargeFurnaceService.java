package com.richfit.mes.produce.service.heat;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2023.1.4
 */
public interface PrechargeFurnaceService extends IService<PrechargeFurnace> {
    /**
     * 功能描述:装炉
     *
     * @param assignList
     * @Author: zhiqiang.lu
     * @Date: 2023/1/5 9:45
     * @return: void
     **/
    public void furnaceCharging(List<Assign> assignList, String tempWork);

    void furnaceChargingHot(List<Assign> assignList, String texture, String branchCode, String workblankType,String classes);

    /**
     * 功能描述:查询装炉跟单列表
     *
     * @param id
     * @Author: zhiqiang.lu
     * @Date: 2023/1/5 9:45
     * @return: List<Assign>
     **/
    public List<Assign> queryTrackItem(Long id);

    /**
     * 更新工序信息
     *
     * @param id
     */
    void updateItemInfo(Long id);

    /**
     * 功能描述:装炉跟单工序添加
     *
     * @param assignList
     * @Author: zhiqiang.lu
     * @Date: 2023/1/5 9:45
     * @return: PrechargeFurnace
     **/
    public PrechargeFurnace addTrackItem(List<Assign> assignList);

    PrechargeFurnace addTrackItemHot(List<Assign> assignList);

    PrechargeFurnace addTrackItemHotYl(List<Assign> assignList);

    /**
     * 功能描述:装炉跟单工序删除
     *
     * @param assignList
     * @Author: zhiqiang.lu
     * @Date: 2023/1/5 9:45
     * @return: PrechargeFurnace
     **/
    public PrechargeFurnace deleteTrackItem(List<Assign> assignList);

    /**
     * 功能描述:装炉跟单工序删除(冶炼车间)
     *
     * @param assignList
     * @Author: zhiqiang.lu
     * @Date: 2023/1/5 9:45
     * @return: PrechargeFurnace
     **/
    public PrechargeFurnace deleteTrackItemYl(List<Assign> assignList);

    Boolean updateRecordStatus(Long id, String recordStatus);

    List totalWeightMolten(String branchCode);

    List<TrackItem> queryAssignByTexture(String texture, String branchCode);

    List<TrackItem> getItemsByPrechargeFurnace(Long id);

   boolean furnaceRollBack(Long id);


}
