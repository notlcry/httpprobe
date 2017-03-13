/*
 * Copyright 2002 - 2016, China UnionPay Co., Ltd. All right reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF CHINA UNIONPAY CO., LTD. THE CONTENTS OF THIS FILE MAY NOT BE
 * DISCLOSED TO THIRD PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART, WITHOUT THE PRIOR WRITTEN
 * PERMISSION OF CHINA UNIONPAY CO., LTD.
 */

package com.tethrnet.cloudmon.probe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Client extends AbstractVerticle {

    protected Log log = LogFactory.getLog(Client.class);

    public Client() {
        vertx = Vertx.vertx();
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {

        vertx.createNetClient().connect(Constant.PROBE_SERVER_PORT, "192.168.56.1", res -> {

            if (res.succeeded()) {
                NetSocket socket = res.result();

                RecordParser parser = RecordParser.newDelimited("\n", h -> handleMsg(h.toString(), socket));

                socket.handler(buffer -> {
                    log.debug("Net client receiving: " + buffer.toString("UTF-8"));

                });

                socket.handler(parser);

                socket.write("online\n");
            } else {
                log.warn("Failed to connect " + res.cause());
            }
        });
    }

    private void handleMsg(String s, NetSocket socket) {
        JsonObject msg = new JsonObject(s);
        String action = msg.getString("action");
        String host = msg.getString("host");

        if (action.equals("add")) {
            new Thread(() -> startProbe(host, Constant.HTTP_SERVER_PORT)).start();
        }
    }

    private void startProbe(String host, int port) {
        while (true) {
            vertx.createHttpClient().getNow(port, host, "/", resp -> {
                log.info("send http request to "+ host);
                log.debug("Got response " + resp.statusCode() + "; port: " + host);
                resp.bodyHandler(body -> {
                    log.debug("Got data " + body.toString("ISO-8859-1"));
                });
            });
            try {
                Thread.sleep(Constant.SEND_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
