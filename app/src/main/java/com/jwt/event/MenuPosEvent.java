package com.jwt.event;

/**
 * Created by lixiaodong on 2017/9/15.
 */

public class MenuPosEvent {
    private int pos;
    private boolean isBadge;
    private String message;

    public MenuPosEvent(){

    }

    public MenuPosEvent(int pos){
        this.pos = pos;
        isBadge = false;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isBadge() {
        return isBadge;
    }

    public void setBadge(boolean badge) {
        isBadge = badge;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
