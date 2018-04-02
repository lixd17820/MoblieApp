package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class JtssParam {
    @Id
    long id;
    String type;
    String code;
    String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type + "/" + code + "/" + value;
    }
}
