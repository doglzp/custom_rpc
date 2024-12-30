package com.lzp.rpceasy.springboot.starter.bootStrap;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.config.RpcConfig;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpceasy.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcProviderBootStrap implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            Class<?> interfacedClass = rpcService.interfaceClass();
            if (interfacedClass == void.class) {
                interfacedClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfacedClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            LocalRegistry.register(serviceName, beanClass);
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getRegistry();
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getHost());
            serviceMetaInfo.setServicePort(rpcConfig.getPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}
