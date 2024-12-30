package com.lzp.rpceasy.springboot.starter.bootStrap;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.server.tcp.VertxTcpServer;
import com.lzp.rpceasy.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if (needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getPort());
        }else {
            log.info("不启动Server");
        }
    }

}
