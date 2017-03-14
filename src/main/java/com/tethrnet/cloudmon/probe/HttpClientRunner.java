package com.tethrnet.cloudmon.probe;

import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
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
//    private HttpClient client;

    public HttpClientRunner(Vertx vertx) {
        this.vertx = vertx;
//        client = this.vertx.createHttpClient();
    }

    @Override
    public void run() {

        while (true) {
            try {
                MsgQueue.getInstance().getHostMap().forEach((host, v) -> {
//                    sendReq(host, Constant.HTTP_SERVER_PORT);
                    sendReqTest(host, Constant.HTTP_SERVER_PORT);
                });

                Thread.sleep(Constant.SEND_INTERVAL);
                log.info("send http end");
            } catch (Exception e) {

            }
        }
    }

//    private void startProbe(String host, int port) {
//        try {
//            client.getNow(port, host, "/", resp -> {
//                log.info("send http request to " + host);
//                log.debug("Got response " + resp.statusCode() + "; port: " + host);
//                resp.bodyHandler(body -> {
//                    log.debug("got data " + body.toString("ISO-8859-1"));
//                });
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void sendReq(String host, int port){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://" + host + ":"+ port);
        try {
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendReqTest(String host, int port){
        log.info("Send Req to " + host);
    }
}
