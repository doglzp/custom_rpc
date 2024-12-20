package com.lzp.rpc.proxy;

import com.lzp.rpc.RpcApplication;

import java.lang.reflect.InvocationHandler;

public class ProxyFactory {

    public static final boolean IS_MOCK = RpcApplication.getRpcConfig().isMock();

    public static <T> T getProxy(Class<T> serviceClazz)
    {
        if (IS_MOCK) {
            return MockProxyFactory.getProxy(serviceClazz);
        } else {
            return ServiceProxyFactory.getProxy(serviceClazz);
        }
    }
}
