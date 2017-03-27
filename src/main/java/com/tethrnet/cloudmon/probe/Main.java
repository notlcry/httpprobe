package com.tethrnet.cloudmon.probe;


import io.vertx.core.Vertx;

/**
 * Created by huiyu on 17/3/13.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("please input the probe server, Useage: java -jar httpProbe.jar 192.168.56.1 ");
            System.exit(1);
        }

        String probeServer = args[0];

        String interval = args[1];
        int intv = Integer.parseInt(interval);

        String countStr = args[2];
        int count = Integer.parseInt(countStr);

        Vertx vertx = Vertx.vertx();

        MsgHandle handle = new MsgHandle(vertx);
        new Thread(handle).start();

        HttpClientRunner handleHttp = new HttpClientRunner(vertx, intv, count);
        new Thread(handleHttp).start();

        // start http server on startup
        Server server = new Server(vertx);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try to connect probe server on startup
        ProbeClient probeClient = new ProbeClient(vertx, probeServer);

        MsgQueue.getInstance().setNetClient(probeClient.getClient());
        try {
            probeClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
