package com.lzp.rpc.fault.tolerant;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.spi.SpiLoader;

public class TolerantStrategyFactory {
    private static volatile TolerantStrategy tolerantStrategy;

    public static TolerantStrategy getTolerantStrategy(String key) {
        if (tolerantStrategy == null) {
            synchronized (TolerantStrategyFactory.class) {
                if (tolerantStrategy == null) {
                    SpiLoader.load(TolerantStrategy.class);
                    tolerantStrategy = SpiLoader.getInstance(TolerantStrategy.class, key);
                }
            }
        }
        return tolerantStrategy;
    }

    public static TolerantStrategy getTolerantStrategy() {
        return getTolerantStrategy(RpcApplication.getRpcConfig().getTolerantStrategy());
    }
}
