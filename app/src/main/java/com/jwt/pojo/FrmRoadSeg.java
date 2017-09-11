package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/26.
 */
@Entity
public class FrmRoadSeg {
    @Id
    long id;
    String xzqh;
    String dldm;
    String lddm;
    String ldmc;
    String qsms;
    String jsms;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getXzqh() {
        return xzqh;
    }

    public void setXzqh(String xzqh) {
        this.xzqh = xzqh;
    }

    public String getDldm() {
        return dldm;
    }

    public void setDldm(String dldm) {
        this.dldm = dldm;
    }

    public String getLddm() {
        return lddm;
    }

    public void setLddm(String lddm) {
        this.lddm = lddm;
    }

    public String getLdmc() {
        return ldmc;
    }

    public void setLdmc(String ldmc) {
        this.ldmc = ldmc;
    }

    public String getQsms() {
        return qsms;
    }

    public void setQsms(String qsms) {
        this.qsms = qsms;
    }

    public String getJsms() {
        return jsms;
    }

    public void setJsms(String jsms) {
        this.jsms = jsms;
    }
}
