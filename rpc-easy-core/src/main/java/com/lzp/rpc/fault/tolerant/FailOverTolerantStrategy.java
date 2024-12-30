package com.lzp.rpc.fault.tolerant;

import com.lzp.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("故障转移");
        return null;
    }
}
