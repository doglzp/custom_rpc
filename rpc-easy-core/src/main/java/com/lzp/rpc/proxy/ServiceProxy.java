package com.lzp.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.constants.ProtocolConstant;
import com.lzp.rpc.constants.RpcConstant;
import com.lzp.rpc.enums.ProtocolMessageSerializerEnum;
import com.lzp.rpc.enums.ProtocolMessageTypeEnum;
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
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            ServiceMetaInfo serviceMetaInfo = discoveryService(serviceName);
            return requestByTcp(serviceMetaInfo, rpcRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Object requestByTcp(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws Exception {
        Vertx vertx = Vertx.vertx();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        NetClient netClient = vertx.createNetClient();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if (result.succeeded()){
                System.out.println("Connected tcp server");
                NetSocket socket = result.result();
                ProtocolMessage<RpcRequest> requestProtocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                requestProtocolMessage.setHeader(header);
                requestProtocolMessage.setBody(rpcRequest);
                try {
                    socket.write(ProtocolMessageEncoder.encode(requestProtocolMessage));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                socket.handler(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Received: " + buffer);
                });
            }else {
                System.out.println("Failed to connect tcp server");
            }
        });
        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse.getData();
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

    private ServiceMetaInfo discoveryService(String serviceName) throws Exception {
        Registry registry = RegistryFactory.getRegistry();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> nodeServiceMetaInfoList = registry.discovery(serviceMetaInfo.getServiceKey());
        if (nodeServiceMetaInfoList.isEmpty()){
            throw new RuntimeException("can not find service");
        }
        return nodeServiceMetaInfoList.get(0);
    }
}
