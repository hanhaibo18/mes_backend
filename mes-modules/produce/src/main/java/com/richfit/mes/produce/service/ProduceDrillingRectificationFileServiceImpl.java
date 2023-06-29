package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProduceDrillingRectificationFile;
import com.richfit.mes.produce.dao.ProduceDrillingRectificationFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author llh
 * @description 针对表【produce_drilling_rectification_file(整改单附件信息)】的数据库操作Service实现
 * @createDate 2023-06-14 14:42:03
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProduceDrillingRectificationFileServiceImpl extends ServiceImpl<ProduceDrillingRectificationFileMapper, ProduceDrillingRectificationFile> implements ProduceDrillingRectificationFileService {

}




