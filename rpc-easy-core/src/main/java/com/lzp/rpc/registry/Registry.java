package com.lzp.rpc.registry;

import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    List<ServiceMetaInfo> discovery(String serviceKey);

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    void destroy();
}
