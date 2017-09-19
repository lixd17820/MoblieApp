package com.jwt.event;

/**
 * Created by lixiaodong on 2017/9/19.
 */

public class FxcUploadEvent {
    public int total;
    public int step;
    public long[] ids;
    public String message;
    public boolean isDone = false;
    public int err;

    public FxcUploadEvent(){

    }

    public FxcUploadEvent(int total,int step){
        this.total = total;
        this.step = step;
    }
}
