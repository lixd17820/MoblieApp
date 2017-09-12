package com.jwt.pojo;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class VioFxcFileBean implements Serializable {

    @Id
    long id;
    private long fxcId;
    private String wjdz;
    private int scbj;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFxcId() {
        return fxcId;
    }

    public void setFxcId(long fxcId) {
        this.fxcId = fxcId;
    }

    public String getWjdz() {
        return wjdz;
    }

    public void setWjdz(String wjdz) {
        this.wjdz = wjdz;
    }

    public int getScbj() {
        return scbj;
    }

    public void setScbj(int scbj) {
        this.scbj = scbj;
    }

}
