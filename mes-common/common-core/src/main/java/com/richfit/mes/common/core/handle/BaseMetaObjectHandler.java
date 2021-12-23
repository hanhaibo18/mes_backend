package com.richfit.mes.common.core.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author sun
 * @Description mybatis-plus自定义填充公共字段
 */
//@Component
@Slf4j
public class BaseMetaObjectHandler implements MetaObjectHandler {

    /**
     * 获取当前用户，为空返回默认system
     *
     * @return
     */
    protected String getCurrentUsername() {
        return "system";
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUsername());
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "modifyBy", String.class, getCurrentUsername());
        this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "modifyBy", String.class, getCurrentUsername());
        this.strictInsertFill(metaObject, "modifyTime", Date.class, new Date());
    }
}
