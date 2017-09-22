package com.jwt.event;

import com.jwt.bean.LoginResultBean;

/**
 * Created by lixiaodong on 2017/8/25.
 */

public class LoginEvent {
    private int status;
    private LoginResultBean result;
    private String stMs;
    private String cxMenuStr;

    public LoginEvent(){
    }

    public LoginEvent(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LoginResultBean getResult() {
        return result;
    }

    public void setResult(LoginResultBean result) {
        this.result = result;
    }

    public String getStMs() {
        return stMs;
    }

    public void setStMs(String stMs) {
        this.stMs = stMs;
    }

    public String getCxMenuStr() {
        return cxMenuStr;
    }

    public void setCxMenuStr(String cxMenuStr) {
        this.cxMenuStr = cxMenuStr;
    }
}
