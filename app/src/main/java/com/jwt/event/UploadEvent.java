package com.jwt.event;

/**
 * Created by lixiaodong on 2017/9/18.
 */

public class UploadEvent {
    public long id;
    public boolean status;
    public String message;

    public UploadEvent(long id, boolean status,String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }

}
