package com.lzp.rpc.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name = "custom-rpc";
    private String version = "1.0";
    private String host = "127.0.0.1";
    private int port = 8080;
    private boolean isMock = false;

}
