package com.example.mixue.exception;

import com.example.mixue.pojo.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理类
 * 统一处理控制器抛出的各种异常
 */
@RestControllerAdvice // 控制器增强，处理所有控制器抛出的异常
public class GlobalExceptionHandlerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    // 处理业务异常       用户不存在、密码错误等
    @ExceptionHandler(BusinessException.class)
    public ResponseMessage handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return new ResponseMessage(e.getCode(), e.getMessage(), null);
    }

    // 处理参数校验异常     用户名不能为空、密码长度不符合要求等
    //当使用@Validated或@Valid注解校验整个对象时触发
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseMessage handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败: {}", errorMsg);
        return new ResponseMessage(400, errorMsg, null);
    }

    // 处理单个参数校验异常
    //当直接在方法参数上使用校验注解时触发
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseMessage handleConstraintViolationException(ConstraintViolationException e) {
        String errorMsg = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("参数校验失败: {}", errorMsg);
        return new ResponseMessage(400, errorMsg, null);
    }

    // 处理数据库唯一约束异常     唯一键冲突、外键约束等
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseMessage handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据库操作异常", e);
        return new ResponseMessage(500, "数据库操作失败，请检查数据是否已存在", null);
    }

    // 处理JSON解析异常       请求体JSON格式错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseMessage handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求参数解析失败", e);
        return new ResponseMessage(400, "请求参数格式错误，请检查JSON格式", null);
    }

    // 处理其他所有异常
    @ExceptionHandler(Exception.class)
    public ResponseMessage handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("系统异常: {}", e.getMessage(), e);
        return new ResponseMessage(500, "系统繁忙，请稍后再试", null);
    }
}