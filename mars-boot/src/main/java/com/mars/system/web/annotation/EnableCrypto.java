package com.mars.system.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mars.system.web.config.CryptoConfig;
import org.springframework.context.annotation.Import;



/**
 * 接口参数解密
 * 
 * @author 薛超
 * @since 2019年11月28日
 * @version 1.0.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CryptoConfig.class)
@Documented
public @interface EnableCrypto {

}