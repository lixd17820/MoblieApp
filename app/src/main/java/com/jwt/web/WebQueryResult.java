package com.jwt.web;

import java.io.Serializable;

/**
 * 本类用与WEB查询返回的结果 <br>
 *
 * @author lenovo
 */
public class WebQueryResult<E> implements Serializable {
    /**
     *
     */
    private int status;
    private E result;
    private String stMs;

    public WebQueryResult() {
    }

    public WebQueryResult(int status, E result) {
        this.status = status;
        this.result = result;
    }

    public WebQueryResult(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public E getResult() {
        return result;
    }

    public void setResult(E result) {
        this.result = result;
    }

    public String getStMs() {
        return stMs;
    }

    public void setStMs(String stMs) {
        this.stMs = stMs;
    }
}
