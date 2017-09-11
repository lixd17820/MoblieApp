package com.jwt.pojo;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/26.
 */
@Entity
public class VioWfdmCode {
    @Id
    long id;
    String wfxw;
    String dmzl;
    String dmfl;
    String wfms;
    String wfnr;
    String wfgd;
    String fltw;
    int wfjfs;
    int fkjeDut;
    String qzcslx;
    String jgbj;
    String fkbj;
    String zkbj;
    String dxbj;
    String jlbj;
    String cxvbj;
    Date yxqs;
    Date yxqz;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWfxw() {
        return wfxw;
    }

    public void setWfxw(String wfxw) {
        this.wfxw = wfxw;
    }

    public String getDmzl() {
        return dmzl;
    }

    public void setDmzl(String dmzl) {
        this.dmzl = dmzl;
    }

    public String getDmfl() {
        return dmfl;
    }

    public void setDmfl(String dmfl) {
        this.dmfl = dmfl;
    }

    public String getWfms() {
        return wfms;
    }

    public void setWfms(String wfms) {
        this.wfms = wfms;
    }

    public String getWfnr() {
        return wfnr;
    }

    public void setWfnr(String wfnr) {
        this.wfnr = wfnr;
    }

    public String getWfgd() {
        return wfgd;
    }

    public void setWfgd(String wfgd) {
        this.wfgd = wfgd;
    }

    public String getFltw() {
        return fltw;
    }

    public void setFltw(String fltw) {
        this.fltw = fltw;
    }

    public int getWfjfs() {
        return wfjfs;
    }

    public void setWfjfs(int wfjfs) {
        this.wfjfs = wfjfs;
    }

    public int getFkjeDut() {
        return fkjeDut;
    }

    public void setFkjeDut(int fkjeDut) {
        this.fkjeDut = fkjeDut;
    }

    public String getQzcslx() {
        return qzcslx;
    }

    public void setQzcslx(String qzcslx) {
        this.qzcslx = qzcslx;
    }

    public String getJgbj() {
        return jgbj;
    }

    public void setJgbj(String jgbj) {
        this.jgbj = jgbj;
    }

    public String getFkbj() {
        return fkbj;
    }

    public void setFkbj(String fkbj) {
        this.fkbj = fkbj;
    }

    public String getZkbj() {
        return zkbj;
    }

    public void setZkbj(String zkbj) {
        this.zkbj = zkbj;
    }

    public String getDxbj() {
        return dxbj;
    }

    public void setDxbj(String dxbj) {
        this.dxbj = dxbj;
    }

    public String getJlbj() {
        return jlbj;
    }

    public void setJlbj(String jlbj) {
        this.jlbj = jlbj;
    }

    public String getCxvbj() {
        return cxvbj;
    }

    public void setCxvbj(String cxvbj) {
        this.cxvbj = cxvbj;
    }

    public Date getYxqs() {
        return yxqs;
    }

    public void setYxqs(Date yxqs) {
        this.yxqs = yxqs;
    }

    public Date getYxqz() {
        return yxqz;
    }

    public void setYxqz(Date yxqz) {
        this.yxqz = yxqz;
    }
}
