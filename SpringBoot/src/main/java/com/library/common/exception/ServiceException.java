package com.library.common.exception;

import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class ServiceException extends RuntimeException {
    private Integer code;

    public ServiceException(String msg) {
        super(msg);
        this.code = 500; // 默认业务错误码
    }

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}