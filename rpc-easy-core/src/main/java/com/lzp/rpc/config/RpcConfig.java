package com.lzp.rpc.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name;
    private String version;
    private String host;
    private int port;

}
