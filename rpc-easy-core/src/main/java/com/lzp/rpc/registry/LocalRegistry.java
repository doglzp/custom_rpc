package com.lzp.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry{

    private static final Map<String,Class<?>> services = new ConcurrentHashMap<>();

    public static void register(String serviceName, Class<?> serviceClass) {
        services.put(serviceName,serviceClass);
    }

    public static Class<?> getService(String serviceName) {
        return services.get(serviceName);
    }

    public static void remove(String serviceName) {
        services.remove(serviceName);
    }
}
