package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.CheckAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XinYu.Hou
 */
@Mapper
public interface TrackCheckAttachmentMapper extends BaseMapper<CheckAttachment> {

    /**
     * 功能描述: 查询文件Id集合
     *
     * @param tiId
     * @Author: xinYu.hou
     * @Date: 2022/6/30 17:54
     * @return: List<String>
     **/
    @Select("SELECT track.file_id FROM produce_track_check_attachment track WHERE ti_id = #{tiId};")
    List<String> queryFileIdList(@Param("tiId") String tiId);
}
