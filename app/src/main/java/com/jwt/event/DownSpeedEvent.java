package com.jwt.event;

import com.jwt.pojo.FrmCode;

import java.util.List;

/**
 * Created by lixiaodong on 2017/8/26.
 */

public class DownSpeedEvent {

    private int total;
    private int step;
    private String currentName;
    private String title;


    public DownSpeedEvent(String title, int total, int step, String currentName) {
        this.title = title;
        this.total = total;
        this.step = step;
        this.currentName = currentName;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }


}
