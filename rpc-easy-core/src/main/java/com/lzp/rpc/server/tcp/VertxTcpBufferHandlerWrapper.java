package com.lzp.rpc.server.tcp;

import com.lzp.rpc.constants.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
public class VertxTcpBufferHandlerWrapper implements Handler<Buffer> {

    private RecordParser recordParser;

    public VertxTcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            int size = -1;
            final Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (size == -1){
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);
                }else {
                    resultBuffer.appendBuffer(buffer);
                    // 这里执行的被装饰的方法
                    bufferHandler.handle(resultBuffer);
                    size = -1;
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                }
            }
        });
        return parser;
    }
}
