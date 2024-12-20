package com.lzp.example.provider;

import com.lzp.example.common.service.UserService;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.server.HttpServer;
import com.lzp.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getPort());
    }
}
