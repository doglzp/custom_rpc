package com.lzp.rpc;

import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.constants.RpcConstant;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    private RpcApplication() {
    }

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc config init, config:{}", rpcConfig);

        RegistryConfig registryConfig = newRpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry();
        registry.init(registryConfig);
        log.info("registry init, config:{}", registryConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init() {
        RpcConfig rpcNewConfig;
        try {
            rpcNewConfig = ConfigUtils.getConfig(RpcConstant.DEFAULT_CONFIG_PREFIX, RpcConfig.class);
        } catch (Exception e) {
            rpcNewConfig = new RpcConfig();
        }
        init(rpcNewConfig);
    }

    public static RpcConfig getRpcConfig() {
        return getRpcConfig(null, null);
    }

    public static RpcConfig getRpcConfig(String env, String fileFormat) {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    rpcConfig = ConfigUtils.getConfig(RpcConstant.DEFAULT_CONFIG_PREFIX, RpcConfig.class, env, fileFormat);
                }
            }
        }
        return rpcConfig;
    }


}
