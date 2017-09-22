package com.jwt.pojo;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/9/22.
 */
@Entity
public class AcdLawBean implements Serializable{
    @Id
    long id;
    private String xh;
    private String flmc;
    private String tkmc;
    private String tknr;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getFlmc() {
        return flmc;
    }

    public void setFlmc(String flmc) {
        this.flmc = flmc;
    }

    public String getTkmc() {
        return tkmc;
    }

    public void setTkmc(String tkmc) {
        this.tkmc = tkmc;
    }

    public String getTknr() {
        return tknr;
    }

    public void setTknr(String tknr) {
        this.tknr = tknr;
    }
}
