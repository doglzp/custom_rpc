package com.lzp.rpceasy.springboot.starter.annotation;

import com.lzp.rpc.constants.RpcConstant;
import com.lzp.rpc.fault.retry.RetryStrategyKeys;
import com.lzp.rpc.fault.tolerant.TolerantStrategyKeys;
import com.lzp.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    Class<?> interfaceClass() default void.class;

    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    String retryStrategy() default RetryStrategyKeys.NO;

    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    boolean mock() default false;
}
