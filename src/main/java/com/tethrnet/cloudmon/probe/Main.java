package com.tethrnet.cloudmon.probe;


import io.vertx.core.Vertx;

/**
 * Created by huiyu on 17/3/13.
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        MsgHandle handle = new MsgHandle(vertx);
        new Thread(handle).start();

        HttpClientRunner handleHttp = new HttpClientRunner(vertx);
        new Thread(handleHttp).start();

        // start http server on startup
        Server server =  new Server(vertx);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try to connect probe server on startup
        Client client = new Client(vertx);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
