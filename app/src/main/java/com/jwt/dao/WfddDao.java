package com.jwt.dao;

import android.app.Activity;
import android.text.TextUtils;

import com.jwt.bean.KeyValueBean;
import com.jwt.pojo.FavorWfdd;
import com.jwt.pojo.FavorWfdd_;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadItem_;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.FrmRoadSeg_;
import com.jwt.pojo.SeriousStreetBean;
import com.jwt.pojo.SeriousStreetBean_;
import com.jwt.pojo.SysParaValue;
import com.jwt.pojo.SysParaValue_;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/8/28.
 */

public class WfddDao {
    public static void checkFavorWfld(Activity context) {
    }

    public static List<KeyValueBean> getOwnerXzqhList(String dwdm, BoxStore bs) {
        Box<SysParaValue> box = bs.boxFor(SysParaValue.class);
        SysParaValue v = box.query().equal(SysParaValue_.xtlb, "04").equal(SysParaValue_.glbm, dwdm)
                .equal(SysParaValue_.gjz, "GLXZQH").build().findFirst();
        List<KeyValueBean> kvs = new ArrayList<>();
        if (v != null) {
            String[] ar = v.getCsz().split(",");
            if (ar != null) {
                for (String s : ar) {
                    KeyValueBean kv = GlobalMethod.findKvByKvlist(GlobalData.xzqhList, s);
                    if (kv != null)
                        kvs.add(kv);
                }
            }
        }
        return kvs;

    }

    public static List<KeyValueBean> getRoadItemsByXzqh(String xzqh, BoxStore bs) {
        Box<FrmRoadItem> box = bs.boxFor(FrmRoadItem.class);
        List<FrmRoadItem> list = box.query().contains(FrmRoadItem_.xzqh, xzqh).build().find();
        List<KeyValueBean> kvs = new ArrayList<>();
        kvs.add(new KeyValueBean("", ""));
        if (list != null) {
            for (FrmRoadItem r : list) {
                KeyValueBean kv = new KeyValueBean(r.getDldm(), r.getDlmc());
                kvs.add(kv);
            }
        }
        return kvs;
    }

    public static boolean isGsd(String road) {
        if (TextUtils.isEmpty(road))
            return false;
        char c = road.charAt(0);
        return Integer.valueOf(c) < 53;
    }

    public static List<KeyValueBean> getRoadSegByRoad(String road, String xzqh, BoxStore bs) {
        Box<FrmRoadSeg> box = bs.boxFor(FrmRoadSeg.class);
        List<FrmRoadSeg> list = box.query().equal(FrmRoadSeg_.xzqh, xzqh).equal(FrmRoadSeg_.dldm, road).build().find();
        List<KeyValueBean> kvs = new ArrayList<>();
        kvs.add(new KeyValueBean("", ""));
        if (list != null) {
            for (FrmRoadSeg r : list) {
                KeyValueBean kv = new KeyValueBean(r.getLddm(), r.getLdmc());
                kvs.add(kv);
            }
        }
        return kvs;
    }

    public static int addFavorWfdd(FavorWfdd dd, BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        long i = box.query().equal(FavorWfdd_.favorLdmc, dd.getFavorLdmc())
                .build().count();
        if (i > 0)
            return -10;
        box.put(dd);
        return 1;
    }

    public static void delFavorWfddById(long id, BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        box.remove(id);
    }

    public static List<FavorWfdd> getAllFavorWfdd(BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        return box.query().build().find();
    }

    public static boolean isWfddOk(String wfdd, BoxStore bs) {
        return true;
    }

    public static boolean checkIsSeriousStreet(String wfdd, BoxStore boxStore) {
        Box<SeriousStreetBean> box = boxStore.boxFor(SeriousStreetBean.class);
        long c = box.query().equal(SeriousStreetBean_.wfdd, wfdd).build().count();
        return c > 0;
    }

    public static String[] getDlmx(String xzqh, String lh, BoxStore boxStore) {
        return null;
    }
}
