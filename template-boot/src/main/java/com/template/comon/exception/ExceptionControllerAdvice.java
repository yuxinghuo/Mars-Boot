package com.template.comon.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.template.comon.base.BaseHttpServlet;
import com.template.comon.constant.ResultCodeEnum;
import com.template.comon.entity.Response;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * controller 增强器
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice implements BaseHttpServlet {

	private final String errorTemplate = "{}:[{}={}]";

	/**
	 * 全局异常捕捉处理
	 * 
	 * @param ex {@link Exception}
	 * @return {@link Response}
	 */
	@ExceptionHandler(value = { Throwable.class, Exception.class, BusinessException.class })
	public Response<String> errorHandler(Exception ex) {
		log.info("AdviceError={}", ex.getMessage());
		// 判断是否为 ajax 请求
		HttpServletRequest request = getRequest();
		if (requestIsAjax(request)) {
			return Response.result(ResultCodeEnum.CODE_500.code(), ex.getMessage());
		}
		// 返回错误页面
		HttpServletResponse response = getResponse();
		ErrorUtil.writeErrorHtml(response, ex.getClass());
		return null;
	}

	@ExceptionHandler(value = { BindException.class })
	public Response<String> errorHandler(BindException ex) {
		log.error("参数绑定异常", ex);
		FieldError fieldError = ex.getFieldError();
		String errorMessage = StrUtil.format(errorTemplate, fieldError.getDefaultMessage(), fieldError.getField(),
				fieldError.getRejectedValue());
		return Response.result(ResultCodeEnum.CODE_500.code(), errorMessage);
	}

}