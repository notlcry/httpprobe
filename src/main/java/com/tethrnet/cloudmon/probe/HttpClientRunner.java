package com.tethrnet.cloudmon.probe;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by huiyu on 17/3/14.
 */
public class HttpClientRunner implements Runnable {

    protected Log log = LogFactory.getLog(HttpClientRunner.class);
    private Vertx vertx;
    private HttpClient client;

    public HttpClientRunner(Vertx vertx) {
        this.vertx = vertx;
        client = this.vertx.createHttpClient();
    }

    @Override
    public void run() {

        while (true) {
            try {
                MsgQueue.getInstance().getHostMap().forEach((host, v) -> {
                    startProbe(host, Constant.HTTP_SERVER_PORT);
                });
                Thread.sleep(Constant.SEND_INTERVAL);
                log.info("send http end");
            } catch (Exception e) {

            }
        }
    }

    private void startProbe(String host, int port) {
        try {
            client.getNow(port, host, "/", resp -> {
                log.info("send http request to " + host);
                log.debug("Got response " + resp.statusCode() + "; port: " + host);
                resp.bodyHandler(body -> {
                    log.debug("got data " + body.toString("ISO-8859-1"));
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
