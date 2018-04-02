package com.jwt.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.googlecode.tesseract.android.TessBaseAPI;

public class TestActivity extends AppCompatActivity {

    private final String outPath = "/storage/emulated/0/jwtdb/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String text = "";
        //final TessBaseAPI baseApi = new TessBaseAPI();
        //初始化OCR的训练数据路径与语言
        //String lang = SFZH_LANGUAGE;
        //if ("drv".equals(catalog) || "veh".equals(catalog))
        //     lang = DRV_LANGUAGE;
        //baseApi.init(outPath, "sfzh");
        //设置识别模式
        //baseApi.setPageSegMode(isSign ? 10 : 7);
    }
}
