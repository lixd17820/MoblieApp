package com.jwt.dao;

import android.content.Context;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.SpringDjItf;
import com.jwt.bean.SpringKcdjBean;
import com.jwt.bean.SpringWhpdjBean;

import java.util.List;

import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/9/14.
 */

public class MessageDao {
    public MessageDao(Context self) {
    }

    public static KeyValueBean queryQymcByBy(String fwdwdm, BoxStore boxStore) {
        return null;
    }

    public static int getQymcCount(BoxStore boxStore) {
        return 0;
    }

    public static List<KeyValueBean> queryQymc(String s, BoxStore boxStore) {
        return null;
    }


    public static void addAllQymc(List<KeyValueBean> re, BoxStore boxStore) {
    }

    public static void delKcdjById(String id, BoxStore boxStore) {
    }

    public static void delWhpdjById(String id, BoxStore boxStore) {
    }

    public static SpringKcdjBean queryKcdjById(String id, BoxStore boxStore) {
        return null;
    }

    public static SpringWhpdjBean queryWhpdjById(String id, BoxStore boxStore) {
        return null;
    }

    public static void updateSpringScbj(String id, int djlx, BoxStore boxStore) {
    }

    public static List<SpringDjItf> getAllKcdj(BoxStore boxStore) {
        return null;
    }

    public static List<SpringDjItf> getAllWhpdj(BoxStore boxStore) {
        return null;
    }

    public static int getLastVioWfdd(int djlx, BoxStore boxStore) {
        return 0;
    }

    public static long insertSpringKcdj(SpringKcdjBean kcdj, BoxStore boxStore) {
        return 0;
    }

    public static long insertSpringWhpdj(SpringWhpdjBean whpdj, BoxStore boxStore) {
        return 0;
    }
}
