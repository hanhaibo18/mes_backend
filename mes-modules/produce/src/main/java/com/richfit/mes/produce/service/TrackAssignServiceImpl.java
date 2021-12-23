package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 马峰
 * @Description 跟单派工服务
 */
@Service
public class TrackAssignServiceImpl extends ServiceImpl<TrackAssignMapper, Assign> implements TrackAssignService{

    @Autowired
    public TrackAssignMapper trackAssignMapper;
    
    public IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw)
    {
        return trackAssignMapper.getPageAssignsByStatus(page, qw);
    }
    
    public IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page,@Param("name") String name, QueryWrapper<TrackItem> qw)
    {
        return trackAssignMapper.getPageAssignsByStatusAndTrack(page,name, qw);
    }
    
     public IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page,@Param("name") String name, QueryWrapper<TrackItem> qw)
    {
        return trackAssignMapper.getPageAssignsByStatusAndRouter(page,name, qw);
    }
    
    public IPage<Assign> queryPage(Page page, String siteId,String trackNo,String routerNo, String startTime, String endTime, String state,String userId)
    {
          
            return trackAssignMapper.queryPage(page,siteId,trackNo,routerNo,startTime,endTime,state,userId);
    }
    
    

}
