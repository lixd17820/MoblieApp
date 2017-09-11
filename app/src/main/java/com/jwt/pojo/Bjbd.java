package com.jwt.pojo;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/30.
 */
@Entity
public class Bjbd {
    @Id
    long id;
    private String ddsj;
    private String hpzl;
    private String hphm;
    private String cbzId;
    private String cbz;
    private String xsfx;
    private String bjyy;
    private String yjbm;
    private String bksj;
    private String bkmj;
    private String tplj;
    private String sender;
    private String type;
    private Date jssj;
    private long ydbj;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDdsj() {
        return ddsj;
    }

    public void setDdsj(String ddsj) {
        this.ddsj = ddsj;
    }

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

    public String getCbzId() {
        return cbzId;
    }

    public void setCbzId(String cbzId) {
        this.cbzId = cbzId;
    }

    public String getCbz() {
        return cbz;
    }

    public void setCbz(String cbz) {
        this.cbz = cbz;
    }

    public String getXsfx() {
        return xsfx;
    }

    public void setXsfx(String xsfx) {
        this.xsfx = xsfx;
    }

    public String getBjyy() {
        return bjyy;
    }

    public void setBjyy(String bjyy) {
        this.bjyy = bjyy;
    }

    public String getYjbm() {
        return yjbm;
    }

    public void setYjbm(String yjbm) {
        this.yjbm = yjbm;
    }

    public String getBksj() {
        return bksj;
    }

    public void setBksj(String bksj) {
        this.bksj = bksj;
    }

    public String getBkmj() {
        return bkmj;
    }

    public void setBkmj(String bkmj) {
        this.bkmj = bkmj;
    }

    public String getTplj() {
        return tplj;
    }

    public void setTplj(String tplj) {
        this.tplj = tplj;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getJssj() {
        return jssj;
    }

    public void setJssj(Date jssj) {
        this.jssj = jssj;
    }

    public long getYdbj() {
        return ydbj;
    }

    public void setYdbj(long ydbj) {
        this.ydbj = ydbj;
    }
}
