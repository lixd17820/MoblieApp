package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class JtssLightPic {
    @Id
    long id;
    String crossId;
    String lightId;
    String pic;
    int scbj;

    public long getId() {
        return id;
    }

    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getScbj() {
        return scbj;
    }

    public void setScbj(int scbj) {
        this.scbj = scbj;
    }

    @Override
    public String toString() {
        return lightId + "/" + pic + "/" + scbj;
    }
}
