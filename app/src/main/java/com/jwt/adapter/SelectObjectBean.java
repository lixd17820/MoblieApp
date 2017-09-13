package com.jwt.adapter;

/**
 * Created by lixiaodong on 2017/9/13.
 */

public class SelectObjectBean<E> {

    private boolean sel;
    private E bean;

    public SelectObjectBean() {
    }

    public SelectObjectBean(E bean) {
        this.bean = bean;
    }

    public SelectObjectBean(E bean, boolean sel) {
        this.bean = bean;
        this.sel = sel;
    }

    public boolean isSel() {
        return sel;
    }

    public void setSel(boolean sel) {
        this.sel = sel;
    }

    public E getBean() {
        return bean;
    }

    public void setBean(E bean) {
        this.bean = bean;
    }
}
