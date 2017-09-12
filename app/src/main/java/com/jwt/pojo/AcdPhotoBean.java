package com.jwt.pojo;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class AcdPhotoBean implements Serializable {

    @Id
    long id;
    private String sgsj;
    private String sgdddm;
    private String sgdd;
    private String sgbh;
    private String xtbh;
    private String photo;
    private int scbj;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSgsj() {
        return sgsj;
    }

    public void setSgsj(String sgsj) {
        this.sgsj = sgsj;
    }

    public String getSgdddm() {
        return sgdddm;
    }

    public void setSgdddm(String sgdddm) {
        this.sgdddm = sgdddm;
    }

    public String getSgdd() {
        return sgdd;
    }

    public void setSgdd(String sgdd) {
        this.sgdd = sgdd;
    }

    public String getSgbh() {
        return sgbh;
    }

    public void setSgbh(String sgbh) {
        this.sgbh = sgbh;
    }

    public String getXtbh() {
        return xtbh;
    }

    public void setXtbh(String xtbh) {
        this.xtbh = xtbh;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getScbj() {
        return scbj;
    }

    public void setScbj(int scbj) {
        this.scbj = scbj;
    }

}
