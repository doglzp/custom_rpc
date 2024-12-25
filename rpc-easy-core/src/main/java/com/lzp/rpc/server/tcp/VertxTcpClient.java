package com.lzp.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {
    public void start(int port){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(port, "localhost", result -> {
            if (result.succeeded()){
                System.out.println("Connected");
                NetSocket socket = result.result();
                socket.write("Hello,server!");
                socket.handler(buffer -> {
                    System.out.println("Received: " + buffer.toString());
                });
            }else {
                System.out.println("Failed to connect");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start(8080);
    }
}
