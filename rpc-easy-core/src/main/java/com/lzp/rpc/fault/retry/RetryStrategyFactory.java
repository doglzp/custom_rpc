package com.lzp.rpc.fault.retry;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.spi.SpiLoader;

public class RetryStrategyFactory {

    public static volatile RetryStrategy retryStrategy;

    public static RetryStrategy getRetryStrategy(String key) {
        if (retryStrategy == null) {
            synchronized (RetryStrategyFactory.class) {
                if (retryStrategy == null) {
                    SpiLoader.load(RetryStrategy.class);
                    retryStrategy = SpiLoader.getInstance(RetryStrategy.class,key);
                }
            }
        }
        return retryStrategy;
    }

    public static RetryStrategy getRetryStrategy() {
        return getRetryStrategy(RpcApplication.getRpcConfig().getRetryStrategy());
    }
}
