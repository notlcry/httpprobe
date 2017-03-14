package com.tethrnet.cloudmon.probe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by huiyu on 17/3/14.
 */


public class MsgQueue {

    public static volatile MsgQueue INSTANCE = null;
    private BlockingQueue<String> msgQueue = new LinkedBlockingDeque<>();

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


}
