package com.lzp.rpc.serializer;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.spi.SpiLoader;

public class SerializerFactory {

    private static volatile Serializer serializer;

    public static Serializer getSerializer(String key) {
        if (serializer == null) {
            synchronized (SerializerFactory.class) {
                if (serializer == null) {
                    SpiLoader.load(Serializer.class);
                    serializer = SpiLoader.getInstance(Serializer.class, key);
                }
            }
        }
        return serializer;
    }

    public static Serializer getSerializer() {
        return getSerializer(RpcApplication.getRpcConfig().getSerializer());
    }
}
