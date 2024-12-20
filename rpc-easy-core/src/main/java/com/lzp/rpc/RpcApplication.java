package com.lzp.rpc;

import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.constans.RpcConstants;
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
    }

    public static void init() {
        RpcConfig rpcNewConfig;
        try {
            rpcNewConfig = ConfigUtils.getConfig(RpcConstants.RPC_CONFIG_PREFIX, RpcConfig.class);
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
                    rpcConfig = ConfigUtils.getConfig(RpcConstants.RPC_CONFIG_PREFIX, RpcConfig.class, env, fileFormat);
                }
            }
        }
        return rpcConfig;
    }


}
