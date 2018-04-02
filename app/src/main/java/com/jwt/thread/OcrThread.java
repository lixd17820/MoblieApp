package com.jwt.thread;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.jwt.event.OcrEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixd1 on 2017/12/26.
 */

public class OcrThread extends Thread {

    private Bitmap[] bitmaps;
    private String catalog;
    private boolean isSign;

    public OcrThread() {

    }

    public OcrThread(Bitmap[] bitmaps, String catalog, boolean isSign) {
        this.bitmaps = bitmaps;
        this.catalog = catalog;
        this.isSign = isSign;
    }

    @Override
    public void run() {
        List<String> list = new ArrayList<>();
        for (Bitmap bitmap : bitmaps) {
            list.add(simpleChineseOCR(bitmap, catalog, isSign));
        }
        EventBus.getDefault().post(new OcrEvent(list));
    }

    public String recText() {
        final TessBaseAPI baseApi = new TessBaseAPI();
        //初始化OCR的训练数据路径与语言
        //String lang = SFZH_LANGUAGE;
        //if ("drv".equals(catalog) || "veh".equals(catalog))
        //     lang = DRV_LANGUAGE;
        baseApi.init(TESSBASE_PATH, catalog);
        baseApi.setPageSegMode(isSign ? TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR : TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        String text = "";
        for (Bitmap bitmap : bitmaps) {
            baseApi.setImage(bitmap);
            String t = baseApi.getUTF8Text().toUpperCase();
            if (t != null)
                text += t.trim();
            baseApi.clear();
        }
        baseApi.end();
        return text;
    }


    //训练数据路径，必须包含tesseract文件夹
    private final String TESSBASE_PATH = "/storage/emulated/0/jwtdb/";
    //识别语言简体中文
    //private final String SFZH_LANGUAGE = "sfzh";
    //private final String DRV_LANGUAGE = "xinshi";

    //public String simpleChineseOCR(String file) {
    //    return "return: " + file;
    //}

    public String simpleChineseOCR(Bitmap bitmap, String catalog, boolean isSign) {
        String text = "";
        final TessBaseAPI baseApi = new TessBaseAPI();
        //初始化OCR的训练数据路径与语言
        //String lang = SFZH_LANGUAGE;
        //if ("drv".equals(catalog) || "veh".equals(catalog))
        //     lang = DRV_LANGUAGE;
        baseApi.init(TESSBASE_PATH, catalog);
        //设置识别模式
        baseApi.setPageSegMode(isSign ? TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR : TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        //设置要识别的图片
        baseApi.setImage(bitmap);
        text = baseApi.getUTF8Text().toUpperCase();
        baseApi.clear();
        baseApi.end();
        return text;
    }

    public String simpleChineseOCR(Bitmap bitmap, String catalog) {
        return simpleChineseOCR(bitmap, catalog, false);
    }
}
