package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 *
 * Created by on 2015/6/24.
 */
@Entity
public class SeriousStreetBean {
    @Id
    long id;
    private String wfdd;
    private String nwfxw;
    private String version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNwfxw() {
        return nwfxw;
    }

    public void setNwfxw(String nwfxw) {
        this.nwfxw = nwfxw;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWfdd() {
        return wfdd;
    }

    public void setWfdd(String wfdd) {
        this.wfdd = wfdd;
    }
}
