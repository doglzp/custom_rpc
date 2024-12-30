package com.lzp.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.constants.ProtocolConstant;
import com.lzp.rpc.constants.RpcConstant;
import com.lzp.rpc.enums.ProtocolMessageSerializerEnum;
import com.lzp.rpc.enums.ProtocolMessageTypeEnum;
import com.lzp.rpc.fault.retry.RetryStrategy;
import com.lzp.rpc.fault.retry.RetryStrategyFactory;
import com.lzp.rpc.loadbalancer.LoadBalancer;
import com.lzp.rpc.loadbalancer.LoadBalancerFactory;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.protocol.ProtocolMessage;
import com.lzp.rpc.protocol.ProtocolMessageDecoder;
import com.lzp.rpc.protocol.ProtocolMessageEncoder;
import com.lzp.rpc.registry.Registry;
import com.lzp.rpc.registry.RegistryFactory;
import com.lzp.rpc.serializer.Serializer;
import com.lzp.rpc.serializer.SerializerFactory;
import com.lzp.rpc.server.tcp.VertxTcpBufferHandlerWrapper;
import com.lzp.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            ServiceMetaInfo serviceMetaInfo = discoveryService(serviceName, method.getName());
            RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy();
            RpcResponse rpcResponse = retryStrategy.retry(() -> VertxTcpClient.doRequest(serviceMetaInfo, rpcRequest));
            return rpcResponse.getData();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("调用失败" + e.getMessage());
        }
    }



    private static Object requestByHttp(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException {
        final Serializer serializer = SerializerFactory.getSerializer();
        String url = serviceMetaInfo.getServiceAddress();
        byte[] bytes = serializer.serialize(rpcRequest);
        try(HttpResponse httpResponse = HttpRequest.post(url)
                .body(bytes)
                .execute()){
            RpcResponse rpcResponse = serializer.deserialize(httpResponse.bodyBytes(), RpcResponse.class);
            return rpcResponse.getData();
        }
    }

    private ServiceMetaInfo discoveryService(String serviceName, String methodName) {
        Registry registry = RegistryFactory.getRegistry();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> nodeServiceMetaInfoList = registry.discovery(serviceMetaInfo.getServiceKey());
        if (nodeServiceMetaInfoList.isEmpty()){
            throw new RuntimeException("can not find service");
        }
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer();
        Map<String, Object> requestParams = new ConcurrentHashMap<>();
        requestParams.put("serviceName", serviceName);
        requestParams.put("methodName", methodName);
        return loadBalancer.select(requestParams, nodeServiceMetaInfoList);
    }
}
