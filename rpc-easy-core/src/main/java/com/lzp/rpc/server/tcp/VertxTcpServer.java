package com.lzp.rpc.server.tcp;

import com.lzp.rpc.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements Server {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(new VertxTcpRequestHandler()).listen(port, result->{
            if (result.succeeded()){
                System.out.println("server started, listen port" + port);
            }else {
                System.out.println("Failed to start server:" + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
