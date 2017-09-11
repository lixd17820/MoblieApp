package com.jwt.jbyw;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VioVehBean implements Serializable {

    private String hpzl;

    private String hphm;

    private String cllx;

    private String bdxx;

    private String hdzzl;


    public String getHpzl() {
        return hpzl;
    }

    public void setHpzl(String hpzl) {
        this.hpzl = hpzl;
    }

    public String getHphm() {
        return hphm;
    }

    public void setHphm(String hphm) {
        this.hphm = hphm;
    }

    public String getCllx() {
        return cllx;
    }

    public void setCllx(String cllx) {
        this.cllx = cllx;
    }

    public String getBdxx() {
        return bdxx;
    }

    public void setBdxx(String bdxx) {
        this.bdxx = bdxx;
    }

    public String getHdzzl() {
        return hdzzl;
    }

    public void setHdzzl(String hdzzl) {
        this.hdzzl = hdzzl;
    }
}
