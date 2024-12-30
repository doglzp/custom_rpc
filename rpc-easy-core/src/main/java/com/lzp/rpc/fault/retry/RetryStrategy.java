package com.lzp.rpc.fault.retry;

import com.lzp.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    RpcResponse retry(Callable<RpcResponse> callable) throws Exception;
}
