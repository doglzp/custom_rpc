package com.lzp.rpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer vertxHttpServer = vertx.createHttpServer();
        vertxHttpServer
                .requestHandler(new HttpRequestHandler())
                .listen(port, result -> {
                    if (result.succeeded()) {
                        System.out.println("server started");
                    } else {
                        System.out.println("Failed to start server:" + result.cause());
                    }
                });
    }
}
