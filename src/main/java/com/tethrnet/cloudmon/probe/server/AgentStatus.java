/*
 * Copyright 2002 - 2016, China UnionPay Co., Ltd. All right reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF CHINA UNIONPAY CO., LTD. THE CONTENTS OF THIS FILE MAY NOT BE
 * DISCLOSED TO THIRD PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART, WITHOUT THE PRIOR WRITTEN
 * PERMISSION OF CHINA UNIONPAY CO., LTD.
 */

package com.tethrnet.cloudmon.probe.server;

import io.vertx.core.net.NetSocket;

/**
 * Created by huiyu on 17/3/7.
 */
public class AgentStatus {
    private String agentHost;
    private String status;
    private NetSocket sock;

    public String getAgentHost() {
        return agentHost;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NetSocket getSock() {
        return sock;
    }

    public void setSock(NetSocket sock) {
        this.sock = sock;
    }
}
