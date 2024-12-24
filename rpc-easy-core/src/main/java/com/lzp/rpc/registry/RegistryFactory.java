package com.lzp.rpc.registry;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.spi.SpiLoader;

public class RegistryFactory {

    private static volatile Registry registry;

    public static Registry getRegistry(RegistryConfig registryConfig) {
        if (registry == null) {
            synchronized (RegistryFactory.class) {
                if (registry == null) {
                    SpiLoader.load(Registry.class);
                    registry = SpiLoader.getInstance(Registry.class, registryConfig.getType());
                }
            }
        }
        return registry;
    }

    public static Registry getRegistry(){
        return getRegistry(RpcApplication.getRpcConfig().getRegistryConfig());
    }
}
