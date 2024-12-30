package com.lzp.rpceasy.springboot.starter.bootStrap;

import com.lzp.rpc.proxy.ServiceProxyFactory;
import com.lzp.rpceasy.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

@Slf4j
public class RpcConsumerBootStrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
