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
public class Client extends AbstractVerticle {

    protected Log log = LogFactory.getLog(Client.class);

    public Client(Vertx v) {
        vertx = v;
    }

    public static void main(String[] args) {
        Vertx v = Vertx.vertx();
        Client client = new Client(v);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {

        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000).setTcpKeepAlive(true)
                .setLogActivity(true);

        NetClient client = vertx.createNetClient(options);

        client.connect(Constant.PROBE_SERVER_PORT, "192.168.56.1", (AsyncResult<NetSocket> res) -> {

            if (res.succeeded()) {
                NetSocket socket = res.result();

                RecordParser parser = RecordParser.newDelimited("\n", h -> handleMsg(h.toString(), socket));

//                socket.handler(buffer -> {
//                    log.debug("Net client receiving: " + buffer.toString("UTF-8"));
//
//                });

                socket.handler(parser);

                socket.write("online\n");

//                socket.closeHandler(r -> log.info("Socket closed"));
//                socket.close();
                socket.exceptionHandler(r -> r.printStackTrace());
            } else {
                log.warn("Failed to connect " + res.cause());
            }
        });
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

}
