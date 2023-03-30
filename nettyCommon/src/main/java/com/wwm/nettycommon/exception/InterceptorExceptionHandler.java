
package com.wwm.nettycommon.exception;


import com.wwm.nettycommon.response.EnumCode;
import com.wwm.nettycommon.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;


/**
 * 处理拦截器异常或者aop抛出的异常
 */

@RestControllerAdvice(basePackages = "com.wwm")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InterceptorExceptionHandler {



    /**
     * 请求参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result requestParameterException(MissingServletRequestParameterException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    /**
     * JSON解析异常
     *
     * @param e
     * @return
     */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result httpMessageNotReadableException(HttpMessageNotReadableException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    /**
     * 请求类型错误异常  json 或者  text
     *
     * @param e
     * @return
     */

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    /**
     * 参数绑定异常
     *
     * @param e
     * @return
     */

    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public Result methodArgumentConversionNotSupportedException(MethodArgumentConversionNotSupportedException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    /**
     * 参数绑定异常
     *
     * @param e
     * @return
     */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    /**
     * 不支持的请求方法
     *
     * @param e
     * @return
     */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        Result result = Result.fail(EnumCode.FAIL.getCode(), e.getMessage());
        log.warn(e.getMessage(), e);
        return result;
    }


    @ExceptionHandler(BusinessException.class)
    public Result businessException(BusinessException e) {
        Result<Object> result = Result.fail(e.getErrorCode(), e.getErrorMessage());
        log.error(e.getMessage(), e);
        return result;
    }


    @ExceptionHandler(Exception.class)
    public Result exception(Exception exception) {
        log.error(exception.getMessage(), exception);
        Result result = Result.fail(EnumCode.FAIL);
        return result;
    }

    /**
     * get请求对象参数校验
     */
    @ExceptionHandler(BindException.class)
    public Result handlerConstraintViolationException(HttpServletRequest req, BindException e) {
        log.warn(e.getMessage(),e);
        if (!e.getAllErrors().isEmpty()) {
            String msg = e.getAllErrors().get(0).getDefaultMessage();
            if (chineseHandle(msg)) {
                if ((e.getAllErrors().get(0)) instanceof FieldError) {
                    FieldError error = (FieldError) e.getAllErrors().get(0);
                    msg = "请求参数" + error.getField() + "的值" + error.getRejectedValue() + "不合法，请检查修改后重新请求。";
                } else {
                    msg = "请求参数不合法，请检查修改后重新请求。";
                }
            }
            return Result.fail(400, msg);
        } else {
            return Result.fail(400,
                    e.getMessage());
        }

    }

    /**
     * post 请求参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handlerArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        log.warn(e.getMessage(),e);
        if (!e.getBindingResult().getAllErrors().isEmpty()) {
            String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            if (chineseHandle(msg)) {
                if ((e.getBindingResult().getAllErrors().get(0)) instanceof FieldError) {
                    FieldError error = (FieldError) e.getBindingResult().getAllErrors().get(0);
                    msg = "请求参数" + error.getField() + "的值" + error.getRejectedValue() + "不合法，请检查修改后重新请求。";
                } else {
                    msg = "请求参数不合法，请检查修改后重新请求。";
                }
            }
            return Result.fail(400, msg);
        } else {
            return Result.fail(400,
                    e.getMessage());
        }
    }

    /**
     * get 请求单个参数校验
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result constraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        log.warn(e.getMessage(),e);
        StringBuilder msg = new StringBuilder();
        e.getConstraintViolations().forEach(err -> msg.append(err.getMessage()).append("."));
        return responseException(request, msg.toString(), e);
    }

    private Result responseException(HttpServletRequest request, String msg, Exception e) {
        return Result.fail(400, msg);
    }


    private static boolean chineseHandle(String str) {
        // 判断一个字符串是否含有中文
        if (str == null) {
            return true;
        }
        for (char c : str.toCharArray()) {
            // 有一个中文字符就返回
            if (chineseHandle(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean chineseHandle(char c) {
        // 根据字节码判断
        return c >= 0x4E00 && c <= 0x9FA5;
    }


}

