package com.huweiv.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @title
 * @description 自定义元数据对象处理器
 * @author HUWEIV
 * @date 2022/7/16 10:05
 * @return
 */
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * @title insertFill
     * @description 插入时自动填充
     * @author HUWEIV
     * @date 2022/7/16 10:13
     * @return void
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * @title updateFill
     * @description 更新时自动填充
     * @author HUWEIV
     * @date 2022/7/16 10:14
     * @return void
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
