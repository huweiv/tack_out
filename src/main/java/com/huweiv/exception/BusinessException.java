package com.huweiv.exception;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName BusinessException
 * @Description TODO
 * @CreateTime 2022/5/11 16:13
 */

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
