package com.jwt.event;

import com.jwt.pojo.AcdSimpleBean;

/**
 * Created by lixiaodong on 2017/9/19.
 */

public class AcdUploadEvent {

    private AcdSimpleBean acd;
    private int scbj;
    private String message;

    public AcdSimpleBean getAcd() {
        return acd;
    }

    public void setAcd(AcdSimpleBean acd) {
        this.acd = acd;
    }

    public int getScbj() {
        return scbj;
    }

    public void setScbj(int scbj) {
        this.scbj = scbj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
