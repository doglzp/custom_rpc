package com.lzp.rpc.protocol;

import com.lzp.rpc.constants.ProtocolConstant;
import com.lzp.rpc.enums.ProtocolMessageSerializerEnum;
import com.lzp.rpc.enums.ProtocolMessageTypeEnum;
import com.lzp.rpc.model.RpcRequest;
import com.lzp.rpc.model.RpcResponse;
import com.lzp.rpc.serializer.Serializer;
import com.lzp.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;
import java.util.Objects;

public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("magic error");
        }
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null){
            throw new RuntimeException("unSupported serializer");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        switch (messageTypeEnum) {
            case REQUEST:
                return new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcRequest.class));
            case RESPONSE:
                return new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcResponse.class));
            case HEARTBEAT:
            case OTHERS:
            default:
                throw new RuntimeException("unknown message type");
        }
    }
}
