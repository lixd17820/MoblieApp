package com.jwt.web;

import android.util.Log;

/**
 * 外网
 */
public class OutRestfulDao extends RestfulDao {

    public OutRestfulDao() {
        Log.e("OutRestfulDao", "OutRestfulDao create");
    }

    @Override
    public String getUrl() {
        return "http://www.ntjxj.com";
    }

    @Override
    public String getPicUrl() {
        return getUrl() + PIC_URL;
    }

    @Override
    public String getFileUrl() {
        return getUrl() + "/ydjw/DownloadFile?pack=";
    }

    @Override
    public String getJqtbFileUrl() {
        return getUrl() + JQTB_FILE_URL;
    }

    @Override
    public String getClassName() {
        return "OutRestfulDao";
    }

}
