package com.tethrnet.cloudmon.probe;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by huiyu on 17/3/14.
 */
public class HttpClientRunner implements Runnable {

    protected Log log = LogFactory.getLog(HttpClientRunner.class);
    private Vertx vertx;
    private int interval = 100;
    private int count = 1;

    public HttpClientRunner(Vertx vertx, int intv, int cnt) {
        this.vertx = vertx;
        this.interval = intv;
        this.count = cnt;
    }

    @Override
    public void run() {
        vertx.setPeriodic(interval, id -> {

            MsgQueue.getInstance().getHosts().forEach((host) -> {
//                sendReq(host, Constant.HTTP_SERVER_PORT);
                startProbe(host, Constant.HTTP_SERVER_PORT);
            });
            log.debug("end a loop");
        });
    }

    private void startProbe(String host, int port) {
        try {
            HttpClientOptions options = new HttpClientOptions().setKeepAlive(true).setConnectTimeout(1000);
            HttpClient client = vertx.createHttpClient(options);

            for (int i = 0; i < count; i++) {
                client.getNow(port, host, "/", resp -> {
                    log.info("send http request to " + host);
                    log.debug("Got response " + resp.statusCode() + "; port: " + host);
                    resp.bodyHandler(body -> {
                        log.debug("got data " + body.toString("ISO-8859-1"));
                        client.close();
                    });
                });
            }

        } catch (Exception e) {
            log.error(e.getStackTrace());
            e.printStackTrace();
        }
    }

    public void sendReq(String host, int port) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://" + host + ":" + port);
        try {
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//
//    public void sendReqTest(String host, int port){
//        log.info("Send Req to " + host);
//    }
}
