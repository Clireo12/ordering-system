package com.example.mixue.exception;

/**
 * 自定义业务异常类
 * 用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {
    private final int code; // 异常状态码

    /**
     * 构造函数
     * @param code 状态码
     * @param message 异常信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public int getCode() {
        return code;
    }
}
