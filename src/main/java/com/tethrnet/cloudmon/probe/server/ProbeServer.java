/*
 * Copyright 2002 - 2016, China UnionPay Co., Ltd. All right reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF CHINA UNIONPAY CO., LTD. THE CONTENTS OF THIS FILE MAY NOT BE
 * DISCLOSED TO THIRD PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART, WITHOUT THE PRIOR WRITTEN
 * PERMISSION OF CHINA UNIONPAY CO., LTD.
 */

package com.tethrnet.cloudmon.probe.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProbeServer extends AbstractVerticle {

    protected Log log = LogFactory.getLog(ProbeServer.class);

    private ProbeAgentMgr agentMgr;

    public ProbeServer() {
        agentMgr = ProbeAgentMgr.getInstance();
    }

    public static void main(String[] args) {

        ProbeServer server = new ProbeServer();
        RESTServer restServer = new RESTServer();
        try {
            server.start();
            restServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        vertx = Vertx.vertx();
        NetServerOptions options = new NetServerOptions().setLogActivity(true);
        NetServer server = vertx.createNetServer(options);

        server.connectHandler(sock -> {
            log.info("Incoming connection! from " + sock.remoteAddress().host());
            sock.handler(buffer -> {
                log.debug("receive new msg from " + sock.remoteAddress().host() + ": " + buffer);
            });
            RecordParser parser = RecordParser.newDelimited("\n", h -> handleAgent(h.toString(), sock));
            String ip = sock.remoteAddress().host();
            sock.handler(parser);
            sock.closeHandler(v -> {
                log.info(ip + "is disconnect");
                agentMgr.removeAgent(sock.remoteAddress().host());
            });
            sock.exceptionHandler(r -> r.printStackTrace());

        }).listen(21234);
        log.info("Echo server is now listening");
    }

    private void handleAgent(String record, NetSocket sock) {
        log.debug("receive record: " + record);
        if (record.equals("online")) {
            agentMgr.addAgent(sock);
            log.debug("Add host + " + sock.remoteAddress().host() + " to AgentMgr");
        } else if (record.equals("alive")) {
            agentMgr.updateAgent(sock.remoteAddress().host(), AgentConstant.STAT_RUNNING);
            log.debug("Add host + " + sock.remoteAddress().host() + " to AgentMgr");
        } else {
            log.warn("receive unknown record: " + record);
        }
    }
}
