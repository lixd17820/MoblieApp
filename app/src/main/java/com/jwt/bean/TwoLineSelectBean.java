package com.jwt.bean;

public class TwoLineSelectBean {
    private String text1;
    private String text2;
    private boolean isSelect;

    public TwoLineSelectBean() {
        isSelect = false;
    }

    public TwoLineSelectBean(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
        this.isSelect = false;
    }

    public TwoLineSelectBean(String text1, String text2, boolean isSelect) {
        this.text1 = text1;
        this.text2 = text2;
        this.isSelect = isSelect;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

}
