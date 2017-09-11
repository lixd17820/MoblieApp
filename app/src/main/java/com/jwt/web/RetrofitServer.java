package com.jwt.web;

import com.jwt.bean.KeyValueBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lixiaodong on 2017/9/3.
 */

public interface RetrofitServer {

    @GET("ydjw/services/ydjw/zhcxMenus")
    Call<String> getZhcxMenus(@Query("q") List<String> name);

}
