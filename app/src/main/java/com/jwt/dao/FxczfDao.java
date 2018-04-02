package com.jwt.dao;

import android.text.TextUtils;

import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxcFileBean_;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.pojo.VioFxczfBean_;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * Created by lixiaodong on 2017/9/6.
 */

public class FxczfDao {
    public static ArrayList<VioFxcFileBean> queryFxczfFileByFId(long id, BoxStore boxStore) {
        Box<VioFxcFileBean> box = boxStore.boxFor(VioFxcFileBean.class);
        List<VioFxcFileBean> list = box.query().equal(VioFxcFileBean_.fxcId, id).build().find();
        ArrayList<VioFxcFileBean> res = new ArrayList<>();
        for (VioFxcFileBean f : list) {
            res.add(f);
        }
        return res;
    }

    public static String[] getLastWfdd(BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        long id = box.query().build().max(VioFxczfBean_.id);
        if (id <= 0)
            return null;
        VioFxczfBean f = box.query().equal(VioFxczfBean_.id, id).build().findUnique();
        String[] ar = new String[3];
        ar[0] = f.getXzqh() + f.getWfdd() + f.getLddm() + f.getDdms();
        ar[1] = f.getWfdz();
        ar[2] = f.getWfxw();
        return ar;
    }

    public static String getTodayFxczfId(BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        long row = box.query().build().max(VioFxczfBean_.id) + 1;
        row += 1000;
        String sr = String.valueOf(row);
        String dwdm = GlobalData.grxx.get(GlobalConstant.YBMBH);
        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String tzsbh = dwdm.substring(0, 6) + date + jybh.substring(4)
                + sr.substring(sr.length() - 3);
        return tzsbh;
    }

    public static long insertFxczfDb(VioFxczfBean fxczf, BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        box.put(fxczf);
        return fxczf.getId();
    }

    public static long insertFxcFile(VioFxcFileBean file, BoxStore boxStore) {
        Box<VioFxcFileBean> box = boxStore.boxFor(VioFxcFileBean.class);
        box.put(file);
        return file.getId();
    }

    public static void updateFxcUploaded(long id, BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        VioFxczfBean f = box.query().equal(VioFxczfBean_.id, id).build().findUnique();
        f.setScbj("1");
        box.put(f);
    }

    public static void updateXtbhScbj(long id, String xtbh, BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        VioFxczfBean f = box.query().equal(VioFxczfBean_.id, id).build().findUnique();
        f.setXtxh(xtbh);
        f.setScbj("1");
        box.put(f);
    }

    public static void delFxczf(long id, BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        box.remove(id);
    }


    public static boolean isCompleteUpload(VioFxczfBean fxc, BoxStore boxStore) {
        if (TextUtils.isEmpty(fxc.getScbj()) || TextUtils.equals("0", fxc.getScbj()))
            return false;

        return true;
    }

    public static List<VioFxczfBean> getFxczfByScbj(String scbj, int maxrow, BoxStore boxStore) {
        Box<VioFxczfBean> box = boxStore.boxFor(VioFxczfBean.class);
        QueryBuilder<VioFxczfBean> query = box.query();
        if ("1".equals(scbj)) {
            query = query.equal(VioFxczfBean_.scbj, "0").or().isNull(VioFxczfBean_.scbj);
        } else if ("2".equals(scbj)) {
            query = query.equal(VioFxczfBean_.scbj, "1");
        }
        Query<VioFxczfBean> b = query.build();
        long count = b.count();
        List<VioFxczfBean> list = (count > maxrow) ? b.find(count - maxrow, maxrow) : b.find();
        Collections.reverse(list);
        return list;
    }
}
