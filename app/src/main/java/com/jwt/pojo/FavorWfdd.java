package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class FavorWfdd {

    @Id
    long id;
    private String xzqh;
    private String dldm;
    private String lddm;
    private String ms;
    private String sysLdmc;
    private String favorLdmc;
    private String yxbj;

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

    public String getMs() {
        return ms;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }

    public String getSysLdmc() {
        return sysLdmc;
    }

    public void setSysLdmc(String sysLdmc) {
        this.sysLdmc = sysLdmc;
    }

    public String getFavorLdmc() {
        return favorLdmc;
    }

    public void setFavorLdmc(String favorLdmc) {
        this.favorLdmc = favorLdmc;
    }

    public String getYxbj() {
        return yxbj;
    }

    public void setYxbj(String yxbj) {
        this.yxbj = yxbj;
    }

}
