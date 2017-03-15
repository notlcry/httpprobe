package com.tethrnet.cloudmon.probe;

import com.google.gson.Gson;
import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * Created by huiyu on 17/3/14.
 */
public class MsgHandle implements Runnable {

    protected Log log = LogFactory.getLog(MsgHandle.class);
    private Vertx vertx;

    public MsgHandle(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void run() {
        vertx.setPeriodic(1, id -> {
            String record = MsgQueue.getInstance().poll();
            if (record != null) {
                log.info("poll msg: " + record);

                Gson gson = new Gson();
                HashMap map = gson.fromJson(record, HashMap.class);
                String action = map.get("action").toString();
                String host = map.get("host").toString();
                if (action.equals("add")){
                    MsgQueue.getInstance().addHost(host);
                    log.info("add " + host + " to Map");
                }

                if (action.equals("remove")){
                    MsgQueue.getInstance().removeHost(host);
                    log.info("remove " + host + " from Map");
                }


            }
        });
    }

    private void startProbe(String host, int port) {
        while (true) {
            try {
                vertx.createHttpClient().getNow(port, host, "/", resp -> {
                    log.info("send http request to " + host);
                    log.debug("Got response " + resp.statusCode() + "; port: " + host);
                    resp.bodyHandler(body -> {
                        log.debug("Got data " + body.toString("ISO-8859-1"));
                    });
                });
                Thread.sleep(Constant.SEND_INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
