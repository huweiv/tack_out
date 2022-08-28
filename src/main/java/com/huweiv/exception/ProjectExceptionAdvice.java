package com.huweiv.exception;

import com.huweiv.common.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName ProjectExceptionAdvice
 * @Description TODO
 * @CreateTime 2022/5/11 17:18
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class ProjectExceptionAdvice {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> doSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        String exMsg = ex.getMessage();
        if (exMsg.contains("Duplicate entry")) {
            String[] strings = exMsg.split(" ");
            String msg = strings[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(SystemException.class)
    public R<String> doSystemException(SystemException ex) {
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public R<String> doBusinessException(BusinessException ex) {
        return R.error(ex.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    public R<String> doException(Exception ex) {
//        return R.error("系统繁忙, 请稍后再试!");
//    }
}
