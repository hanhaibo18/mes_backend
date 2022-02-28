package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.Announcement;
import com.richfit.mes.sys.dao.AnnouncementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName: AnnouncementServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 通知公告
 * @CreateTime: 2022年01月27日 11:17:00
 */
@Slf4j
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService{

}
