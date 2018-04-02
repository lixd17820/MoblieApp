package com.jwt.adapter;

/**
 * Created by lixiaodong on 2017/9/13.
 */

public class SelectObjectBean<T> {

    private boolean sel;
    private T bean;
    private String text;

    public SelectObjectBean() {
    }

    public SelectObjectBean(T bean) {
        this.bean = bean;
    }

    public SelectObjectBean(T bean, boolean sel) {
        this.bean = bean;
        this.sel = sel;
    }

    public boolean isSel() {
        return sel;
    }

    public void setSel(boolean sel) {
        this.sel = sel;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
