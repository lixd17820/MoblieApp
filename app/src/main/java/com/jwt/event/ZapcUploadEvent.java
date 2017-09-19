package com.jwt.event;

/**
 * Created by lixiaodong on 2017/9/19.
 */

public class ZapcUploadEvent {

    private int total;
    private int step;
    private int error;
    private String message;
    private boolean isDone;
    private int catalog = 10000;

    public ZapcUploadEvent() {

    }

    public ZapcUploadEvent(int total, int step) {
        this.total = total;
        this.step = step;
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

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }
}
