package com.lzp.rpc.fault.retry;

import com.lzp.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
