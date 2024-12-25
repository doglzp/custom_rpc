package com.lzp.rpc;

import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.constants.RpcConstant;
import com.lzp.rpc.utils.ConfigUtils;
import org.junit.Test;

public class ApplicationTest {

    @Test
    public void test(){
        RpcConfig config = ConfigUtils.getConfig(RpcConstant.DEFAULT_CONFIG_PREFIX, RpcConfig.class);
    }
}
