package com.jwt.event;

/**
 * Created by lixiaodong on 2017/8/29.
 */

public class CommEvent {

    private int status;
    private String message;

    public CommEvent(){

    }

    public CommEvent(int status,String message){
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
