package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/26.
 */
@Entity
public class FrmRoadItem {

    @Id
    long id;
    String dldm;
    String xzqh;
    String dlmc;
    String dllx;
    String xzqhxxlc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDldm() {
        return dldm;
    }

    public void setDldm(String dldm) {
        this.dldm = dldm;
    }

    public String getXzqh() {
        return xzqh;
    }

    public void setXzqh(String xzqh) {
        this.xzqh = xzqh;
    }

    public String getDlmc() {
        return dlmc;
    }

    public void setDlmc(String dlmc) {
        this.dlmc = dlmc;
    }

    public String getDllx() {
        return dllx;
    }

    public void setDllx(String dllx) {
        this.dllx = dllx;
    }

    public String getXzqhxxlc() {
        return xzqhxxlc;
    }

    public void setXzqhxxlc(String xzqhxxlc) {
        this.xzqhxxlc = xzqhxxlc;
    }
}
