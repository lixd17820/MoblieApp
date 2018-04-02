package com.jwt.event;

/**
 * Created by lixd1 on 2017/12/23.
 */

public class ServiceEvent {

    public ServiceEvent(){

    }

    public ServiceEvent(int catalog){
        this.catalog = catalog;
    }

    public int catalog;
    //1:ok,0:err
    public boolean code;
}
