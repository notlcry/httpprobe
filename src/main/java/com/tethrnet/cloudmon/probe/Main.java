package com.tethrnet.cloudmon.probe;

import io.vertx.core.cli.CLI;

/**
 * Created by huiyu on 17/3/13.
 */
public class Main {

    public static void main(String[] args) {

        // start http server on startup
        Server server =  new Server();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try to connect probe server on startup
        Client client = new Client();
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
