package com.lzp.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.serializer.JdkSerializer;
import com.lzp.rpc.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final Serializer serializer = new JdkSerializer();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
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
}
