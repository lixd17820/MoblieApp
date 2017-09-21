package com.jwt.event;

/**
 * Created by lixiaodong on 2017/9/21.
 */

public class DownApkEvent {
    public boolean isOver;
    public int step;
    public String filename;


    public DownApkEvent(boolean isOver, int step, String fn) {
        this.step = step;
        this.isOver = isOver;
        this.filename = fn;
    }
}
