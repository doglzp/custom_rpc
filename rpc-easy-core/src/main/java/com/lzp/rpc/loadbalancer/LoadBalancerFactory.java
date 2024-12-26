package com.lzp.rpc.loadbalancer;

import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.spi.SpiLoader;

public class LoadBalancerFactory {

    private static volatile LoadBalancer loadBalancer;

    public static LoadBalancer getLoadBalancer(String key){
        if (loadBalancer == null){
            synchronized (LoadBalancerFactory.class){
                if (loadBalancer == null){
                    SpiLoader.load(LoadBalancer.class);
                    loadBalancer = SpiLoader.getInstance(LoadBalancer.class, key);
                }
            }
        }
        return loadBalancer;
    }

    public static LoadBalancer getLoadBalancer(){
        return getLoadBalancer(RpcApplication.getRpcConfig().getLoadBalancer());
    }
}
