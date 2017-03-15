package com.tethrnet.cloudmon.probe;

import io.netty.util.internal.ConcurrentSet;
import io.vertx.core.net.NetClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by huiyu on 17/3/14.
 */


public class MsgQueue {

    public static volatile MsgQueue INSTANCE = null;
    private BlockingQueue<String> msgQueue = new LinkedBlockingDeque<>();
    private ConcurrentSet<String> hosts = new ConcurrentSet();
    private NetClient netClient;

    public static MsgQueue getInstance() {
        if (INSTANCE == null) {
            synchronized (MsgQueue.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MsgQueue();
                }
            }
        }
        return INSTANCE;
    }

    private MsgQueue() {
    }


    public synchronized void put(String msg){
        try {
            msgQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  String take(){
        try {
            return msgQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String poll(){
        try {
            return msgQueue.poll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized List<String> getHosts() {
        List<String> allHost = new ArrayList<>();
        hosts.forEach(host->allHost.add(host));
        return allHost;
    }

    public synchronized void addHost(String host) {
        hosts.add(host);
    }

    public synchronized void removeHost(String host) {
        hosts.remove(host);
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }
}
