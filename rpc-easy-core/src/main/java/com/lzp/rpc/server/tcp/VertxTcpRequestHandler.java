package com.lzp.rpc.server.tcp;

import com.lzp.rpc.enums.ProtocolMessageTypeEnum;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.protocol.ProtocolMessage;
import com.lzp.rpc.protocol.ProtocolMessageDecoder;
import com.lzp.rpc.protocol.ProtocolMessageEncoder;
import com.lzp.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class VertxTcpRequestHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket socket) {
        socket.handler(new VertxTcpBufferHandlerWrapper(buffer -> {
            ProtocolMessage<RpcRequest> protocolMessage;
            // 解码，调用，编码返回
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("request is null");
                doResponse(socket, rpcResponse, protocolMessage.getHeader());
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
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                throw new RuntimeException(e);
            }
            doResponse(socket, rpcResponse, protocolMessage.getHeader());
        }));
    }

    public void doResponse(NetSocket socket, RpcResponse rpcResponse, ProtocolMessage.Header header) {
        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
        try {
            socket.write(ProtocolMessageEncoder.encode(new ProtocolMessage<>(header, rpcResponse)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
