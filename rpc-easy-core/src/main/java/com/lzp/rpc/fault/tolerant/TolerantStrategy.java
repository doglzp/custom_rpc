package com.lzp.rpc.fault.tolerant;

import com.lzp.rpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
