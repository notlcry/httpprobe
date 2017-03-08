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
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Client extends AbstractVerticle {

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

        vertx.createNetClient().connect(1234, "localhost", res -> {

            if (res.succeeded()) {
                NetSocket socket = res.result();

                RecordParser parser = RecordParser.newDelimited("\n", h -> handleMsg(h.toString(), socket));

                socket.handler(buffer -> {
                    System.out.println("Net client receiving: " + buffer.toString("UTF-8"));

                });

                socket.handler(parser);

                socket.write("online\n");
            } else {
                System.out.println("Failed to connect " + res.cause());
            }
        });
    }

    private void handleMsg(String s, NetSocket socket) {
        if (s.startsWith("{new")) {
            new Thread(() -> startProbe(s)).start();
        }
    }

    private void startProbe(String s) {
        while (true){
            vertx.createHttpClient().getNow(8080, "localhost", "/", resp -> {
                System.out.println("Got response " + resp.statusCode() + "; port: " + s);
                resp.bodyHandler(body -> {
                    System.out.println("Got data " + body.toString("ISO-8859-1"));
                });
            });
            try {
                Thread.sleep(10*1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
