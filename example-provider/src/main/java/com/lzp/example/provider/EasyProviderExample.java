package com.lzp.example.provider;

import com.lzp.example.common.service.UserService;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.server.HttpServer;
import com.lzp.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        Registry registry = RegistryFactory.getRegistry();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceHost(RpcApplication.getRpcConfig().getHost());
        serviceMetaInfo.setServicePort(RpcApplication.getRpcConfig().getPort());
        serviceMetaInfo.setServiceName(serviceName);
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getPort());
    }
}
