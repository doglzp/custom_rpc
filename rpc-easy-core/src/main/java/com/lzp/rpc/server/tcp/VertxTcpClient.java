package com.lzp.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.lzp.rpc.RpcApplication;
import com.lzp.rpc.constants.ProtocolConstant;
import com.lzp.rpc.enums.ProtocolMessageSerializerEnum;
import com.lzp.rpc.enums.ProtocolMessageTypeEnum;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.model.ServiceMetaInfo;
import com.lzp.rpc.protocol.ProtocolMessage;
import com.lzp.rpc.protocol.ProtocolMessageDecoder;
import com.lzp.rpc.protocol.ProtocolMessageEncoder;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class VertxTcpClient {
    public static RpcResponse doRequest(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws Exception {
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
                socket.handler(new VertxTcpBufferHandlerWrapper(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Received: " + buffer);
                }));
            }else {
                System.out.println("Failed to connect tcp server");
            }
        });
        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse;
    }
}
