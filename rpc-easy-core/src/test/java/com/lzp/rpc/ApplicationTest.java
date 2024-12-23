package com.lzp.rpc;

import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.constans.RpcConstants;
import com.lzp.rpc.utils.ConfigUtils;
import org.junit.Test;

public class ApplicationTest {

    @Test
    public void test(){
        RpcConfig config = ConfigUtils.getConfig(RpcConstants.DEFAULT_CONFIG_PREFIX, RpcConfig.class);
    }
}
