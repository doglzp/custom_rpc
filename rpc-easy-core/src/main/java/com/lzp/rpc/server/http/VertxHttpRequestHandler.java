package com.lzp.rpc.server.http;

import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.registry.LocalRegistry;
import com.lzp.rpc.serializer.Serializer;
import com.lzp.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;
import java.lang.reflect.Method;

public class VertxHttpRequestHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = SerializerFactory.getSerializer();
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null){
                rpcResponse.setMessage("request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            try {
                Class<?> service = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(service.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    public void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        try {
            request.response()
                    .putHeader("content-type", "application/json")
                    .end(Buffer.buffer(serializer.serialize(rpcResponse)));
        } catch (IOException e) {
            e.printStackTrace();
            request.response().end(Buffer.buffer());
        }
    }
}
