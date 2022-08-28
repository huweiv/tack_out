package com.huweiv.common;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName BaseContext
 * @Description 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * @CreateTime 2022/7/16 10:41
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
