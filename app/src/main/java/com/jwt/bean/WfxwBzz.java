package com.jwt.bean;

/**
 * Created by lixiaodong on 2017/9/5.
 */

public class WfxwBzz {

    private String wfxw;
    private String bzz;
    private String scz;
    private int select;

    public WfxwBzz(String wfxw, String bzz, String scz) {
        this.wfxw = wfxw;
        this.bzz = bzz;
        this.scz = scz;
    }

    public WfxwBzz() {

    }

    public String getWfxw() {
        return wfxw;
    }

    public void setWfxw(String wfxw) {
        this.wfxw = wfxw;
    }

    public String getBzz() {
        return bzz;
    }

    public void setBzz(String bzz) {
        this.bzz = bzz;
    }

    public String getScz() {
        return scz;
    }

    public void setScz(String scz) {
        this.scz = scz;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        select = select;
    }
}
