package com.reggie.common;

/**
 * 自定义业务异常类
 */

public class CustomException extends RuntimeException{
    //创建一个运行异常类，用于springmvc的异常处理器进行全局异常的捕获
    public CustomException(String message) {
        super(message);
    }
}
