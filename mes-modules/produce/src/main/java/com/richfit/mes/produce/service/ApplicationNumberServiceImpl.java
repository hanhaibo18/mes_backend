package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ApplicationNumber;
import com.richfit.mes.produce.dao.ApplicationNumberMapper;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ApplicationNumberServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 申请单号接口实现
 * @CreateTime: 2022年11月17日 10:24:00
 */
@Service
public class ApplicationNumberServiceImpl extends ServiceImpl<ApplicationNumberMapper, ApplicationNumber> implements ApplicationNumberService {
    
}
