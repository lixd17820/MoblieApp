package com.jwt.event;

import com.jwt.utils.ConnCata;

import java.util.Set;

/**
 * Created by lixiaodong on 2017/9/20.
 */

public class MqttEvent {

    private int catalog;
    private Set<String> topics;
    private ConnCata conn;
    private String url;

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public ConnCata getConn() {
        return conn;
    }

    public void setConn(ConnCata conn) {
        this.conn = conn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
