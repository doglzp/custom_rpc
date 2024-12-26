package com.lzp.rpc.loadbalancer;

import com.lzp.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer{

    AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()){
            return null;
        }
        int nodeNums = serviceMetaInfoList.size();
        if (nodeNums == 1){
            return serviceMetaInfoList.get(0);
        }
        int index = currentIndex.getAndIncrement() % nodeNums;
        return serviceMetaInfoList.get(index);
    }
}
