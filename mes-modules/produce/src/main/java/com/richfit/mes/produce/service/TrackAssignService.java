package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.ibatis.annotations.Param;

/**
 * @author 马峰
 * @Description 跟单派工
 */
public interface TrackAssignService extends IService<Assign> {
    IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw);
    IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page,@Param("name") String name, QueryWrapper<TrackItem> qw);
      IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page,@Param("name") String name, QueryWrapper<TrackItem> qw);
  
    IPage<Assign> queryPage(Page page, String siteId,String trackNo,String routerNo, String startTime, String endTime, String state,String userId);
}
