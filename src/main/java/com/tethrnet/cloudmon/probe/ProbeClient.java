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


public class ProbeClient extends AbstractVerticle {

    private final String clientIp;
    protected Log log = LogFactory.getLog(ProbeClient.class);


    private NetClient client;
    private String probeServer;

    public ProbeClient(Vertx v, String pbs, String clientIp) {
        vertx = v;
        this.probeServer = pbs;
        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000).setTcpKeepAlive(true)
                .setLogActivity(true).setReconnectAttempts(86400).setReconnectInterval(10 * 1000l).setConnectTimeout
                        (1000);
        client = vertx.createNetClient(options);
        this.clientIp = clientIp;
    }

    public static void main(String[] args) {
        Vertx v = Vertx.vertx();
        ProbeClient client = new ProbeClient(v,"192.168.56.1","127.0.0.1");
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {

        client.connect(Constant.PROBE_SERVER_PORT, probeServer, (AsyncResult<NetSocket> res) -> {

            if (res.succeeded()) {
                log.info("Connected to ProbeServer");
                NetSocket socket = res.result();

                RecordParser parser = RecordParser.newDelimited("\n", h -> handleMsg(h.toString(), socket));

//                socket.handler(buffer -> {
//                    log.debug("Net client receiving: " + buffer.toString("UTF-8"));
//
//                });

                startHB(socket);

                socket.handler(parser);

                socket.write("online-" + clientIp + "\n");

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
        vertx.setPeriodic(Constant.HB_INTERVAL, id -> socket.write("alive\n"));
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
