/*
 * Copyright 2002 - 2016, China UnionPay Co., Ltd. All right reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF CHINA UNIONPAY CO., LTD. THE CONTENTS OF THIS FILE MAY NOT BE
 * DISCLOSED TO THIRD PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART, WITHOUT THE PRIOR WRITTEN
 * PERMISSION OF CHINA UNIONPAY CO., LTD.
 */

package com.tethrnet.cloudmon.probe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ProbeClient extends AbstractVerticle {

    protected Log log = LogFactory.getLog(ProbeClient.class);


    private NetClient client;

    public ProbeClient(Vertx v) {
        vertx = v;

        NetClientOptions options = new NetClientOptions().setConnectTimeout(1).setTcpKeepAlive(true);
//                .setLogActivity(true).setReconnectAttempts(2).setReconnectInterval(1 * 1000l).setConnectTimeout
//                        (1000);
        client = vertx.createNetClient(options);
    }

    public static void main(String[] args) {
        Vertx v = Vertx.vertx();
        ProbeClient client = new ProbeClient(v);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {

        client.connect(Constant.PROBE_SERVER_PORT, "192.168.56.1", (AsyncResult<NetSocket> res) -> {

            if (res.succeeded()) {
                NetSocket socket = res.result();

                RecordParser parser = RecordParser.newDelimited("\n", h -> handleMsg(h.toString(), socket));

//                socket.handler(buffer -> {
//                    log.debug("Net client receiving: " + buffer.toString("UTF-8"));
//
//                });

                startHB(socket);

                socket.handler(parser);

                socket.write("online\n");

                socket.closeHandler(r -> {
                    log.info("Socket closed");
                    reconnect();
                });

                socket.exceptionHandler(r -> r.printStackTrace());

            } else {
                log.warn("Failed to connect " + res.cause());
                reconnect();
            }
        });
    }

    private void reconnect() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHB(NetSocket socket) {
        vertx.setPeriodic(Constant.SEND_INTERVAL, id -> socket.write("I am alive\n"));
    }

    private void handleMsg(String source, NetSocket socket) {
        log.info("handle msg: " + source);
        MsgQueue.getInstance().put(source);
        try {
//            JsonObject msg = new JsonObject(s);
//            String action = msg.getString("action");
//            String host = msg.getString("host");

//            Gson gson = new Gson();
//            HashMap map = gson.fromJson(source, HashMap.class);
//            String action = map.get("action").toString();
//            String host = map.get("host").toString();
//
//            if (action.equals("add")) {
//                new Thread(() -> startProbe(host, Constant.HTTP_SERVER_PORT)).start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NetClient getClient() {
        return client;
    }
}
