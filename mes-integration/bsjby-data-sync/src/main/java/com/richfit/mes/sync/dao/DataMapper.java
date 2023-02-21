package com.richfit.mes.sync.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.sync.entity.RawData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author gaol
 * @date 2023/2/9
 * @apiNote
 */
@Mapper
public interface DataMapper extends BaseMapper<RawData> {

    List<RawData> selectRawData(@Param("par") Map par);
    
    void batchUpdateAttachmentId(@Param("par") Map updateMap);
}
