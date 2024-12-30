package com.lzp.rpceasy.springboot.starter.annotation;

import com.lzp.rpceasy.springboot.starter.bootStrap.RpcConsumerBootStrap;
import com.lzp.rpceasy.springboot.starter.bootStrap.RpcInitBootStrap;
import com.lzp.rpceasy.springboot.starter.bootStrap.RpcProviderBootStrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootStrap.class, RpcProviderBootStrap.class, RpcConsumerBootStrap.class})
public @interface EnableRpc {

    boolean needServer() default true;
}
