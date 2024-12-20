package com.lzp.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockProxy implements InvocationHandler {
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        log.info("MockProxy invoke");
        return getMockObject(method.getReturnType());
    }

    private Object getMockObject(Class<?> returnType){
        if (returnType.isPrimitive()){
            if (returnType == int.class){
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            } else if (returnType == float.class) {
                return 0.0f;
            } else if (returnType == double.class) {
                return 0.0d;
            } else if (returnType == boolean.class) {
                return false;
            }
        }
        try {
            return returnType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
