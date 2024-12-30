package com.lzp.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.lzp.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> responseRetryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("retry nums:{}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return responseRetryer.call(callable);
    }
}
