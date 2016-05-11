package com.razikallayi.suraksha;

/**
 * Created by Razi Kallayi on 11-05-2016.
 */
public class Session {
    private static Session mInstance=null;
    private long lastActiveTime;

    public Session() {
    }

    public static Session getInstance(){
        if(mInstance==null){
            mInstance = new Session();
        }
        return mInstance;
    }

    public boolean exist(){
        return System.currentTimeMillis() - lastActiveTime > 0;
    }

    public void extend(){
        if(exist()){
            
        }
    }


    public void suspend(){
        lastActiveTime = System.currentTimeMillis();
    }


}
