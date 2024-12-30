package com.lzp.rpc.bootstrap;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.model.ServiceRegisterInfo;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.server.Server;
import com.lzp.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootStrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        RpcApplication.init();
        String host = RpcApplication.getRpcConfig().getHost();
        int port = RpcApplication.getRpcConfig().getPort();
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getInterfaceClass());

            Registry registry = RegistryFactory.getRegistry();
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceHost(host);
            serviceMetaInfo.setServicePort(port);
            serviceMetaInfo.setServiceName(serviceName);
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Server server = new VertxTcpServer();
        server.doStart(port);
    }
}
