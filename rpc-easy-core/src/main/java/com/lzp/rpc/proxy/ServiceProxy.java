package com.lzp.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.constans.RpcConstants;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.serializer.JdkSerializer;
import com.lzp.rpc.serializer.Serializer;
import com.lzp.rpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final Serializer serializer = SerializerFactory.getSerializer();
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            String url = discoveryService(serviceName).getServiceAddress();
            try(HttpResponse httpResponse = HttpRequest.post(url)
                    .body(bytes)
                    .execute()){
                RpcResponse rpcResponse = serializer.deserialize(httpResponse.bodyBytes(), RpcResponse.class);
                return rpcResponse.getData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private ServiceMetaInfo discoveryService(String serviceName) throws Exception {
        Registry registry = RegistryFactory.getRegistry();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstants.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> nodeServiceMetaInfoList = registry.discovery(serviceMetaInfo.getServiceKey());
        if (nodeServiceMetaInfoList.isEmpty()){
            throw new RuntimeException("can not find service");
        }
        return nodeServiceMetaInfoList.get(0);
    }
}
