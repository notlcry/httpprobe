package com.tethrnet.cloudmon.probe;


import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by huiyu on 17/3/13.
 */
public class Main {

    protected static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {

        if (args.length < 5) {
            System.out.println("please input the probe server, Useage: java -jar httpProbe.jar [server_ip] " +
                    "[client_ip] [interval] [count_per_flow] [count_per_interval]");
            System.exit(1);
        }

        String probeServer = args[0];

        String clientIp = args[1];

        String interval = args[2];
        int intv = Integer.parseInt(interval);

        String countStr = args[3];
        int count = Integer.parseInt(countStr);

        String everyCountStr = args[4];
        int everyCount = Integer.parseInt(everyCountStr);

        Vertx vertx = Vertx.vertx();

        MsgHandle handle = new MsgHandle(vertx);
        new Thread(handle).start();

        HttpClientRunner handleHttp = new HttpClientRunner(vertx, intv, count, everyCount);
        new Thread(handleHttp).start();

        // start http server on startup
        HttpServer server = new HttpServer(vertx);
        try {
            server.start();
        } catch (Exception e) {
            log.error("Cannot start http server, " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // try to connect probe server on startup
        ProbeClient probeClient = new ProbeClient(vertx, probeServer, clientIp);

        MsgQueue.getInstance().setNetClient(probeClient.getClient());
        try {
            probeClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
