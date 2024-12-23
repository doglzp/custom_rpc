package com.lzp.rpc.config;

import com.lzp.rpc.serializer.SerializerKeys;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcConfig {

    private String name = "custom-rpc";
    private String version = "1.0";
    private String host = "127.0.0.1";
    private int port = 8080;
    private boolean isMock = false;
    private String serializer = SerializerKeys.JDK;

}
