package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/9/13.
 */

@Entity
public class ZapcLxxx {
    @Id
    long id;
    String xldm;
    String xlxl;
    String trffbh;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getXldm() {
        return xldm;
    }

    public void setXldm(String xldm) {
        this.xldm = xldm;
    }

    public String getXlxl() {
        return xlxl;
    }

    public void setXlxl(String xlxl) {
        this.xlxl = xlxl;
    }

    public String getTrffbh() {
        return trffbh;
    }

    public void setTrffbh(String trffbh) {
        this.trffbh = trffbh;
    }
}
