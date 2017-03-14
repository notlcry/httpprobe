package com.tethrnet.cloudmon.probe;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by huiyu on 17/3/14.
 */


public class MsgQueue {

    public static volatile MsgQueue INSTANCE = null;
    private BlockingQueue<String> msgQueue = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<String,Integer> hostMap = new ConcurrentHashMap();

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


    public void put(String msg){
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

    public ConcurrentHashMap<String, Integer> getHostMap() {
        return hostMap;
    }
}
