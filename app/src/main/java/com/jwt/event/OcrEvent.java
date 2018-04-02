package com.jwt.event;

import java.util.List;

/**
 * Created by lixd1 on 2017/12/26.
 */

public class OcrEvent {

    public List<String> ocrTexts;

    public String txt;

    public OcrEvent(List<String> ocrTexts) {
        this.ocrTexts = ocrTexts;
    }

    public OcrEvent(String txt) {
        this.txt = txt;
    }

    public OcrEvent() {

    }

    public boolean isOk = false;
}
