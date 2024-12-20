package com.lzp.rpc.proxy;

import java.lang.reflect.Proxy;

public class MockProxyFactory {
    public static <T> T getProxy(Class<T> serviceClazz) {
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class[]{serviceClazz},
                new MockProxy()
        );
    }
}
