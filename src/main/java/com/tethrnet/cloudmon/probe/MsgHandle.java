package com.tethrnet.cloudmon.probe;

import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by huiyu on 17/3/14.
 */
public class MsgHandle implements Runnable{

    protected Log log = LogFactory.getLog(MsgHandle.class);
    private Vertx vertx;


    @Override
    public void run() {

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
                }).close();
                Thread.sleep(Constant.SEND_INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
