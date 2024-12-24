package com.lzp.rpc.registry;


import com.lzp.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache;

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    void writeCache(String key, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(key, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @return
     */
    List<ServiceMetaInfo> readCache(String key) {
        return this.serviceCache.get(key);
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }
}
