package com.lzp.example.provider;

import com.lzp.example.common.service.UserService;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.bootstrap.ProviderBootStrap;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.model.ServiceRegisterInfo;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.server.Server;
import com.lzp.rpc.server.http.VertxHttpServer;
import com.lzp.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class EasyProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);
        ProviderBootStrap.init(serviceRegisterInfoList);
    }
}
