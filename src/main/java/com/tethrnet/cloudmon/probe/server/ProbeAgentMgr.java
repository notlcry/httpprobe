/*
 * Copyright 2002 - 2016, China UnionPay Co., Ltd. All right reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF CHINA UNIONPAY CO., LTD. THE CONTENTS OF THIS FILE MAY NOT BE
 * DISCLOSED TO THIRD PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART, WITHOUT THE PRIOR WRITTEN
 * PERMISSION OF CHINA UNIONPAY CO., LTD.
 */

package com.tethrnet.cloudmon.probe.server;

import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huiyu on 17/3/7.
 */

public class ProbeAgentMgr {

    private static ProbeAgentMgr INSTANCE;

    public static ProbeAgentMgr getInstance() {
        if (INSTANCE == null) {
            synchronized (ProbeAgentMgr.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProbeAgentMgr();
                }
            }
        }
        return INSTANCE;
    }

    private ProbeAgentMgr() {
    }

    protected Log log = LogFactory.getLog(ProbeAgentMgr.class);


    private ConcurrentHashMap<String, AgentStatus> agentStatusMap = new ConcurrentHashMap<>();

    public void updateStatus(String agentHost, AgentStatus status) {
        agentStatusMap.put(agentHost, status);
    }

    public synchronized void addAgent(NetSocket agentSock) {
        //broadcast rookie to older and add old to rookie
        agentStatusMap.forEach((k, agent) -> {
            agent.getSock().write(new JsonObject()
                    .put("action", "add")
                    .put("host", agentSock.remoteAddress().host())
                    .encode() + "\n");
            log.debug("add " + agentSock.remoteAddress().host() + " to " + agent.getSock().remoteAddress().host());
            //
            String msgFornew = new JsonObject()
                    .put("action", "add")
                    .put("host", agent.getSock().remoteAddress().host())
                    .encode() + "\n";
            agentSock.write(msgFornew);
            log.debug("add " + agent.getSock().remoteAddress().host() + " to " + agentSock.remoteAddress().host());
        });

        AgentStatus status = new AgentStatus();
        status.setAgentHost(agentSock.remoteAddress().host());
        status.setStatus(AgentConstant.STST_INIT);
        status.setSock(agentSock);
        agentStatusMap.put(agentSock.remoteAddress().host(), status);
    }

    public synchronized void removeAgent(String host) {
        agentStatusMap.remove(host);
        //broadcast to all agents
        agentStatusMap.forEach((k, agent) -> {
            if (agent.getSock() != null) {
                agent.getSock().write(new JsonObject()
                        .put("action", "remove")
                        .put("host", host)
                        .encode() + "\n");
            }
            log.debug("removed " + host);
        });

    }


    public synchronized void updateAgent(String host,String status) {
        agentStatusMap.get(host).setStatus(status);
    }

    public List<JsonObject> listAgentStatus() {

        List<JsonObject> probeAgentStatusList = new ArrayList<>();
        agentStatusMap.values().forEach(probe -> {
            probeAgentStatusList.add(new JsonObject().put("host", probe.getAgentHost()).put("status", probe.getStatus
                    ()));
        });

        return probeAgentStatusList;
    }

    public JsonObject getAgentStatus(String host) {

        AgentStatus agentstatus = agentStatusMap.get(host);
        JsonObject agent = new JsonObject().put("host",agentstatus.getAgentHost()).put("status",agentstatus.getStatus());

        return agent;
    }

}
