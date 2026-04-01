package com.example.mixue.pojo.dto;

import org.springframework.http.HttpStatus;

/**
 * 统一响应格式类
 * @param <T> 响应数据类型
 */
public class ResponseMessage<T> {
    private Integer code; // 状态码
    private String message; // 消息
    private T data; // 数据

    public ResponseMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    //接口请求成功
    public static <T> ResponseMessage<T> success(T data){
        return new ResponseMessage(HttpStatus.OK.value(),"success",data);
    }

    // 带详细信息的成功方法
    public static <T> ResponseMessage<T> success(String message, T data) {
        return new ResponseMessage<>(HttpStatus.OK.value(), message, data);
    }

    // 错误响应
    public static <T> ResponseMessage<T> error(Integer code, String message) {
        return new ResponseMessage<>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
