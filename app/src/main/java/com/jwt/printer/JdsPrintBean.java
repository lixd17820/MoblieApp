package com.jwt.printer;

import android.view.Gravity;

import java.io.Serializable;


public class JdsPrintBean implements Serializable {
    private int alignMode;
    private String content;

    public JdsPrintBean() {
    }

    public JdsPrintBean(String content) {
        this.alignMode = Gravity.LEFT;
        this.content = content;
    }

    public JdsPrintBean(int alignMode, String content) {
        this.alignMode = alignMode;
        this.content = content;
    }

    public int getAlignMode() {
        return alignMode;
    }

    public void setAlignMode(int alignMode) {
        this.alignMode = alignMode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return alignMode + "/" + content;
    }
}
