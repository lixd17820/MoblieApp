package com.jwt.bean;

import java.io.Serializable;

public class UpdateFile implements Serializable {
    private String id;
    private String fileName;
    private String packageName;
    private String version;
    private String versionName;
    private String hashValue;

    public String getFileName() {
        return fileName;
    }

    public String getHashValue() {
        return hashValue;
    }

    public String getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
